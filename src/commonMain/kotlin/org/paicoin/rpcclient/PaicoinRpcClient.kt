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

interface PaicoinRpcClient {

    fun abandonTransaction(transactionId: String)

    fun abortRescan()

    fun addMultiSigAddress(required: Int? = null, keys: List<String>): String

    fun addNode(address: String, operation: NodeListOperation)

    fun backupWallet(destination: String)

    fun clearBanned()

    fun createMultiSig(required: Int, keys: List<String>): MultiSigAddress

    fun createRawTransaction(
            inputs: List<OutPoint>,
            outputs: Map<String, Double>,
            lockTime: Int? = null,
            replaceable: Boolean? = null
    ): String

    fun decodeRawTransaction(transactionId: String): Transaction

    fun decodeScript(scriptHex: String): DecodedScript

    fun disconnectNode(nodeAddress: String? = null, nodeId: Int? = null)

    fun dumpPrivateKey(address: String): String

    fun dumpWallet(filename: String): Map<*, *>

    fun encryptWallet(passphrase: String)

    fun walletPassphraseChange(oldPassphrase: String,newPassphrase: String)

    fun walletPassphrase(passphrase: String,timeout: Long)

    fun walletLock()

    fun generate(numberOfBlocks: Int, maxTries: Int? = null): List<String>

    fun getAddedNodeInfo(): List<AddedNodeInfo>

    fun getBalance(
            account: String = "*",
            minconf: Int = 1,
            includeWatchOnly: Boolean = false): Double

    fun getBestBlockhash(): String

    fun getBlockData(blockHash: String, verbosity: Int = 0): String

    fun getBlock(blockHash: String, verbosity: Int = 1): BlockInfo

    fun getBlockWithTransactions(blockHash: String, verbosity: Int = 2): BlockInfoWithTransactions

    fun getInfo(): Info

    fun getBlockchainInfo(): BlockChainInfo

    fun getBlockCount(): Int

    fun getBlockHash(height: Int): String

    fun getBlockHeader(blockHash: String, verbose: Boolean? = false): Any

    fun getBlockTemplate(blockTemplateRequest: BlockTemplateRequest? = null)

    fun getChainTips(): List<ChainTip>

    fun getChainTransactionStats(
            blockWindowSize: Int? = null,
            blockHashEnd: String? = null
    ): ChainTransactionStats

    fun getConnectionCount(): Int

    fun getDifficulty(): Double

    fun getMemoryInfo(): Any

    fun getMempoolAncestors(transactionId: String): Any

    fun getMempoolDescendants(): Any

    fun getMempoolEntry(transactionId: String): Map<*, *>

    fun getMempoolInfo(): MemPoolInfo

    fun getMiningInfo(): MiningInfo

    fun getNetworkTotals(): NetworkTotals

    fun getNetworkHashesPerSeconds(lastBlocks: Int, height: Int): Long

    fun getNetworkInfo(): NetworkInfo

    fun getNewAddress(): String

    fun getPeerInfo(): List<PeerInfo>

    fun getRawChangeAddress(): String

    fun getRawMemPool(verbose: Boolean = false): List<Map<*, *>>

    fun getRawTransaction(transactionId: String, verbosity: Int = 1): Transaction

    fun getReceivedByAddress(address: String, minConfirmations: Int = 1): Double

    fun getWalletTransaction(transactionId: String): Map<*, *>

    fun getUnspentTransactionOutputInfo(transactionId: String, index: Int): Map<*, *>

    fun getUnspentTransactionOutputSetInfo(): UtxoSet

    fun getWalletInfo(): WalletInfo

    fun importAddress(
            scriptOrAddress: String,
            label: String? = null,
            rescan: Boolean? = null,
            includePayToScriptHash: Boolean? = null)

    fun importPrivateKey(
            privateKey: String,
            label: String? = null,
            rescan: Boolean? = null
    )

