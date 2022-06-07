package co.nilin.opex.chainscanner.bitcoin.data

data class BlockResponse(
    val hash: String,
    val height: Int,
    val tx: List<TransactionResponse>
)
