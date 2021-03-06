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

enum class BlockTemplateRequestMode { template, proposal }

enum class FeeEstimateMode { UNSET, ECONOMICAL, CONSERVATIVE }

enum class NodeListOperation { onetry, add, remove }

data class OutPoint(
        val txid: String,
        val vout: Int)

data class Info(
        val version: Long? = null,
        val protocolversion: Long? = null,
        val blocks: Long? = null,
        val timeoffset: Long? = null,
        val connections: Long? = null,
        val proxy: String? = null,
        val difficulty: Double? = null,
        val testnet: Boolean? = null,
        val relayfee: Double? = null,
        val errors: String? = null,
        val pruned: Boolean? = null)

data class BlockChainInfo(
        val chain: String? = null,
        val blocks: Long? = null,
        val headers: Long? = null,
        val bestblockhash: String? = null,
        val difficulty: Double? = null,
        val mediantime: Long? = null,
        val verificationprogress: Long? = null,
        val chainwork: String? = null,
        val pruned: Boolean? = null,
        val pruneheight: Long? = null,
        val softforks: List<SoftForkStatus>? = null,
        val bip9_softforks: Map<String, Bip9SoftForkStatus>? = null)

data class SoftForkRejection(
        val status: Boolean? = null)

data class SoftForkStatus(
        val id: String? = null,
        val version: Long? = null,
        val reject: SoftForkRejection? = null)

data class Bip9SoftForkStatus(
        val status: String? = null,
        val bit: Long? = null,
        val startTime: Long? = null,
        val timeout: Long? = null,
        val since: Long? = null,
        val statistics: Bip9SoftForkStats? = null)

data class Bip9SoftForkStats(
        val period: Long? = null,
        val threshold: Long? = null,
        val elapsed: Long? = null,
        val count: Long? = null,
        val possible: Boolean? = null)

data class BlockTemplateRequest(
        val mode: BlockTemplateRequestMode? = null,
        val capabilities: List<String>? = null,
        val rules: List<String>? = null)

data class ChainTip(
        val height: Int? = null,
        val hash: String? = null,
        val branchlen: Int? = null,
        val status: String? = null)

data class ChainTransactionStats(
        val time: Long? = null,
        val txcount: Long? = null,
        val txrate: Double? = null)

data class MemPoolInfo(
        val size: Long? = null,
        val bytes: Long? = null,
        val usage: Long? = null,
        val maxmempool: Long? = null,
        val mempoolminfee: Long? = null
)

data class MiningInfo(
        val blocks: Long? = null,
        val currentblockweight: Long? = null,
        val currentblocktx: Long? = null,
        val difficulty: Double? = null,
        val errors: String? = null,
        val networkhashps: Double? = null,
        val pooledtx: Long? = null,
        val chain: String? = null,
        val warnings: String? = null
)

data class NetworkTotals(
        val totalbytesrecv: Long? = null,
        val totalbytessent: Long? = null,
        val timemillis: Long? = null,
        val uploadtarget: NetworkUploadTarget? = null)

data class NetworkUploadTarget(
        val timeframe: Long? = null,
        val target: Long? = null,
        val target_reached: Boolean? = null,
        val serve_historical_blocks: Boolean? = null,
        val bytes_left_in_cycle: Long? = null,
        val time_left_in_cycle: Long? = null)

data class NetworkInfo(
        val version: Long? = null,
        val subversion: String? = null,
        val protocolversion: Long? = null,
        val localservices: String? = null,
        val localrelay: Boolean? = null,
        val timeoffset: Int? = null,
        val connections: Int? = null,
        val networkactive: Boolean? = null,
        val networks: List<Network>? = null,
        val relayfee: Double? = null,
        val incrementalfee: Double? = null,
        val localaddresses: List<AddressInfo>? = null,
        val warnings: String? = null)

data class Network(
        val name: String? = null,
        val limited: Boolean? = null,
        val reachable: Boolean? = null,
        val proxy: String? = null,
        val proxy_randomize_credentials: Boolean? = null)

data class AddressInfo(
        val address: String,
        val port: Int,
        val score: Int)

data class PeerInfo(
        val id: Long? = null,
        val addr: String? = null,
        val addrlocal: String? = null,
        val addrbind: String? = null,
        val services: String? = null,
        val relaytxes: Boolean? = null,
        val lastsend: Long? = null,
        val lastrecv: Long? = null,
        val bytessent: Long? = null,
        val bytesrecv: Long? = null,
        val conntime: Long? = null,
        val timeoffset: Long? = null,
        val pingtime: Double? = null,
        val minping: Double? = null,
        val version: Long? = null,
        val subver: String? = null,
        val inbound: Boolean? = null,
        val addnode: Boolean? = null,
        val startingheight: Long? = null,
        val banscore: Long? = null,
        val synced_headers: Long? = null,
        val synced_blocks: Long? = null,
        val inflight: List<*>? = null,
        val whitelisted: Boolean? = null,
        val bytessent_per_msg: BytesPerMessage? = null,
        val bytesrecv_per_msg: BytesPerMessage? = null)

data class BytesPerMessage(
        val alert: Long? = null,
        val filterload: Long? = null,
        val getheaders: Long? = null,
        val getblocks: Long? = null,
        val mempool: Long? = null,
        val ping: Long? = null,
        val pong: Long? = null,
        val verack: Long? = null,
        val inv: Long? = null,
        val version: Long? = null)

data class UtxoSet(
        val height: Int? = null,
        val bestblock: String? = null,
        val transactions: Int? = null,
        val txouts: Int? = null,
        val bogosize: Long? = null,
        val hash_serialized_2: String? = null,
        val disk_size: Long? = null,
        val total_amount: Double? = null)

