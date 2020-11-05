package org.paicoin.rpcclient;

import org.junit.Test;

import org.paicoin.rpcclient.PaicoindRpcClient.BlockTemplate;
import org.paicoin.rpcclient.PaicoindRpcClient.MiningInfo;


import static org.junit.Assert.*;

import java.math.BigDecimal;


public class MiningRPCClientTest extends PaicoinTestBase{
	
	
	@Test
    public void getBlockTemplateTest() throws Exception {
    	PaicoindRpcClient client = getClient("getblocktemplate");
    	BlockTemplate blockTemplate = client.getBlockTemplate();
        assertEquals(blockTemplate != null 
        		&& blockTemplate.version() > 0
        		&& blockTemplate.height() > 0
        		,true);
        
    }
	
    @Test
    public void getMiningInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("getmininginfo");
    	MiningInfo miningInfo = client.getMiningInfo();
        assertEquals(miningInfo != null 
        		&& miningInfo.blocks() > 0
        		,true);
        
    }
    
    @Test
    public void getNetworkHashPsTest() throws Exception {
    	PaicoindRpcClient client = getClient("getnetworkhashps");
    	BigDecimal bigDecimal1 = client.getNetworkHashPs(100);
    	BigDecimal bigDecimal2 = client.getNetworkHashPs();
        assertEquals(bigDecimal1 != null && bigDecimal1.doubleValue() > 0
        		&& bigDecimal2 != null && bigDecimal2.doubleValue() > 0
        		,true);
    }
    
    @Test
    public void submitBlockTest() throws Exception {
    	PaicoindRpcClient client = getClient("submitblock");
    	client.submitBlock("");
        assertEquals(true,true);
    }
    
    
}