package co.nilin.opex.chainscan.core.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Wallet(val address: String, val memo: String? = null)
