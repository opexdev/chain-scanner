package co.nilin.opex.chainscan.tron.data

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionResponse(
    val txID: String,
    @JsonProperty("raw_data")
    val rawData: TXRawData,
    @JsonProperty("raw_data_hex")
    val rawDataHex: String,
) {

    fun from(): String? {
        return rawData.contract.find { it.type == TransactionType.TransferContract }?.parameter?.value?.from
    }

    fun to(): String? {
        return rawData.contract.find { it.type == TransactionType.TransferContract }?.parameter?.value?.to
    }

    fun amount(): Long? {
        return rawData.contract.find { it.type == TransactionType.TransferContract }?.parameter?.value?.amount
    }

    fun isTransfer(): Boolean {
        return rawData.contract.find { it.type == TransactionType.TransferContract } != null
    }

}

data class TXRawData(
    val contract: List<Contract>,
    val timestamp: Long,
)

data class Contract(
    val parameter: Parameter,
    val type: TransactionType,
)

data class Parameter(
    val value: ParamValue,
    @JsonProperty("type_url")
    val typeUrl: String?,
)

data class ParamValue(
    val amount: Long?,
    @JsonProperty("owner_address")
    val from: String?,
    @JsonProperty("to_address")
    val to: String?,
    val data: String?,
    @JsonProperty("owner_address")
    val contractAddress: String?,
)
