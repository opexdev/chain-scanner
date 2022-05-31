package co.nilin.opex.chainscan.scheduler.dto

import co.nilin.opex.chainscan.scheduler.model.*
import co.nilin.opex.chainscan.scheduler.po.*

fun ChainModel.toPlainObject() = Chain(name)

fun Chain.toModel() = ChainModel(name)

fun ChainScannerModel.toPlainObject() = ChainScanner(
    chainName,
    url,
    maxBlockRange,
    confirmations,
    id
)

fun ChainScanner.toModel() = ChainScannerModel(
    chainName,
    url,
    maxBlockRange,
    confirmations,
    id
)

fun ChainSyncRecordModel.toPlainObject() = ChainSyncRecord(
    chain,
    syncTime,
    blockNumber,
    id
)

fun ChainSyncRecord.toModel() = ChainSyncRecordModel(
    chain,
    syncTime,
    blockNumber,
    id
)

fun ChainSyncRetryModel.toPlainObject() = ChainSyncRetry(
    chain,
    startBlock,
    endBlock,
    retries,
    synced,
    giveUp,
    error,
    id
)

fun ChainSyncRetry.toModel() = ChainSyncRetryModel(
    chain,
    startBlock,
    endBlock,
    retries,
    synced,
    giveUp,
    error,
    id
)

fun ChainSyncScheduleModel.toPlainObject() = ChainSyncSchedule(
    chain,
    retryTime,
    delay,
    errorDelay,
    enabled,
    id
)

fun ChainSyncSchedule.toModel() = ChainSyncScheduleModel(
    chainName,
    retryTime,
    delay,
    errorDelay,
    enabled,
    id
)
