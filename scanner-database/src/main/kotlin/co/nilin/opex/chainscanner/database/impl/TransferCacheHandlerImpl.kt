package co.nilin.opex.chainscanner.database.impl

import co.nilin.opex.chainscanner.core.model.Transfer
import co.nilin.opex.chainscanner.core.spi.TransferCacheHandler
import co.nilin.opex.chainscanner.database.dto.toModel
import co.nilin.opex.chainscanner.database.dto.toPlainObject
import co.nilin.opex.chainscanner.database.repository.TransferRepository
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
        transferRepository.clearCache(blockNumber).awaitSingle()
    }
}
