package cash.pai.rpcclient;

import org.junit.Test;

import cash.pai.rpcclient.PaicoindRpcClient.Block;
import cash.pai.rpcclient.PaicoindRpcClient.BlockChainInfo;
import cash.pai.rpcclient.PaicoindRpcClient.ChainTips;
import cash.pai.rpcclient.PaicoindRpcClient.MempoolInfo;
import cash.pai.rpcclient.PaicoindRpcClient.TxOut;
import cash.pai.rpcclient.PaicoindRpcClient.TxOutSetInfo;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;


public class BlockchainRPCClientTest extends PaicoinTestBase{
	
	
	@Test
    public void getBlockTemplateTest() throws Exception {
    	PaicoindRpcClient client = getClient("getbestblockhash");
    	String blockHash = client.getBestBlockHash();
        assertEquals(blockHash != null ,true);
    }
    
	@Test
    public void getBlockTest() throws Exception {
    	PaicoindRpcClient client = getClient("getblock","getblockhash");
    	String blockHash = client.getBlockHash(57440);
    	Block block1 = client.getBlock(57440);
    	Block block2 = client.getBlock(blockHash);
    	
        assertEquals(block1 != null && block2 != null && block1.height() == block2.height()  ,true);
    }
	
	@Test
    public void getBlockChainInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("getblockchaininfo");
    	BlockChainInfo blockChainInfo = client.getBlockChainInfo();
        assertEquals(blockChainInfo != null 
        		&& blockChainInfo.blocks() > 0
        		,true);
    }
	
	@Test
    public void getBlockCountTest() throws Exception {
    	PaicoindRpcClient client = getClient("getblockcount");
    	int blockCount = client.getBlockCount();
        assertEquals(blockCount > 0 ,true);
    }
	
	@Test
    public void getChainTipsTest() throws Exception {
    	PaicoindRpcClient client = getClient("getchaintips");
    	List<ChainTips> list = client.getChainTips();
        assertEquals(list != null && list.size() > 0 ,true);
    }
	
	@Test
    public void getDifficultyTest() throws Exception {
    	PaicoindRpcClient client = getClient("getdifficulty");
    	BigDecimal diff = client.getDifficulty();
        assertEquals(diff != null && diff.doubleValue() > 0 ,true);
    }
	
	@Test
    public void getMempoolInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("getmempoolinfo");
    	MempoolInfo mempoolInfo = client.getMempoolInfo();
        assertEquals(mempoolInfo != null && mempoolInfo.maxmempool() > 0 ,true);
    }
	
	
	@Test
    public void getRawMemPoolTest() throws Exception {
    	PaicoindRpcClient client = getClient("getrawmempool");
    	List<String> list = client.getRawMemPool();
        assertEquals(list != null && list.size() > 0 ,true);
    }
	
	@Test
    public void getTxOutTest() throws Exception {
    	PaicoindRpcClient client = getClient("gettxout");
    	TxOut txOut = client.getTxOut("e6cc2e83ba940354154579a99ac87442b8c410f78ace713083e3ddb929b24b3d", 1);
        assertEquals(txOut != null && txOut.confirmations() > 0 ,true);
    }
	
	@Test
    public void getTxOutProofTest() throws Exception {
    	PaicoindRpcClient client = getClient("gettxoutproof");
    	String[] txids = {"5d6a945aae9c0d55b9d1c7dfc6e95d6f33fb26e66b4326af1d95d677aeecbc06"};
    	String data = client.getTxOutProof(txids,"0000000004b36d93f351588e429556ead7164a2b574d95c73b2ebef28044fa0c");
        assertEquals(data != null ,true);
    }
	
	@Test
    public void getTxOutSetInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("gettxoutsetinfo");
    	TxOutSetInfo txOutSetInfo = client.getTxOutSetInfo();
        assertEquals(txOutSetInfo != null && txOutSetInfo.height() > 0 ,true);
    }
	
	@Test
    public void verifyChainTest() throws Exception {
    	PaicoindRpcClient client = getClient("verifychain");
    	boolean verify = client.verifyChain();
        assertEquals(verify ,true);
    }
	
	@Test
    public void verifyTxoutProofTest() throws Exception {
    	PaicoindRpcClient client = getClient("verifytxoutproof");
    	List<String> txids = client.verifyTxOutProof("00000020af04300f8c0c97a89c2fdbd0ddeeb435dc21853eae4cc2cd0c05300900000000382628ce38fe384131d04abd109f2a285bed0b68f89a4c0c2b26542d44e4643f2ad4da5e61fe091cc036ba3f0200000002b8e3fdf1998fcdaf88d02466f4c3d5ae1f09e70cee2ca7f8e2732b3848cfd37a06bcecae77d6951daf26436be626fb336f5de9c6dfc7d1b9550d9cae5a946a5d0105");
        assertEquals(txids != null && txids.size()>0 ,true);
    }
}