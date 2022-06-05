package co.nilin.opex.chainscanner.database.dto

import co.nilin.opex.chainscanner.core.model.Transfer
import co.nilin.opex.chainscanner.core.model.Wallet
import co.nilin.opex.chainscanner.core.model.WatchListItem
import co.nilin.opex.chainscanner.database.model.TransferModel
import co.nilin.opex.chainscanner.database.model.WatchListItemModel

fun TransferModel.toPlainObject() = Transfer(
    txHash,
    blockNumber,
    Wallet(receiverAddress, receiverMemo),
    isTokenTransfer,
    amount,
    chain,
    tokenAddress,
    id
)

fun Transfer.toModel() = TransferModel(
    txHash,
    blockNumber,
    receiver.address,
    receiver.memo,
    isTokenTransfer,
    amount,
    chain,
    tokenAddress,
    id
)

fun WatchListItemModel.toPlainObject() = WatchListItem(symbol, name, address, id)

fun WatchListItem.toModel() = WatchListItemModel(symbol, name, address, id)
