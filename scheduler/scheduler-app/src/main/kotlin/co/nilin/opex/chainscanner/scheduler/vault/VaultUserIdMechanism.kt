package co.nilin.opex.chainscanner.scheduler.vault

import org.springframework.vault.authentication.AppIdUserIdMechanism

class VaultUserIdMechanism() : AppIdUserIdMechanism {
    override fun createUserId(): String {
        return System.getenv("BACKEND_USER")
    }
}