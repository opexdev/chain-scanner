package co.nilin.opex.chainscan.bitcoin.data

data class BlockResponse(
    val hash: String,
    val height: Int,
    val tx: List<TransactionResponse>
)