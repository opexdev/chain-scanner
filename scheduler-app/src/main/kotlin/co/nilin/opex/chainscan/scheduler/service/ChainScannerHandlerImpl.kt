package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainScannerHandler
import co.nilin.opex.chainscan.scheduler.dto.toPlainObject
import co.nilin.opex.chainscan.scheduler.po.ChainScanner
import co.nilin.opex.chainscan.scheduler.repository.ChainScannerRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class ChainScannerHandlerImpl(private val chainScannerRepository: ChainScannerRepository) : ChainScannerHandler {
    override suspend fun getScannersByName(chainName: String): List<ChainScanner> {
        return chainScannerRepository.findByChainName(chainName).map { it.toPlainObject() }.collectList().awaitSingle()
    }

}