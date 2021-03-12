/*
 * PaiCoin-RPC-Client-Kotlin License
 *
 * Copyright (c) 2021, JinHua.IO. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.paicoin.rpcclient

import com.googlecode.jsonrpc4j.JsonRpcMethod

interface JvmPaicoinRpcClient : PaicoinRpcClient{

    @JsonRpcMethod("abandontransaction")
    override fun abandonTransaction(transactionId: String)

    @JsonRpcMethod("abortrescan")
    override fun abortRescan()

    @JsonRpcMethod("addmultisigaddress")
    override fun addMultiSigAddress(required: Int?, keys: List<String>): String

    @JsonRpcMethod("addnode")
    override fun addNode(address: String, operation: NodeListOperation)

    @JsonRpcMethod("backupwallet")
    override fun backupWallet(destination: String)

    @JsonRpcMethod("clearbanned")
    override fun clearBanned()

    @JsonRpcMethod("createmultisig")
    override fun createMultiSig(required: Int, keys: List<String>): MultiSigAddress

    @JsonRpcMethod("createrawtransaction")
    override fun createRawTransaction(
        inputs: List<OutPoint>,
        outputs: Map<String, Double>,
        lockTime: Int?,
        replaceable: Boolean?
    ): String

    @JsonRpcMethod("decoderawtransaction")
    override fun decodeRawTransaction(transactionId: String): Transaction

    @JsonRpcMethod("decodescript")
    override fun decodeScript(scriptHex: String): DecodedScript

    @JsonRpcMethod("disconnectnode")
    override fun disconnectNode(nodeAddress: String?, nodeId: Int?)

    @JsonRpcMethod("dumpprivkey")
    override fun dumpPrivateKey(address: String): String

    @JsonRpcMethod("dumpwallet")
    override fun dumpWallet(filename: String): Map<*, *>

    @JsonRpcMethod("encryptwallet")
    override fun encryptWallet(passphrase: String)

    @JsonRpcMethod("walletpassphrasechange")
    override fun walletPassphraseChange(oldPassphrase: String,newPassphrase: String)

    @JsonRpcMethod("walletpassphrase")
    override fun walletPassphrase(passphrase: String,timeout: Long)

    @JsonRpcMethod("walletlock")
    override fun walletLock()

    @JsonRpcMethod("generate")
    override fun generate(numberOfBlocks: Int, maxTries: Int?): List<String>

    @JsonRpcMethod("getaddednodeinfo")
    override fun getAddedNodeInfo(): List<AddedNodeInfo>

    @JsonRpcMethod("getbalance")
    override fun getBalance(
            account: String,
            minconf: Int,
            includeWatchOnly: Boolean): Double

    @JsonRpcMethod("getbestblockhash")
    override fun getBestBlockhash(): String

    @JsonRpcMethod("getblock")
    override fun getBlockData(blockHash: String, verbosity: Int): String

    @JsonRpcMethod("getblock")
    override fun getBlock(blockHash: String, verbosity: Int): BlockInfo

    @JsonRpcMethod("getblock")
    override fun getBlockWithTransactions(blockHash: String, verbosity: Int): BlockInfoWithTransactions

    @JsonRpcMethod("getinfo")
    override fun getInfo(): Info

    @JsonRpcMethod("getblockchaininfo")
    override fun getBlockchainInfo(): BlockChainInfo

    @JsonRpcMethod("getblockcount")
    override fun getBlockCount(): Int

    @JsonRpcMethod("getblockhash")
    override fun getBlockHash(height: Int): String

    @JsonRpcMethod("getblockheader")
    override fun getBlockHeader(blockHash: String, verbose: Boolean?): Any

    @JsonRpcMethod("getblocktemplate")
    override fun getBlockTemplate(blockTemplateRequest: BlockTemplateRequest?)

    @JsonRpcMethod("getchaintips")
    override fun getChainTips(): List<ChainTip>

    @JsonRpcMethod("getchaintxstats")
    override fun getChainTransactionStats(
            blockWindowSize: Int?,
            blockHashEnd: String?
    ): ChainTransactionStats

    @JsonRpcMethod("getconnectioncount")
    override fun getConnectionCount(): Int

    @JsonRpcMethod("getdifficulty")
    override fun getDifficulty(): Double

    @JsonRpcMethod("getmemoryinfo")
    override fun getMemoryInfo(): Any

    @JsonRpcMethod("getmempoolancestors")
    override fun getMempoolAncestors(transactionId: String): Any

    @JsonRpcMethod("getmempooldescendants")
    override fun getMempoolDescendants(): Any

    @JsonRpcMethod("getmempoolentry")
    override fun getMempoolEntry(transactionId: String): Map<*, *>

    @JsonRpcMethod("getmempoolinfo")
    override fun getMempoolInfo(): MemPoolInfo

    @JsonRpcMethod("getmininginfo")
    override fun getMiningInfo(): MiningInfo

    @JsonRpcMethod("getnettotals")
    override fun getNetworkTotals(): NetworkTotals

    @JsonRpcMethod("getnetworkhashps")
    override fun getNetworkHashesPerSeconds(lastBlocks: Int, height: Int): Long

    @JsonRpcMethod("getnetworkinfo")
    override fun getNetworkInfo(): NetworkInfo

    @JsonRpcMethod("getnewaddress")
    override fun getNewAddress(): String

    @JsonRpcMethod("getnewaddress")
    override fun getNewAddress(account: String): String

    @JsonRpcMethod("getpeerinfo")
    override fun getPeerInfo(): List<PeerInfo>

    @JsonRpcMethod("getrawchangeaddress")
    override fun getRawChangeAddress(): String

    @JsonRpcMethod("getrawmempool")
    override fun getRawMemPool(verbose: Boolean): List<Map<*, *>>

    @JsonRpcMethod("getrawtransaction")
    override fun getRawTransaction(transactionId: String, verbosity: Int): Transaction

    @JsonRpcMethod("getreceivedbyaddress")
    override fun getReceivedByAddress(address: String, minConfirmations: Int): Double

    @JsonRpcMethod("gettransaction")
    override fun getWalletTransaction(transactionId: String): Map<*, *>

    @JsonRpcMethod("gettxout")
    override fun getUnspentTransactionOutputInfo(transactionId: String, index: Int): Map<*, *>

    @JsonRpcMethod("gettxoutsetinfo")
    override fun getUnspentTransactionOutputSetInfo(): UtxoSet

    @JsonRpcMethod("getwalletinfo")
    override fun getWalletInfo(): WalletInfo

    @JsonRpcMethod("importaddress")
    override fun importAddress(
            scriptOrAddress: String,
            label: String?,
            rescan: Boolean?,
            includePayToScriptHash: Boolean?)

    @JsonRpcMethod("importprivkey")
    override fun importPrivateKey(
            privateKey: String,
            label: String?,
            rescan: Boolean?
    )

    @JsonRpcMethod("importpubkey")
    override fun importPublicKey(
            publicKey: String,
            label: String?,
            rescan: Boolean?
    )

    @JsonRpcMethod("importwallet")
    override fun importWallet(walletFile: String)

    @JsonRpcMethod("keypoolrefill")
    override fun keypoolRefill(newSize: Int)

    @JsonRpcMethod("listaddressgroupings")
    override fun listAddressGroupings(): List<*>

    @JsonRpcMethod("listbanned")
    override fun listBanned(): List<String>

    @JsonRpcMethod("listlockunspent")
    override fun listLockUnspent(): List<Map<*, *>>

    @JsonRpcMethod("listreceivedbyaddress")
    override fun listReceivedByAddress(
            minConfirmations: Int?,
            includeEmpty: Boolean?,
            includeWatchOnly: Boolean?
    ): List<Map<*, *>>

    @JsonRpcMethod("listsinceblock")
    override fun listSinceBlock(
            blockHash: String?,
            targetConfirmations: Int?,
            includeWatchOnly: Boolean?,
            includeRemoved: Boolean?
    ): Map<*, *>

    @JsonRpcMethod("listtransactions")
    override fun listTransactions(
            account: String?,
            count: Int?,
            skip: Int?,
            includeWatchOnly: Boolean?
    ): List<Map<*, *>>

    @JsonRpcMethod("listunspent")
    override fun listUnspent(
            minConfirmations: Int?,
            maxConfirmations: Int?,
            addresses: List<String>?,
            includeUnsafe: Boolean?,
            queryOptions: QueryOptions?
    ): List<QueryResult>

    @JsonRpcMethod("listwallets")
    override fun listWallets(): List<String>

    @JsonRpcMethod("lockunspent")
    override fun lockUnspent(unlock: Boolean, unspentOutputs: List<OutPoint>): Boolean

    override fun ping()

    @JsonRpcMethod("preciousblock")
    override fun preciousBlock(block: String)

    @JsonRpcMethod("prioritisetransaction")
    override fun prioritiseTransaction(transactionId: String, dummy: Int, feeDeltaSatoshis: Int)

    @JsonRpcMethod("pruneblockchain")
    override fun pruneBlockchain(blockHeightOrUnixTimestamp: Long)

    @JsonRpcMethod("removeprunedfunds")
    override fun removePrunedFunds(transactionId: String)

    @JsonRpcMethod("sendmany")
    override fun sendMany(account: String,
                 addressAmounts: Map<String, Double>,
                 comment: String?,
                 subtractFee: Boolean,
                 replaceable: Boolean,
                 minConfirmations: Int?,
                 feeEstimateMode: FeeEstimateMode?)

    @JsonRpcMethod("sendrawtransaction")
    override fun sendRawTransaction(transaction: String): String

    @JsonRpcMethod("sendtoaddress")
    override fun sendToAddress(
            address: String,
            amount: Double,
            comment: String?,
            commentTo: String?,
            subtractFee: Boolean?,
            replaceable: Boolean?,
            minConfirmations: Int?,
            feeEstimateMode: FeeEstimateMode?): String

    @JsonRpcMethod("setban")
    override fun setBan(
            address: String,
            operation: NodeListOperation,
            seconds: Int
    )

    @JsonRpcMethod("settxfee")
    override fun setTransactionFee(fee: Double)

    @JsonRpcMethod("estimatesmartfee")
    override fun estimateSmartFee(confTarget: Int, feeEstimateMode: FeeEstimateMode?): EstimateSmartFee

    @JsonRpcMethod("signmessage")
    override fun signMessage(
            address: String,
            message: String
    )

    @JsonRpcMethod("signmessagewithprivkey")
    override fun signMessageWithPrivateKey(
            privateKey: String,
            message: String
    )

    @JsonRpcMethod("signrawtransaction")
    override fun signRawTransaction(transactionId: String)

    @JsonRpcMethod("submitblock")
    override fun submitBlock(blockData: String)

    override fun uptime(): Int

    @JsonRpcMethod("validateaddress")
    override fun validateAddress(address: String)

    @JsonRpcMethod("verifychain")
    override fun verifyChain(): Boolean

    @JsonRpcMethod("verifymessage")
    override fun verifyMessage(
            address: String,
            signature: String,
            message: String
    )

    @JsonRpcMethod("searchrawtransactions")
    override fun searchRawSerialisedTransactions(
            address: String,
            verbose: Int?,
            skip: Int?,
            count: Int?,
            vInExtra: Int?,
            reverse: Boolean?): List<String>

    @JsonRpcMethod("searchrawtransactions")
    override fun searchRawVerboseTransactions(
            address: String,
            verbose: Int?,
            skip: Int?,
            count: Int?,
            vInExtra: Int?,
            reverse: Boolean?): List<SearchedTransactionResult>

    /**
     * btcd-specific extension methods
     */
    @JsonRpcMethod("authenticate")
    override fun btcdAuthenticate(username: String, password: String)

    @JsonRpcMethod("generate")
    override fun btcdGenerate(numberOfBlocks: Int): List<String>

    @JsonRpcMethod("getblock")
    override fun btcdGetBlockWithTransactions(blockHash: String, verbose: Boolean): String

    @JsonRpcMethod("getstakeinfo")
    override fun getStakeInfo(): StakeInfo

    @JsonRpcMethod("listaccounts")
    override fun listAccounts(): Map<String, Double>

    @JsonRpcMethod("getaccountaddress")
    override fun getAccountAddress(account: String): String

    @JsonRpcMethod("createnewaccount")
    override fun createNewAccount(account: String)

    @JsonRpcMethod("renameaccount")
    override fun renameAccount(oldAccount: String, newAccount: String)

    @JsonRpcMethod("getblocksubsidy")
    override fun getBlockSubsidy(height: Int, voters: Int): BlockSubsidy

    @JsonRpcMethod("ticketbuyerconfig")
    override fun ticketBuyerConfig(): TicketBuyerConfig

    @JsonRpcMethod("startticketbuyer")
    override fun startTicketBuyer(romaccount: String,
                                  maintain: Double,
                                  passphrase: String?,
                                  votingaccount: String?,
                                  votingaddress: String?,
                                  rewardaddress: String?,
                                  poolfeeaddress: String?,
                                  poolfees: Double?,
                                  limit: Int?,
                                  expiry: Int?,)

    @JsonRpcMethod("stopticketbuyer")
    override fun stopTicketBuyer()

    @JsonRpcMethod("autovoterconfig")
    override fun autoVoterConfig(): AutoVoterConfig

    @JsonRpcMethod("startautovoter")
    override fun startAutoVoter(votebits: Int,
                                votebitsext: String?,
                                passphrase: String?)

    @JsonRpcMethod("stopautovoter")
    override fun stopAutoVoter()

    @JsonRpcMethod("autorevokerconfig")
    override fun autoRevokerConfig() : AutoRevokerConfig

    @JsonRpcMethod("startautorevoker")
    override fun startAutoRevoker(passphrase: String?)

    @JsonRpcMethod("stopautorevoker")
    override fun stopAutoRevoker()

    @JsonRpcMethod("setticketbuyermaxperblock")
    override fun setTicketBuyerMaxPerBlock(count: Int)

    @JsonRpcMethod("setticketfee")
    override fun setTicketFee(fee: Double): Boolean

    @JsonRpcMethod("getticketfee")
    override fun getTicketFee(): String
}