    fun importPublicKey(
            publicKey: String,
            label: String? = null,
            rescan: Boolean? = null
    )

    fun importWallet(walletFile: String)

    fun keypoolRefill(newSize: Int = 100)

    fun listAddressGroupings(): List<*>

    fun listBanned(): List<String>

    fun listLockUnspent(): List<Map<*, *>>

    fun listReceivedByAddress(
            minConfirmations: Int? = null,
            includeEmpty: Boolean? = null,
            includeWatchOnly: Boolean? = null
    ): List<Map<*, *>>

    fun listSinceBlock(
            blockHash: String? = null,
            targetConfirmations: Int? = null,
            includeWatchOnly: Boolean? = null,
            includeRemoved: Boolean? = null
    ): Map<*, *>

    fun listTransactions(
            account: String? = null,
            count: Int? = null,
            skip: Int? = null,
            includeWatchOnly: Boolean? = null
    ): List<Map<*, *>>

    fun listUnspent(
            minConfirmations: Int? = null,
            maxConfirmations: Int? = null,
            addresses: List<String>? = null,
            includeUnsafe: Boolean? = null,
            queryOptions: QueryOptions? = null
    ): List<QueryResult>

    fun listWallets(): List<String>

    fun lockUnspent(unlock: Boolean, unspentOutputs: List<OutPoint>): Boolean

    fun ping()

    fun preciousBlock(block: String)

    fun prioritiseTransaction(transactionId: String, dummy: Int, feeDeltaSatoshis: Int)

    fun pruneBlockchain(blockHeightOrUnixTimestamp: Long)

    fun removePrunedFunds(transactionId: String)

    fun sendMany(account: String,
                 addressAmounts: Map<String, Double>,
                 comment: String? = null,
                 subtractFee: Boolean = false,
                 replaceable: Boolean = false,
                 minConfirmations: Int? = null,
                 feeEstimateMode: FeeEstimateMode? = null)

    fun sendRawTransaction(transaction: String): String

    fun sendToAddress(
            address: String,
            amount: Double,
            comment: String? = null,
            commentTo: String? = null,
            subtractFee: Boolean? = null,
            replaceable: Boolean? = null,
            minConfirmations: Int? = null,
            feeEstimateMode: FeeEstimateMode? = null): String

    fun setBan(
            address: String,
            operation: NodeListOperation,
            seconds: Int
    )

    fun setTransactionFee(fee: Double)

    fun estimateSmartFee(confTarget: Int, feeEstimateMode: FeeEstimateMode? = FeeEstimateMode.CONSERVATIVE): EstimateSmartFee

    fun signMessage(
            address: String,
            message: String
    )

    fun signMessageWithPrivateKey(
            privateKey: String,
            message: String
    )

    fun signRawTransaction(transactionId: String)

    fun submitBlock(blockData: String)

    fun uptime(): Int

    fun validateAddress(address: String)

    fun verifyChain(): Boolean

    fun verifyMessage(
            address: String,
            signature: String,
            message: String
    )

    fun searchRawSerialisedTransactions(
            address: String,
            verbose: Int? = 0,
            skip: Int? = null,
            count: Int? = null,
            vInExtra: Int? = null,
            reverse: Boolean? = null): List<String>

    fun searchRawVerboseTransactions(
            address: String,
            verbose: Int? = 1,
            skip: Int? = null,
            count: Int? = null,
            vInExtra: Int? = null,
            reverse: Boolean? = null): List<SearchedTransactionResult>

    fun btcdAuthenticate(username: String, password: String)

    fun btcdGenerate(numberOfBlocks: Int): List<String>

    fun btcdGetBlockWithTransactions(blockHash: String, verbose: Boolean = true): String

    fun getStakeInfo(): StakeInfo

    fun listAccounts(): Map<String, Double>

    fun getAccountAddress(account: String): String

    fun createNewAccount(account: String)

    fun renameAccount(oldAccount: String,newAccount: String)

    fun getNewAddress(account: String): String


}
