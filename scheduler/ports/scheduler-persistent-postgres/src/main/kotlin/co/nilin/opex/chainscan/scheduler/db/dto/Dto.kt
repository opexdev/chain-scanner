package co.nilin.opex.chainscan.scheduler.db.dto

import co.nilin.opex.chainscan.scheduler.core.po.*
import co.nilin.opex.chainscan.scheduler.db.model.*

fun ChainModel.toPlainObject() = Chain(name)

fun Chain.toModel() = ChainModel(name)

fun ChainScannerModel.toPlainObject() = ChainScanner(
    chainName,
    url,
    maxBlockRange,
    delayOnRateLimit,
    id
)

fun ChainScanner.toModel() = ChainScannerModel(
    chainName,
    url,
    maxBlockRange,
    delayOnRateLimit,
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
    blockNumber,
    retries,
    maxRetries,
    synced,
    giveUp,
    error,
    id
)

fun ChainSyncRetry.toModel() = ChainSyncRetryModel(
    chain,
    blockNumber,
    retries,
    maxRetries,
    synced,
    giveUp,
    error,
    id
)

fun ChainSyncScheduleModel.toPlainObject() = ChainSyncSchedule(
    chain,
    executeTime,
    delay,
    errorDelay,
    timeout,
    enabled,
    confirmations,
    maxRetries,
    id
)

fun ChainSyncSchedule.toModel() = ChainSyncScheduleModel(
    chainName,
    executeTime,
    delay,
    errorDelay,
    timeout,
    enabled,
    confirmations,
    maxRetries,
    id
)
