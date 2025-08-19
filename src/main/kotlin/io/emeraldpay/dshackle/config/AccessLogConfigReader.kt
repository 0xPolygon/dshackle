package io.emeraldpay.dshackle.config

import io.klogging.noCoLogger
import org.yaml.snakeyaml.nodes.MappingNode

class AccessLogConfigReader : YamlConfigReader(), ConfigReader<AccessLogConfig> {

    companion object {
        private val log = noCoLogger(AccessLogConfigReader::class)
    }

    override fun read(input: MappingNode?): AccessLogConfig {
        return getMapping(input, "access-log", "accessLog", "egress-log", "egressLog")?.let { node ->
            val enabled = getValueAsBool(node, "enabled") ?: false
            if (!enabled) {
                AccessLogConfig.disabled()
            } else {
                val includeMessages = getValueAsBool(node, "include-messages") ?: false
                val config = AccessLogConfig(true, includeMessages)
                LogTargetConfigReader(AccessLogConfig.defaultFile).read(node)?.let {
                    config.target = it
                }
                config
            }
        } ?: AccessLogConfig.default()
    }
}
