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
package org.paicoin.rpcclient.websocket

import com.googlecode.jsonrpc4j.JsonRpcMethod
import org.paicoin.rpcclient.*

class WrappedWebSocketPaiClient(
    private val username: String,
    private val password: String,
    private val delegate: JvmPaicoinRpcClient,
    private val jsonWebSocketRpcClient: JsonWebSocketRpcClient
) : WebSocketPaicoinRpcClient {

    override fun connect() {
        jsonWebSocketRpcClient.connect()
        // Authenticate as soon as web socket is open (btcd)
        delegate.btcdAuthenticate(username, password)
    }

    override fun disconnect() {
        jsonWebSocketRpcClient.disconnect()
    }

    override fun btcdAuthenticate(username: String, password: String) {
        delegate.btcdAuthenticate(username, password)
    }

    override fun btcdGenerate(numberOfBlocks: Int): List<String> {
        return delegate.btcdGenerate(numberOfBlocks)
    }

    override fun btcdGetBlockWithTransactions(blockHash: String, verbose: Boolean): String {
        return delegate.btcdGetBlockWithTransactions(blockHash, verbose)
    }

    override fun abandonTransaction(transactionId: String) {
        delegate.abandonTransaction(transactionId)
    }

    override fun abortRescan() {
        delegate.abortRescan()
    }

    override fun addMultiSigAddress(required: Int?, keys: List<String>): String {
        return delegate.addMultiSigAddress(required, keys)
    }

    override fun addNode(address: String, operation: NodeListOperation) {
        delegate.addNode(address, operation)
    }

    override fun backupWallet(destination: String) {
        delegate.backupWallet(destination)
    }

    override fun clearBanned() {
        delegate.clearBanned()
    }

    override fun createMultiSig(required: Int, keys: List<String>): MultiSigAddress {
        return delegate.createMultiSig(required, keys)
    }

    override fun createRawTransaction(inputs: List<OutPoint>, outputs: Map<String, Double>, lockTime: Int?, replaceable: Boolean?): String {

        return delegate.createRawTransaction(inputs, outputs, lockTime, replaceable)
    }

    override fun decodeRawTransaction(transactionId: String): Transaction {
        return delegate.decodeRawTransaction(transactionId)
    }

    override fun decodeScript(scriptHex: String): DecodedScript {
        return delegate.decodeScript(scriptHex)
    }

    override fun disconnectNode(nodeAddress: String?, nodeId: Int?) {
        delegate.disconnectNode(nodeAddress, nodeId)
    }

    override fun dumpPrivateKey(address: String): String {
        return delegate.dumpPrivateKey(address)
    }

    override fun dumpWallet(filename: String): Map<*, *> {
        return delegate.dumpWallet(filename)
    }

    override fun encryptWallet(passphrase: String) {
        delegate.encryptWallet(passphrase)
    }

    override fun walletPassphraseChange(oldPassphrase: String,newPassphrase: String) {
        delegate.walletPassphraseChange(oldPassphrase,newPassphrase)
    }

    override fun walletPassphrase(passphrase: String,timeout: Long) {
        delegate.walletPassphrase(passphrase,timeout)
    }

    override fun walletLock() {
        delegate.walletLock()
    }

    override fun generate(numberOfBlocks: Int, maxTries: Int?): List<String> {
        return delegate.generate(numberOfBlocks, maxTries)
    }

    override fun getAddedNodeInfo(): List<AddedNodeInfo> {
        return delegate.getAddedNodeInfo()
    }

    override fun getBalance(account: String, minconf: Int, includeWatchOnly: Boolean): Double {
        return delegate.getBalance(account, minconf, includeWatchOnly)
    }

    override fun getBestBlockhash(): String {
        return delegate.getBestBlockhash()
    }

    override fun getBlockData(blockHash: String, verbosity: Int): String {
        return delegate.getBlockData(blockHash, verbosity)
    }

    override fun getBlock(blockHash: String, verbosity: Int): BlockInfo {
        return delegate.getBlock(blockHash, verbosity)
    }

    override fun getBlockWithTransactions(blockHash: String, verbosity: Int): BlockInfoWithTransactions {
        return delegate.getBlockWithTransactions(blockHash, verbosity)
    }

    override fun getInfo(): Info {
        return delegate.getInfo()
    }

    override fun getBlockchainInfo(): BlockChainInfo {
        return delegate.getBlockchainInfo()
    }

    override fun getBlockCount(): Int {
        return delegate.getBlockCount()
    }

    override fun getBlockHash(height: Int): String {
        return delegate.getBlockHash(height)
    }

    override fun getBlockHeader(blockHash: String, verbose: Boolean?): Any {
        return delegate.getBlockHeader(blockHash, verbose)
    }

    override fun getBlockTemplate(blockTemplateRequest: BlockTemplateRequest?) {
        delegate.getBlockTemplate(blockTemplateRequest)
    }

    override fun getChainTips(): List<ChainTip> {
        return delegate.getChainTips()
    }

    override fun getChainTransactionStats(blockWindowSize: Int?, blockHashEnd: String?): ChainTransactionStats {
        return delegate.getChainTransactionStats(blockWindowSize, blockHashEnd)
    }

    override fun getConnectionCount(): Int {
        return delegate.getConnectionCount()
    }

    override fun getDifficulty(): Double {
        return delegate.getDifficulty()
    }

    override fun getMemoryInfo(): Any {
        return delegate.getMemoryInfo()
    }

    override fun getMempoolAncestors(transactionId: String): Any {
        return delegate.getMempoolAncestors(transactionId)
    }

    override fun getMempoolDescendants(): Any {
        return delegate.getMempoolDescendants()
    }

    override fun getMempoolEntry(transactionId: String): Map<*, *> {
        return delegate.getMempoolEntry(transactionId)
    }

    override fun getMempoolInfo(): MemPoolInfo {
        return delegate.getMempoolInfo()
    }

    override fun getMiningInfo(): MiningInfo {
        return delegate.getMiningInfo()
    }

    override fun getNetworkTotals(): NetworkTotals {
        return delegate.getNetworkTotals()
    }

    override fun getNetworkHashesPerSeconds(lastBlocks: Int, height: Int): Long {
        return delegate.getNetworkHashesPerSeconds(lastBlocks, height)
    }

    override fun getNetworkInfo(): NetworkInfo {
        return delegate.getNetworkInfo()
    }

    override fun getNewAddress(): String {
        return delegate.getNewAddress()
    }

    override fun getNewAddress(account: String): String {
        return delegate.getNewAddress(account)
    }

    override fun getPeerInfo(): List<PeerInfo> {
        return delegate.getPeerInfo()
    }

    override fun getRawChangeAddress(): String {
        return delegate.getRawChangeAddress()
    }

    override fun getRawMemPool(verbose: Boolean): List<Map<*, *>> {
        return delegate.getRawMemPool(verbose)
    }

    override fun getRawTransaction(transactionId: String, verbosity: Int): Transaction {
        return delegate.getRawTransaction(transactionId)
    }

    override fun getReceivedByAddress(address: String, minConfirmations: Int): Double {
        return delegate.getReceivedByAddress(address, minConfirmations)
    }

    override fun getWalletTransaction(transactionId: String): Map<*, *> {
        return delegate.getWalletTransaction(transactionId)
    }

    override fun getUnspentTransactionOutputInfo(transactionId: String, index: Int): Map<*, *> {
        return delegate.getUnspentTransactionOutputInfo(transactionId, index)
    }

    override fun getUnspentTransactionOutputSetInfo(): UtxoSet {
        return delegate.getUnspentTransactionOutputSetInfo()
    }

    override fun getWalletInfo(): WalletInfo {
        return delegate.getWalletInfo()
    }

    override fun importAddress(scriptOrAddress: String, label: String?, rescan: Boolean?, includePayToScriptHash: Boolean?) {
        delegate.importAddress(scriptOrAddress, label, rescan, includePayToScriptHash)
    }

    override fun importPrivateKey(privateKey: String, label: String?, rescan: Boolean?) {
        delegate.importPrivateKey(privateKey, label, rescan)
    }

    override fun importPublicKey(publicKey: String, label: String?, rescan: Boolean?) {
        delegate.importPublicKey(publicKey, label, rescan)
    }

    override fun importWallet(walletFile: String) {
        delegate.importWallet(walletFile)
    }

    override fun keypoolRefill(newSize: Int) {
        delegate.keypoolRefill(newSize)
    }

    override fun listAddressGroupings(): List<*> {
        return delegate.listAddressGroupings()
    }

    override fun listBanned(): List<String> {
        return delegate.listBanned()
    }

    override fun listLockUnspent(): List<Map<*, *>> {
        return delegate.listLockUnspent()
    }

    override fun listReceivedByAddress(minConfirmations: Int?, includeEmpty: Boolean?, includeWatchOnly: Boolean?): List<Map<*, *>> {
        return delegate.listReceivedByAddress(minConfirmations, includeEmpty, includeWatchOnly)
    }

    override fun listSinceBlock(blockHash: String?, targetConfirmations: Int?, includeWatchOnly: Boolean?, includeRemoved: Boolean?): Map<*, *> {
        return delegate.listSinceBlock(blockHash, targetConfirmations, includeWatchOnly, includeRemoved)
    }

    override fun listTransactions(account: String?, count: Int?, skip: Int?, includeWatchOnly: Boolean?): List<Map<*, *>> {
        return delegate.listTransactions(account, count, skip, includeWatchOnly)
    }

    override fun listUnspent(minConfirmations: Int?, maxConfirmations: Int?, addresses: List<String>?, includeUnsafe: Boolean?, queryOptions: QueryOptions?): List<QueryResult> {
        return delegate.listUnspent(minConfirmations, maxConfirmations, addresses, includeUnsafe, queryOptions)
    }

    override fun listWallets(): List<String> {
        return delegate.listWallets()
    }

    override fun lockUnspent(unlock: Boolean, unspentOutputs: List<OutPoint>): Boolean {
        return delegate.lockUnspent(unlock, unspentOutputs)
    }

    override fun ping() {
        delegate.ping()
    }

    override fun preciousBlock(block: String) {
        delegate.preciousBlock(block)
    }

    override fun prioritiseTransaction(transactionId: String, dummy: Int, feeDeltaSatoshis: Int) {
        delegate.prioritiseTransaction(transactionId, dummy, feeDeltaSatoshis)
    }

    override fun pruneBlockchain(blockHeightOrUnixTimestamp: Long) {
        delegate.pruneBlockchain(blockHeightOrUnixTimestamp)
    }

    override fun removePrunedFunds(transactionId: String) {
        delegate.removePrunedFunds(transactionId)
    }

    override fun sendMany(account: String, addressAmounts: Map<String, Double>, comment: String?, subtractFee: Boolean, replaceable: Boolean, minConfirmations: Int?, feeEstimateMode: FeeEstimateMode?) {
        delegate.sendMany(account, addressAmounts, comment, subtractFee, replaceable, minConfirmations, feeEstimateMode)
    }

    override fun sendRawTransaction(transaction: String): String {
        return delegate.sendRawTransaction(transaction)
    }

    override fun sendToAddress(address: String, amount: Double, comment: String?, commentTo: String?, subtractFee: Boolean?, replaceable: Boolean?, minConfirmations: Int?, feeEstimateMode: FeeEstimateMode?): String {
        return delegate.sendToAddress(address, amount, comment, commentTo, subtractFee, replaceable, minConfirmations, feeEstimateMode)
    }

    override fun setBan(address: String, operation: NodeListOperation, seconds: Int) {
        delegate.setBan(address, operation, seconds)
    }

    override fun setTransactionFee(fee: Double) {
        delegate.setTransactionFee(fee)
    }

    override fun estimateSmartFee(confTarget: Int, feeEstimateMode: FeeEstimateMode?): EstimateSmartFee {
        return delegate.estimateSmartFee(confTarget, feeEstimateMode)
    }

    override fun signMessage(address: String, message: String) {
        delegate.signMessage(address, message)
    }

    override fun signMessageWithPrivateKey(privateKey: String, message: String) {
        delegate.signMessageWithPrivateKey(privateKey, message)
    }

    override fun signRawTransaction(transactionId: String) {
        delegate.signRawTransaction(transactionId)
    }

    override fun submitBlock(blockData: String) {
        delegate.submitBlock(blockData)
    }

    override fun uptime(): Int {
        return delegate.uptime()
    }

    override fun validateAddress(address: String) {
        delegate.validateAddress(address)
    }

    override fun verifyChain(): Boolean {
        return delegate.verifyChain()
    }

    override fun verifyMessage(address: String, signature: String, message: String) {
        delegate.verifyMessage(address, signature, message)
    }

    override fun searchRawSerialisedTransactions(address: String, verbose: Int?, skip: Int?, count: Int?, vInExtra: Int?, reverse: Boolean?): List<String> {
        return delegate.searchRawSerialisedTransactions(address, verbose, skip, count, vInExtra, reverse)
    }

    override fun searchRawVerboseTransactions(address: String, verbose: Int?, skip: Int?, count: Int?, vInExtra: Int?, reverse: Boolean?): List<SearchedTransactionResult> {
        return delegate.searchRawVerboseTransactions(address, verbose, skip, count, vInExtra, reverse)
    }

    override fun getStakeInfo(): StakeInfo {
        return delegate.getStakeInfo()
    }

    override fun listAccounts(): Map<String, Double> {
        return delegate.listAccounts()
    }

    override fun getAccountAddress(account: String): String {
        return delegate.getAccountAddress(account)
    }

    override fun createNewAccount(account: String) {
        return delegate.createNewAccount(account)
    }

    override fun renameAccount(oldAccount: String, newAccount: String) {
        return delegate.renameAccount(oldAccount,newAccount)
    }

    override fun getBlockSubsidy(height: Int, voters: Int): BlockSubsidy {
        return delegate.getBlockSubsidy(height,voters)
    }

    override fun ticketBuyerConfig(): TicketBuyerConfig {
        return delegate.ticketBuyerConfig()
    }

    override fun startTicketBuyer(
        fromaccount: String,
        maintain: Double,
        passphrase: String?,
        votingaccount: String?,
        votingaddress: String?,
        rewardaddress: String?,
        poolfeeaddress: String?,
        poolfees: Double?,
        limit: Int?,
        expiry: Int?
    ) {
        return delegate.startTicketBuyer(fromaccount,maintain,passphrase,votingaccount,votingaddress,rewardaddress,poolfeeaddress,poolfees,limit,expiry)
    }

    override fun stopTicketBuyer() {
        return delegate.stopTicketBuyer()
    }

    override fun autoVoterConfig(): AutoVoterConfig {
        return delegate.autoVoterConfig()
    }

    override fun startAutoVoter(votebits: Int,
                                votebitsext: String?,
                                passphrase: String?) {
        return delegate.startAutoVoter(votebits,votebitsext,passphrase)
    }

    override fun stopAutoVoter() {
        return delegate.stopAutoVoter()
    }

    override fun autoRevokerConfig() : AutoRevokerConfig {
        return delegate.autoRevokerConfig()
    }

    override fun startAutoRevoker(passphrase: String?) {
        return delegate.startAutoRevoker(passphrase)
    }

    override fun stopAutoRevoker() {
        return delegate.stopAutoRevoker()
    }

    override fun setTicketBuyerMaxPerBlock(count: Int) {
        return delegate.setTicketBuyerMaxPerBlock(count)
    }

    override fun setTicketFee(fee: Double): Boolean {
        return delegate.setTicketFee(fee)
    }

    override fun getTicketFee(): String {
        return delegate.getTicketFee()
    }

}