data class QueryResult(
        val txid: String? = null,
        val vout: Int? = null,
        val label: String? = null,
        val address: String? = null,
        val scriptPubKey: String? = null,
        val amount: Double? = null,
        val confirmations: Int? = null,
        val redeemScript: String? = null,
        val spendable: Boolean? = null,
        val solvable: Boolean? = null,
        val safe: Boolean? = null)

data class QueryOptions(
        val minimumAmount: String? = null,
        val maximumAmount: String? = null,
        val maximumCount: String? = null,
        val minimumSumAmount: String? = null)

data class TransactionInput(
        val txid: String? = null,
        val vout: Long? = null,
        val scriptSig: ScriptSignature? = null,
        val txinwitness: List<String>? = null,
        val coinbase: String? = null,
        val sequence: Long? = null)

data class ScriptSignature(
        val asm: String? = null,
        val hex: String? = null)

data class TransactionOutput(
        val value: Double? = null,
        val n: Long? = null,
        val scriptPubKey: ScriptPubKey? = null)

data class ScriptPubKey(
        val asm: String? = null,
        val hex: String? = null,
        val reqSigs: Long? = null,
        val type: String? = null,
        val addresses: List<String>? = null)

data class Transaction(
        val txid: String? = null,
        val hash: String? = null,
        val size: Long? = null,
        val weight: Long? = null,
        val vsize: Long? = null,
        val version: Long? = null,
        val locktime: Long? = null,
        val vin: List<TransactionInput>? = null,
        val vout: List<TransactionOutput>? = null,
        val hex: String? = null,
        val blockhash: String? = null,
        val confirmations: Int? = null,
        val time: Long? = null,
        val blocktime: Long? = null)

data class DecodedScript(
        val asm: String? = null,
        val hex: String? = null,
        val type: String? = null,
        val reqSigs: Int? = null,
        val addresses: List<String>? = null,
        val p2sh: String? = null)

data class MultiSigAddress(
        val address: String? = null,
        val redeemScript: String? = null)

data class AddedNodeInfo(
        val addednode: String? = null,
        val connected: Boolean? = null,
        val addresses: List<NodeAddress>? = null)

data class NodeAddress(
        val address: String? = null,
        val connected: String? = null)

data class BlockInfo(
        val hash: String? = null,
        val confirmations: Long? = null,
        val size: Long? = null,
        val strippedsize: Long? = null,
        val weight: Long? = null,
        val height: Long? = null,
        val version: Long? = null,
        val versionHex: String? = null,
        val merkleroot: String? = null,
        val tx: List<String>? = null,
        val time: Long? = null,
        val mediantime: Long? = null,
        val nonce: Long? = null,
        val bits: String? = null,
        val difficulty: Double? = null,
        val chainwork: String? = null,
        val previousblockhash: String? = null,
        val nextblockhash: String? = null)

data class BlockInfoWithTransactions(
        val hash: String? = null,
        val confirmations: Long? = null,
        val size: Long? = null,
        val strippedsize: Long? = null,
        val weight: Long? = null,
        val height: Long? = null,
        val version: Long? = null,
        val versionHex: String? = null,
        val merkleroot: String? = null,
        val tx: List<Transaction>? = null,
        val rawtx: List<Transaction>? = null,
        val time: Long? = null,
        val mediantime: Long? = null,
        val nonce: Long? = null,
        val bits: String? = null,
        val difficulty: Double? = null,
        val chainwork: String? = null,
        val previousblockhash: String? = null,
        val nextblockhash: String? = null)


data class SearchedTransactionResult(
        val txid: String? = null,
        val hash: String? = null,
        val size: Long? = null,
        val locktime: Long? = null,
        val vin: List<TransactionInput>? = null,
        val vout: List<TransactionOutput>? = null,
        val hex: String? = null,
        val blockhash: String? = null,
        val confirmations: Int? = null,
        val time: Long? = null,
        val blocktime: Long? = null)

data class EstimateSmartFee(
        val feerate: Double? = null,
        val errors: List<String>? = null,
        val blocks: Long? = null
)

data class StakeInfo(
        val blockheight: Long? = null,
        val difficulty: Double? = null,
        val totalsubsidy: Double? = null,
        val ownmempooltix: Long? = null,
        val immature: Int? = null,
        val unspent: Int? = null,
        val voted: Int? = null,
        val revoked: Int? = null,
        val unspentexpired: Int? = null,
        val poolsize: Int? = null,
        val allmempooltix: Int? = null,
        val live: Int? = null,
        val proportionlive: Double? = null,
        val missed: Int? = null,
        val proportionmissed: Double? = null,
        val expired: Long? = null)

data class WalletInfo(
        val walletname: String? = null,
        val walletversion: Long? = null,
        val available_balance: Double? = null,
        val balance: Double? = null,
        val total_staked_balance: Double? = null,
        val staked_balance_mempool: Double? = null,
        val staked_balance_immature: Double? = null,
        val staked_balance_live: Double? = null,
        val staked_balance_voted: Double? = null,
        val staked_balance_missed: Double? = null,
        val staked_balance_expired: Double? = null,
        val staked_balance_revoked: Double? = null,
        val unconfirmed_balance: Double? = null,
        val immature_balance: Double? = null,
        val legacy_balance: Double? = null,
        val rewards: Double? = null,
        val refunds: Double? = null,
        val mined: Double? = null,
        val txcount: Int? = null,
        val keypoololdest: Long? = null,
        val keypoolsize: Int? = null,
        val keypoolsize_hd_internal: Int? = null,
        val unlocked_until: Long? = null,
        val paytxfee: Double? = null,
        val total_fees: Double? = null,
        val hdmasterkeyid: String? = null)

