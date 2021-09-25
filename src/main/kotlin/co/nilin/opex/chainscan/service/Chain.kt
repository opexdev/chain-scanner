package co.nilin.opex.chainscan.service

import co.nilin.opex.chainscan.model.Transfer

interface Chain {
    fun getTransfers(startBlock: Long, endBlock: Long, addresses: List<String>): List<Transfer>
}
