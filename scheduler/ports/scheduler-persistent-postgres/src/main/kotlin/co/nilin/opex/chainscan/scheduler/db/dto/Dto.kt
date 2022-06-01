package co.nilin.opex.chainscan.scheduler.db.dto

import co.nilin.opex.chainscan.scheduler.core.po.*
import co.nilin.opex.chainscan.scheduler.db.model.*

fun ChainModel.toPlainObject() = Chain(name)

fun Chain.toModel() = ChainModel(name)

fun ChainScannerModel.toPlainObject() = ChainScanner(
    chainName,
    url,
    maxBlockRange,
    confirmations,
    rateLimitDelay,
    id
)

fun ChainScanner.toModel() = ChainScannerModel(
    chainName,
    url,
    maxBlockRange,
    confirmations,
    rateLimitDelay,
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
    synced,
    giveUp,
    error,
    id
)

fun ChainSyncRetry.toModel() = ChainSyncRetryModel(
    chain,
    blockNumber,
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
    timeout,
    enabled,
    id
)

fun ChainSyncSchedule.toModel() = ChainSyncScheduleModel(
    chainName,
    retryTime,
    delay,
    errorDelay,
    timeout,
    enabled,
    id
)
