package io.emeraldpay.dshackle.test

import io.emeraldpay.dshackle.data.BlockContainer
import io.emeraldpay.dshackle.upstream.Head
import io.klogging.noCoLogger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class FixedHead(
    var height: Long? = null,
    var block: BlockContainer? = null,
) : Head {

    companion object {
        private val log = noCoLogger(FixedHead::class)
    }

    override fun getFlux(): Flux<BlockContainer> {
        return Flux.from(Mono.justOrEmpty(block))
    }

    override fun onBeforeBlock(handler: Runnable) {
    }

    override fun getCurrentHeight(): Long? {
        return height ?: block?.height
    }
}
