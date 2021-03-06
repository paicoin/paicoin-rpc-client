package org.paicoin.rpcclient

actual object PaicoinRpcClientFactory {
    actual fun createClient(
        user: String,
        password: String,
        host: String,
        port: Int,
        secure: Boolean
    ): PaicoinRpcClient {
        TODO("Not yet implemented")
    }

}