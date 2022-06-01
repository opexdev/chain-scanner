package co.nilin.opex.chainscan.database.dto

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.model.WatchListItem
import co.nilin.opex.chainscan.database.dao.TransferModel
import co.nilin.opex.chainscan.database.dao.WatchListItemModel

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

fun WatchListItemModel.toPlainObject() = WatchListItem(address, id)

fun WatchListItem.toModel() = WatchListItemModel(address, id)
