package co.nilin.opex.chainscan.database.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.spi.TransferCacheHandler
import co.nilin.opex.chainscan.database.model.TransferModel
import co.nilin.opex.chainscan.database.repository.TransferRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class TransferCacheHandlerImpl(
    private val transferRepository: TransferRepository
) : TransferCacheHandler {
    override suspend fun saveTransfers(transfers: List<Transfer>) {
        transferRepository.saveAll(transfers.map {
            TransferModel(
                it.txHash,
                it.blockNumber,
                it.from.address,
                it.from.memo,
                it.to.address,
                it.to.memo,
                it.isTokenTransfer,
                it.amount,
                it.chain,
                it.tokenAddress
            )
        })
    }

    override suspend fun getTransfers(tokenAddresses: List<String>): List<Transfer> {
        val transfers =
            if (tokenAddresses.isEmpty()) transferRepository.findAllNotTokenTransfers()
            else transferRepository.findByTokenAddress(tokenAddresses)
        return transfers.map {
            Transfer(
                it.txHash,
                it.blockNumber,
                Wallet(it.fromAddress, it.fromMemo),
                Wallet(it.toAddress, it.toAddress),
                it.isTokenTransfer,
                it.amount,
                it.chain,
                it.tokenAddress,
                it.id!!
            )
        }.collectList().awaitSingle()
    }

    override suspend fun clearCache(blockNumber: BigInteger) {
        transferRepository.clearCache(blockNumber)
    }
}
