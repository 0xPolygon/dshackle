package io.emeraldpay.dshackle.upstream.bitcoin

import io.emeraldpay.dshackle.upstream.Capability
import io.emeraldpay.dshackle.upstream.bitcoin.data.SimpleUnspent
import org.bitcoinj.core.Address
import io.klogging.noCoLogger
import reactor.core.publisher.Mono

class CurrentUnspentReader(
    upstreams: BitcoinMultistream,
    esploraClient: EsploraClient?,
) : UnspentReader {

    companion object {
        private val log = noCoLogger(CurrentUnspentReader::class)
    }

    private val delegate: UnspentReader = if (esploraClient != null) {
        EsploraUnspentReader(esploraClient)
    } else if (upstreams.upstreams.any { it.isGrpc() && it.getCapabilities().contains(Capability.BALANCE) }) {
        RemoteUnspentReader(upstreams)
    } else {
        RpcUnspentReader(upstreams)
    }

    override fun read(key: Address): Mono<List<SimpleUnspent>> {
        return delegate.read(key)
    }
}
