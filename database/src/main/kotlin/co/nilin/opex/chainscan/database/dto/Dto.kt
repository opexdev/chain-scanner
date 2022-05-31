package co.nilin.opex.chainscan.database.dto

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.model.WatchList
import co.nilin.opex.chainscan.database.model.TransferModel
import co.nilin.opex.chainscan.database.model.WatchListModel

fun TransferModel.toPlainObject() = Transfer(
    txHash,
    blockNumber,
    Wallet(fromAddress, fromMemo),
    Wallet(toAddress, toMemo),
    isTokenTransfer,
    amount,
    chain,
    tokenAddress,
    id
)

fun Transfer.toModel() = TransferModel(
    txHash,
    blockNumber,
    from.address,
    from.memo,
    to.address,
    to.memo,
    isTokenTransfer,
    amount,
    chain,
    tokenAddress,
    id
)

fun WatchListModel.toPlainObject() = WatchList(address, id)

fun WatchList.toModel() = WatchListModel(address, id)
