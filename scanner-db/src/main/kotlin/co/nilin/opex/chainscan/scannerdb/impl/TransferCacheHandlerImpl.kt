package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.spi.TransferCacheHandler
import co.nilin.opex.chainscan.scannerdb.model.TransferModel
import co.nilin.opex.chainscan.scannerdb.repository.TransferRepository
import kotlinx.coroutines.reactor.awaitSingle
import java.math.BigInteger

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
        return transferRepository.findByTokenAddress(tokenAddresses).map {
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
