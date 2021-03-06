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
package org.paicoin.rpcclient.examples

import org.paicoin.rpcclient.AsyncPaicoinRpcClient
import org.paicoin.rpcclient.PaicoinRpcClientFactory
import org.paicoin.rpcclient.BlockInfoWithTransactions
import org.paicoin.rpcclient.Transaction
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class BlockWalker(val client: AsyncPaicoinRpcClient, val startingBlockHeight: Int = 0) {

    fun getBlocks(): Observable<BlockInfoWithTransactions> {
        return Observable
                // Defer the future to allow Rx to schedule onto IO pool
                .defer { Observable.fromFuture(client.getBlockchainInfo()) }
                .flatMap { Observable.range(startingBlockHeight, it.blocks!!.toInt()) }
                .flatMap { Observable.fromFuture(client.getBlockHash(it)) }
                .flatMap { Observable.fromFuture(client.btcdGetBlockWithTransactions(it)) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
    }

    fun getTransactions(): Observable<Transaction> {
        return getBlocks()
                .flatMap { Observable.fromIterable(it.rawtx) }
    }
}

fun main(args: Array<String>) {
    val webSocketClient = PaicoinRpcClientFactory.createAsyncWsClient("james", "james", "localhost", 18334, true)

    webSocketClient.connect()

    val blockWalker = BlockWalker(webSocketClient)

    blockWalker
            .getTransactions()
            .groupBy { it.time!! % 8 }
            .flatMap { it
                    .observeOn(Schedulers.computation())
                    .map { println("${System.currentTimeMillis()} THR[${Thread.currentThread().name}] ${it.hash} - ${it.confirmations}"); Thread.sleep(500); it }
            }
            .forEach { println("${System.currentTimeMillis()} ${Thread.currentThread().name}  ${it.hash}") }

    Thread.sleep(10000)

    webSocketClient.disconnect()
}
