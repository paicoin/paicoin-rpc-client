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

import com.googlecode.jsonrpc4j.IJsonRpcClient
import com.googlecode.jsonrpc4j.JsonRpcHttpClient
import com.googlecode.jsonrpc4j.ProxyUtil
import org.paicoin.rpcclient.websocket.*
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


actual object PaicoinRpcClientFactory {

    actual fun createClient(
        user: String,
        password: String,
        host: String,
        port: Int,
        secure: Boolean
    ): PaicoinRpcClient {
         return createClient2(user,password,host,port,secure,createUnsafeSslContext())
    }

    @JvmStatic
    fun createClient2(user: String,
                     password: String,
                     host: String,
                     port: Int,
                     secure: Boolean = false,
                     sslContext: SSLContext = createUnsafeSslContext()):

            JvmPaicoinRpcClient {

        val jsonRpcHttpClient: IJsonRpcClient

        jsonRpcHttpClient = JsonRpcHttpClient(
                URL("${if (secure) "https" else "http"}://$user@$host:$port"),
                mapOf(Pair("Authorization", computeBasicAuth(user, password))))

        jsonRpcHttpClient.setSslContext(sslContext)

        return ProxyUtil.createClientProxy(
                PaicoinRpcClientFactory::class.java.classLoader,
                JvmPaicoinRpcClient::class.java,
                jsonRpcHttpClient
        )
    }

    @JvmStatic
    fun createWsClient(user: String,
                       password: String,
                       host: String,
                       port: Int,
                       secure: Boolean = false,
                       sslContext: SSLContext = createUnsafeSslContext()):

            WebSocketPaicoinRpcClient {

        val jsonWebSocketRpcClient = JsonWebSocketRpcClient(
                wsUrl = "${if (secure) "wss" else "ws"}://$host:$port/ws",
                sslContext = sslContext
        )

        val proxyClient = ProxyUtil.createClientProxy(
                PaicoinRpcClientFactory::class.java.classLoader,
                JvmPaicoinRpcClient::class.java,
                jsonWebSocketRpcClient
        )

        return WrappedWebSocketPaiClient(user, password, proxyClient, jsonWebSocketRpcClient)
    }

    @JvmStatic
    fun createAsyncWsClient(user: String,
                       password: String,
                       host: String,
                       port: Int,
                       secure: Boolean = false,
                       sslContext: SSLContext = createUnsafeSslContext()):

            AsyncWebSocketPaicoinRpcClient {

        val jsonWebSocketRpcClient = JsonAsyncWebSocketRpcClient(
                wsUrl = "${if (secure) "wss" else "ws"}://$host:$port/ws",
                sslContext = sslContext
        )

        val proxyClient = ProxyUtil.createClientProxy(
                PaicoinRpcClientFactory::class.java.classLoader,
                AsyncPaicoinRpcClient::class.java,
                jsonWebSocketRpcClient
        )

        return WrappedAsyncWebSocketBtcClient(user, password, proxyClient, jsonWebSocketRpcClient)
    }

    private fun createUnsafeSslContext(): SSLContext {
        val dummyTrustManager = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, dummyTrustManager, SecureRandom())
        return sslContext
    }

    private fun computeBasicAuth(user: String, password: String) =
            "Basic ${BASE64.encodeToString("$user:$password".toByteArray())}"

    private val BASE64 = Base64.getEncoder()



}

