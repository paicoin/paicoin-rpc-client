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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import javax.net.ssl.SSLContext

class JsonWebSocketRpcClient(wsUrl: String, sslContext: SSLContext) : AbstractJsonWebSocketRpcClient(wsUrl, sslContext) {
    private val responses = mutableMapOf<String, CompletableFuture<String>>()

    override fun handleTextMessage(text: String?) {
        val id = fastExtractId(text!!)
        responses[id]?.complete(text)
        responses.remove(id)
    }

    override fun invoke(methodName: String?, argument: Any?, returnType: Type?, extraHeaders: MutableMap<String, String>?): Any? {
        val outputStream = ByteArrayOutputStream()
        super.invoke(methodName, argument, outputStream)
        val output = outputStream.toString()

        val id = fastExtractId(output)

        try {
            val completableFuture = CompletableFuture<String>()
            responses[id] = completableFuture
            socket.sendText(output)
            val response = completableFuture.get()
            if (hasError(response)) {
                val error = extractError(response)
                throw JsonRpcError(error.first, error.second)
            }
            return if (response == null) null else super.readResponse(returnType, ByteArrayInputStream(response.toByteArray()))
        } finally {
            responses.remove(id)
        }
    }


}