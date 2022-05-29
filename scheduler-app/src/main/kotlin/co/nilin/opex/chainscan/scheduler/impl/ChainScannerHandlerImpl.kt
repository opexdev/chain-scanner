package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainScannerHandler
import co.nilin.opex.chainscan.scheduler.po.ChainScanner
import co.nilin.opex.chainscan.scheduler.repository.ChainScannerRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class ChainScannerHandlerImpl(private val chainScannerRepository: ChainScannerRepository) : ChainScannerHandler {
    override suspend fun getScannersByName(chainName: String): List<ChainScanner> {
        return chainScannerRepository.findByChainName(chainName).map {
            ChainScanner(
                it.chainName,
                it.url,
                it.maxBlockRange,
                it.id
            )
        }.collectList().awaitSingle()
    }

}