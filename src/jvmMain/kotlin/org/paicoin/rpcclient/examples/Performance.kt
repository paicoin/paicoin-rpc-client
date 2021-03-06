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

import org.paicoin.rpcclient.JvmPaicoinRpcClient
import org.paicoin.rpcclient.PaicoinRpcClientFactory


fun main(args: Array<String>) {

//    val address = "n2hcSwULcFVpR7XZD6o83A7Ckvtohvb43t"
//    val privateKey = "cN88TB4Frqcb9S5hb889Y2HqrY3Wq5XSBwHTkWgxD48dCb9ippG8"

    val webSocketClient = PaicoinRpcClientFactory.createWsClient("james", "james", "localhost", 18334, true)
    val httpClient = PaicoinRpcClientFactory.createClient2("james", "james", "localhost", 18334, true)

    // Ensure enough blocks before starting
    val numberOfBlocks = 100
    val diff = numberOfBlocks - httpClient.getBlockCount()
    if (diff > 0) {
        println("Generating $diff blocks...")
        httpClient.btcdGenerate(numberOfBlocks)
    }

    doPerfTestBlock("http", httpClient, numberOfBlocks)

    webSocketClient.connect()
    doPerfTestBlock("ws", webSocketClient, numberOfBlocks)
    webSocketClient.disconnect()
}

fun doPerfTestBlock(clientName: String, client: JvmPaicoinRpcClient, blockCount: Int) {
    println("Starting retrieval of first $blockCount blocks using $clientName client..")
    val startTime = System.currentTimeMillis()
    for (i in 0..blockCount) {
        client.getBlockHash(i)
    }
    val duration = System.currentTimeMillis() - startTime
    println("$clientName client took $duration ms (${((blockCount.toFloat() / duration) * 1000).toInt()} requests/sec)")
}
