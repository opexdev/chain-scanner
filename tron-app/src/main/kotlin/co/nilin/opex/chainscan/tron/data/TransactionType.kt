package co.nilin.opex.chainscan.tron.data

enum class TransactionType {
    TriggerSmartContract,
    TransferContract,
    AccountCreateContract,
    UnfreezeBalanceContract,
    TransferAssetContract,
    WithdrawBalanceContract,
    VoteWitnessContract
}
