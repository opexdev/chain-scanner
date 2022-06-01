package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.po.ChainScanner
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainSyncJob(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String,
) : SyncJob {
    override suspend fun startJob(sch: ChainSyncSchedule) {
        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
        val currentBlockNumber = scannerProxy.getBlockNumber(chain.url)
        val head = currentBlockNumber - chain.confirmations.toBigInteger()
        val startBlockNumber =
            chainSyncRecordHandler.lastSyncedBlockedNumber(sch.chainName)?.plus(BigInteger.ONE)
                ?: head
        val endBlockNumber = head.max(startBlockNumber)
        val blockRange = startBlockNumber.toLong()..endBlockNumber.toLong()
        runCatching {
            coroutineScope {
                blockRange.chunked(chain.maxBlockRange).forEach { br ->
                    br.forEach { bn ->
                        launch { fetch(chain, bn.toBigInteger()) }
                    }
                }
            }
            sch.nextSchedule(sch.delay)
        }.onFailure { e ->
            when (e) {
                is WebClientResponseException -> e.takeIf { it.rawStatusCode == 429 }
                    ?.run { sch.nextSchedule(chain.rateLimitDelay.toLong()) }
                is Exception -> sch.nextSchedule(sch.errorDelay)
            }
        }
    }

    private suspend fun ChainSyncSchedule.nextSchedule(nextTimeDiff: Long) {
        val retryTime = LocalDateTime.now().plus(nextTimeDiff, ChronoUnit.SECONDS)
        chainSyncSchedulerHandler.save(copy(retryTime = retryTime))
    }

    private suspend fun fetch(chain: ChainScanner, blockNumber: BigInteger) {
        runCatching {
            val response = scannerProxy.getTransfers(chain.url, blockNumber)
            webhookCaller.callWebhook(onSyncWebhookUrl, response.transfers)
            val record = chainSyncRecordHandler.lastSyncRecord(chain.chainName)
            chainSyncRecordHandler.saveSyncRecord(
                record?.copy(syncTime = LocalDateTime.now(), blockNumber = response.blockNumber)
                    ?: ChainSyncRecord(chain.chainName, LocalDateTime.now(), response.blockNumber)
            )
        }.onFailure {
            chainSyncRetryHandler.save(ChainSyncRetry(chain.chainName, blockNumber))
        }
    }
}