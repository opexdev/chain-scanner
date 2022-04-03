package co.nilin.opex.chainscan.tron.data

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionResponse(
    val txID: String,
    @JsonProperty("raw_data")
    val rawData: TXRawData,
    @JsonProperty("raw_data_hex")
    val rawDataHex: String,
)

data class TXRawData(
    val contract: List<Contract>,
    val timestamp: Long,
)

data class Contract(
    val parameter: Parameter,
    val type: String, //TransactionType
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
    @JsonProperty("contract_address")
    val contractAddress: String?,
)
