package co.nilin.opex.chainscanner.tron.data

enum class TransactionType {
    TriggerSmartContract,
    TransferContract,
    AccountCreateContract,
    UnfreezeBalanceContract,
    TransferAssetContract,
    WithdrawBalanceContract,
    VoteWitnessContract
}
