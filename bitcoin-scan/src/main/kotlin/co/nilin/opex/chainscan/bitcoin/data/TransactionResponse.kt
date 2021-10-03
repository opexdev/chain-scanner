package co.nilin.opex.chainscan.bitcoin.data

import java.math.BigDecimal

data class TransactionResponse(
    val hash: String,
    val vin: List<ValueIn>,
    val vout: List<ValueOut>
)

data class ValueIn(
    val txid: String?
)

data class ValueOut(
    val value: BigDecimal,
    val n: Int,
    val scriptPubKey: ScriptedPubKey?
)

data class ScriptedPubKey(
    val asm: String?,
    val hex: String?,
    val address: String?,
    val type: String?,
)
