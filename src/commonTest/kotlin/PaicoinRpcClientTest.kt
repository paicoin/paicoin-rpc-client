import org.paicoin.rpcclient.NetworkInfo
import org.paicoin.rpcclient.PaicoinRpcClientFactory
import kotlin.test.Test
import kotlin.test.assertNotNull

class PaicoinRpcClientTest {

    @Test
    fun testNetworkInfo() {

        val rpcClient = PaicoinRpcClientFactory.createClient(
            user = "",
            password = "",
            host = "",
            port = 8566,
            secure = false)
        var networkinfo : NetworkInfo = rpcClient.getNetworkInfo()

        println("networkinfo ${networkinfo}")

        assertNotNull(networkinfo)
    }
}