package org.paicoin.rpcclient;

import org.junit.Test;

import org.paicoin.rpcclient.PaicoindRpcClient.NetTotals;
import org.paicoin.rpcclient.PaicoindRpcClient.NetworkInfo;
import org.paicoin.rpcclient.PaicoindRpcClient.NodeInfo;
import org.paicoin.rpcclient.PaicoindRpcClient.PeerInfoResult;

import java.util.List;

import static org.junit.Assert.*;


public class NetworkRPCClientTest extends PaicoinTestBase{
	
//	@Before
//    public void init() {
//    }
	
    @Test
    public void getNetworkInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("getnetworkinfo");
        NetworkInfo networkInfo = client.getNetworkInfo();
        assertEquals(networkInfo != null 
        		&& evn.version == networkInfo.version()
        		&& evn.protocolVersion == networkInfo.protocolVersion()
        		&& evn.subversion.contentEquals(networkInfo.subversion())
        		&& networkInfo.localRelay()
        		,true);
        
    }
    
    @Test
    public void getPeerInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("getpeerinfo");
    	List<PeerInfoResult> result = client.getPeerInfo();
        assertEquals(result != null && result.size()>0
        		&& result.get(0).getAddr() != null 
        		&& result.get(0).getAddrLocal() != null
        		&& evn.protocolVersion == result.get(0).getVersion()
        		,true);
        
    }
    
    @Test
    public void getNetTotalsTest() throws Exception {
    	PaicoindRpcClient client = getClient("getnettotals");
    	NetTotals result = client.getNetTotals();
        assertEquals(result != null && result.totalBytesRecv()>0
        		,true);
        
    }
    
    @Test
    public void getConnectionCountTest() throws Exception {
    	PaicoindRpcClient client = getClient("getconnectioncount");
    	long result = client.getConnectionCount();
        assertEquals(result > 0,true);
    }
    
    @Test
    public void addNodeTest() throws Exception {
    	CommonPaicoinTestClient client = (CommonPaicoinTestClient)getClient("addnode");
    	client.addNode(client.rpcURL.getHost()+":"+client.rpcURL.getPort(), "remove");
    	client.addNode(client.rpcURL.getHost()+":"+client.rpcURL.getPort(), "add");
        assertEquals(true,true);
    }
    
    @Test
    public void getAddedNodeInfoTest() throws Exception {
    	CommonPaicoinTestClient client = (CommonPaicoinTestClient)getClient("getaddednodeinfo");
    	List<NodeInfo> result1 = client.getAddedNodeInfo(client.rpcURL.getHost()+":"+client.rpcURL.getPort());
    	List<NodeInfo> result2 = client.getAddedNodeInfo();
        assertEquals(result1 != null && result1.size() > 0
        		&& result2 != null && result2.size() > 0
        		,true);
        
    }
    
    @Test
    public void pingTest() throws Exception {
    	PaicoindRpcClient client = getClient("ping");
    	client.ping();
        assertEquals(true,true);
        
    }
    
}