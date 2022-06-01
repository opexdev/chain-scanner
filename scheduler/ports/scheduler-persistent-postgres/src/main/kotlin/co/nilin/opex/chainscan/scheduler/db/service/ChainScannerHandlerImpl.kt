package co.nilin.opex.chainscan.scheduler.db.service

import co.nilin.opex.chainscan.scheduler.core.spi.ChainScannerHandler
import co.nilin.opex.chainscan.scheduler.db.dto.toPlainObject
import co.nilin.opex.chainscan.scheduler.db.repository.ChainScannerRepository
import co.nilin.opex.chainscan.scheduler.core.po.ChainScanner
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class ChainScannerHandlerImpl(private val chainScannerRepository: ChainScannerRepository) : ChainScannerHandler {
    override suspend fun getScannersByName(chainName: String): List<ChainScanner> {
        return chainScannerRepository.findByChainName(chainName).map { it.toPlainObject() }.collectList().awaitSingle()
    }
}
