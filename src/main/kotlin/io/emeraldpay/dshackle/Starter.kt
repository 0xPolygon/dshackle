/**
 * Copyright (c) 2020 EmeraldPay, Inc
 * Copyright (c) 2019 ETCDEV GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.emeraldpay.dshackle

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.noCoLogger
import io.klogging.sending.STDOUT
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import reactor.core.publisher.Hooks
import reactor.tools.agent.ReactorDebugAgent
import io.klogging.rendering.RenderString
import io.klogging.rendering.serializeMap

@SpringBootApplication(scanBasePackages = ["io.emeraldpay.dshackle"])
@Import(Config::class)
open class Starter

private val log = noCoLogger(Starter::class)

val RENDER_TEST: RenderString =
    RenderString { event ->
        val eventMap: MutableMap<String, Any?> =
            (
                mapOf(
                    "@t" to event.timestamp.toString(),
                    "@l" to event.level.name,
                ) + event.items
                ).toMutableMap()
//        if (event.context != null) eventMap["context"] = event.context
        if (event.template != null) {
            eventMap["@mt"] = event.template
        } else {
            eventMap["@m"] = event.message
        }
        if (event.stackTrace != null) eventMap["@x"] = event.stackTrace

        serializeMap(eventMap, omitNullValues = false)
    }

fun main(args: Array<String>) {
    loggingConfiguration {
        sink("stdout", RENDER_TEST, STDOUT)

        logging {
            fromLoggerBase("io.emeraldpay")
            fromMinLevel(Level.INFO) {
                toSink("stdout")
            }
        }
    }

    val app = SpringApplication(Starter::class.java)
    app.setBannerMode(Banner.Mode.OFF)
    app.setLogStartupInfo(false)

    //
    // Reactor Debug Agent adds a Java Agent to get better stacktraces.
    // It doesn't add an overhead as per https://projectreactor.io/docs/core/release/reference/#reactor-tools-debug
    // But in some cases users may want to disable it, therefore --disable-debug-agent option
    if (!args.contains("--disable-debug-agent")) {
        ReactorDebugAgent.init()
    }

    Hooks.onErrorDropped { t ->
        if (t is SilentException) {
            log.warn(t.message)
        } else {
            log.error("UNHANDLED ERROR. HOOK CALLED", t)
        }
    }

    app.run(*args)
}
