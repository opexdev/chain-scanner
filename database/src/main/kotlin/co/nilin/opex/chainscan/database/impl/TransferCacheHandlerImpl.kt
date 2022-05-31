package co.nilin.opex.chainscan.database.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.TransferCacheHandler
import co.nilin.opex.chainscan.database.dto.toModel
import co.nilin.opex.chainscan.database.dto.toPlainObject
import co.nilin.opex.chainscan.database.repository.TransferRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class TransferCacheHandlerImpl(
    private val transferRepository: TransferRepository
) : TransferCacheHandler {
    override suspend fun saveTransfers(transfers: List<Transfer>) {
        transferRepository.saveAll(transfers.map { it.toModel() })
    }

    override suspend fun getTransfers(tokenAddresses: List<String>, blockNumber: BigInteger?): List<Transfer> {
        val transfers =
            if (tokenAddresses.isEmpty()) transferRepository.findAllNotTokenTransfers()
            else transferRepository.findByTokenAddress(tokenAddresses)
        return transfers.map { it.toPlainObject() }.collectList().awaitSingle()
    }

    override suspend fun clearCache(blockNumber: BigInteger) {
        transferRepository.clearCache(blockNumber)
    }
}
