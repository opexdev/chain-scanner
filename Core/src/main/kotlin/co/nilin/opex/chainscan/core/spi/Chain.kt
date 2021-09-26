package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Transfer

interface Chain {

    fun getTransfers(startBlock: Long, endBlock: Long, addresses: List<String>): List<Transfer>

}
