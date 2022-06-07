package co.nilin.opex.chainscanner.bitcoin.data

import com.fasterxml.jackson.annotation.JsonProperty

data class BlockHashResponse(
    @JsonProperty("blockhash")
    val blockHash:String
)
