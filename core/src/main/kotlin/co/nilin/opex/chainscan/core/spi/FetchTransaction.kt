package co.nilin.opex.chainscan.core.spi

interface FetchTransaction<T> {
    suspend fun getTransactions(blockRange: LongRange): List<T>
}
