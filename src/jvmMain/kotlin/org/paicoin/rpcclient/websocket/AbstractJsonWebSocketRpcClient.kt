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

import com.fasterxml.jackson.databind.ObjectMapper
import com.googlecode.jsonrpc4j.IJsonRpcClient
import com.googlecode.jsonrpc4j.JsonRpcClient
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import java.lang.reflect.Type
import javax.net.ssl.SSLContext

abstract class AbstractJsonWebSocketRpcClient(wsUrl: String, sslContext: SSLContext) : JsonRpcClient(), IJsonRpcClient {

    private val webSocketFactory = WebSocketFactory()

    protected val socket: WebSocket

    init {
        webSocketFactory.sslContext = sslContext
        socket = webSocketFactory.createSocket(wsUrl)
    }

    abstract override fun invoke(methodName: String?, argument: Any?, returnType: Type?, extraHeaders: MutableMap<String, String>?): Any?

    protected abstract fun handleTextMessage(text: String?)

    fun connect() {
        socket.addListener(object: WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                handleTextMessage(text)
            }
        })

        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
        // Hack to ensure reading close timer task is not running by the time our application quits
        Thread.getAllStackTraces().keys.filter { it.name == "ReadingThreadCloseTimer" }.forEach { it.stop() }
    }

    protected fun fastExtractId(jsonRpc: String) = jsonRpc.substringAfterLast("\"id\":\"").substringBefore("\"")

    protected fun hasError(jsonRpcResponse: String) = !jsonRpcResponse.contains("\"error\":null")

    protected fun extractError(jsonRpcResponse: String): Pair<Int, String> {
        val errorNode = ObjectMapper().readTree(jsonRpcResponse)["error"]
        val errorCode = errorNode["code"].asInt()
        val errorMessage = errorNode["message"].asText()
        return Pair(errorCode, errorMessage)
    }

    override fun invoke(methodName: String?, argument: Any?) {
        invoke(methodName, argument, null as Type?)
    }

    override fun invoke(methodName: String?, argument: Any?, returnType: Type?): Any? {
        return invoke(methodName, argument, returnType, mutableMapOf())
    }

    override fun <T : Any?> invoke(methodName: String?, argument: Any?, clazz: Class<T>?): T {
        return invoke(methodName, argument, clazz, mutableMapOf())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> invoke(methodName: String?, argument: Any?, clazz: Class<T>?, extraHeaders: MutableMap<String, String>?): T {
        return invoke(methodName, argument, clazz as Type, extraHeaders) as T
    }
}

class JsonRpcError(val code: Int, val errorMessage: String) : Exception(errorMessage)