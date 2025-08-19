package io.emeraldpay.dshackle.upstream.bitcoin

import io.emeraldpay.dshackle.reader.DshackleRpcReader
import io.emeraldpay.dshackle.upstream.rpcclient.DshackleRequest
import io.emeraldpay.dshackle.upstream.rpcclient.DshackleResponse
import io.klogging.noCoLogger
import reactor.core.publisher.Mono

class NormalizingReader(
    private val delegate: DshackleRpcReader
) : DshackleRpcReader {

    companion object {
        private val log = noCoLogger(NormalizingReader::class)
    }

    override fun read(key: DshackleRequest): Mono<DshackleResponse> {
        return delegate.read(key)
    }
}
