package co.nilin.opex.chainscanner.tron.data

import com.fasterxml.jackson.annotation.JsonProperty

data class BlockResponse(
    val blockID: String?,
    @JsonProperty("block_header")
    val blockHeader: BlockHeader?,
    val transactions: List<TransactionResponse>
)

data class BlockHeader(
    @JsonProperty("raw_data")
    val rawData: BlockHeaderRawData?,
    @JsonProperty("witness_signature")
    val witnessSignature: String?
)

data class BlockHeaderRawData(
    val number: Long,
    val txTrieRoot: String?,
    @JsonProperty("witness_address")
    val witnessAddress: String?,
    val parentHash: String?,
    val version: Int,
    val timestamp: Long
)

