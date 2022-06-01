package co.nilin.opex.chainscan.scheduler.jobs

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.po.ChainScanner
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SyncLatestTransfersScheduledJob(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String,
) : ScheduledJob {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
        val currentBlockNumber = scannerProxy.getBlockNumber(chain.url)
        val head = currentBlockNumber - chain.confirmations.toBigInteger()
        val startBlockNumber =
            chainSyncRecordHandler.lastSyncedBlockedNumber(sch.chainName)?.plus(BigInteger.ONE)
                ?: head
        val endBlockNumber = head.max(startBlockNumber)
        val blockRange = startBlockNumber.toLong()..endBlockNumber.toLong()
        logger.trace("Fetch transfers on block range: $startBlockNumber - $endBlockNumber")
        runCatching {
            coroutineScope {
                val br = blockRange.chunked(chain.maxBlockRange).first()
                br.forEach { bn ->
                    launch { fetch(chain, bn.toBigInteger()) }
                }
            }
            sch.nextSchedule(sch.delay)
        }.onFailure { e ->
            when (e) {
                is WebClientResponseException -> e.takeIf { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
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
            scannerProxy.getTransfers(chain.url, blockNumber)
        }.onFailure { e ->
            chainSyncRetryHandler.findByChainAndBlockNumber(chain.chainName, blockNumber)?.let {
                chainSyncRetryHandler.save(it.copy(error = e.message))
            } ?: chainSyncRetryHandler.save(ChainSyncRetry(chain.chainName, blockNumber, error = e.message))
        }.mapCatching { response ->
            webhookCaller.callWebhook("$onSyncWebhookUrl/${chain.chainName}", response)
            val record = chainSyncRecordHandler.lastSyncRecord(chain.chainName)
            chainSyncRecordHandler.saveSyncRecord(
                record?.copy(syncTime = LocalDateTime.now(), blockNumber = blockNumber)
                    ?: ChainSyncRecord(chain.chainName, LocalDateTime.now(), blockNumber)
            )
        }
    }
}
