package io.emeraldpay.dshackle.upstream

import io.emeraldpay.dshackle.reader.DshackleRpcReader
import io.emeraldpay.dshackle.upstream.calls.CallMethods
import io.emeraldpay.dshackle.upstream.rpcclient.DshackleRequest
import io.emeraldpay.dshackle.upstream.rpcclient.DshackleResponse
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class HardcodedReader(
    private val methods: AtomicReference<CallMethods>,
) : DshackleRpcReader {
    override fun read(key: DshackleRequest): Mono<DshackleResponse> {
        val current = methods.get()
        if (current.isHardcoded(key.method)) {
            return Mono.just(current.executeHardcoded(key.method))
                .map { DshackleResponse(key.id, it, null) }
        }
        return Mono.empty()
    }
}
