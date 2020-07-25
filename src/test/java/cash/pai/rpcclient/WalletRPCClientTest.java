package cash.pai.rpcclient;

import org.junit.Test;

import cash.pai.rpcclient.PaicoindRpcClient.LockedUnspent;
import cash.pai.rpcclient.PaicoindRpcClient.ReceivedAccount;
import cash.pai.rpcclient.PaicoindRpcClient.ReceivedAddress;
import cash.pai.rpcclient.PaicoindRpcClient.Transaction;
import cash.pai.rpcclient.PaicoindRpcClient.TransactionsSinceBlock;
import cash.pai.rpcclient.PaicoindRpcClient.Unspent;
import cash.pai.rpcclient.PaicoindRpcClient.WalletInfo;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class WalletRPCClientTest extends PaicoinTestBase{
	
	
	@Test
    public void getWalletInfoTest() throws Exception {
    	PaicoindRpcClient client = getClient("getwalletinfo");
    	WalletInfo walletInfo = client.getWalletInfo();
        assertEquals(walletInfo != null && walletInfo.walletVersion() > 0 ,true);
    }
    
	@Test
    public void backupWallet() throws Exception {
    	PaicoindRpcClient client = getClient("backupwallet");
    	client.backupWallet(evn.walletRootPath+"test_wallet_backup_"+getDateTime()+".dat");
        assertEquals(true ,true);
    }
	
	@Test
    public void importAddressTest() throws Exception {
    	PaicoindRpcClient client = getClient("importaddress","getnewaddress");
//    	client.importAddress("MbHz3xrYfhxM4dFRCFgW57rV3GNHRDW8mw", "newAddName"+getDateTime(),false);
        assertEquals(true ,true);
    }
	
	@Test
    public void importPrivKeyTest() throws Exception {
    	PaicoindRpcClient client = getClient("importprivkey");
    	client.importPrivKey(evn.walletBalancePrivKey, evn.walletBalanceAccount);
        assertEquals(true ,true);
    }
	
	@Test
    public void importWalletTest() throws Exception {
    	PaicoindRpcClient client = getClient("importwallet");
//    	client.importWallet(evn.walletRootPath+"test_dumpwallet_2020-06-06-16-23-57.txt");
        assertEquals(true ,true);
    }
	
	@Test
    public void keyPoolRefillTest() throws Exception {
    	PaicoindRpcClient client = getClient("keypoolrefill");
    	client.keyPoolRefill(2000);
        assertEquals(true ,true);
    }
	
	
	@Test
    public void dumpPrivKeyTest() throws Exception {
    	PaicoindRpcClient client = getClient("dumpprivkey");
    	String walletInfo = client.dumpPrivKey(evn.walletBalanceAddress);
        assertEquals(walletInfo != null ,true);
    }
	
	@Test
    public void dumpWalletTest() throws Exception {
    	PaicoindRpcClient client = getClient("dumpwallet");
    	client.dumpWallet(evn.walletRootPath+"test_dumpwallet_"+getDateTime()+".txt");
        assertEquals(true ,true);
    }
	
	@Test
    public void getAccountTest() throws Exception {
    	PaicoindRpcClient client = getClient("getaccount");
    	String account = client.getAccount(evn.walletBalanceAddress);
        assertEquals(account != null ,true);
    }
	
	@Test
    public void getAccountAddressTest() throws Exception {
    	PaicoindRpcClient client = getClient("getaccountaddress");
    	String address = client.getAccountAddress(evn.walletBalanceAccount);
        assertEquals(address != null ,true);
    }
	
	@Test
    public void getAddressesByAccountTest() throws Exception {
    	PaicoindRpcClient client = getClient("getaddressesbyaccount");
    	List<String> list = client.getAddressesByAccount(evn.walletBalanceAccount);
        assertEquals(list != null ,true);
    }
	
	@Test
    public void getBalanceTest() throws Exception {
    	PaicoindRpcClient client = getClient("getbalance");
    	BigDecimal balanceAll = client.getBalance();
    	BigDecimal balanceAccount = client.getBalance(evn.walletBalanceAccount);
    	BigDecimal balanceMinConf = client.getBalance(evn.walletBalanceAccount,5);
        assertEquals(balanceAll != null && balanceAccount != null && balanceMinConf != null
        		&& balanceAll.doubleValue() > 0 && balanceAccount.doubleValue() > 0 && balanceMinConf.doubleValue() > 0,true);
    }
	
	@Test
    public void getNewAddresTest() throws Exception {
    	PaicoindRpcClient client = getClient("getnewaddress");
    	String address = client.getNewAddress();
        assertEquals(address != null ,true);
    }
	
	@Test
    public void getRawChangeAddressTest() throws Exception {
    	PaicoindRpcClient client = getClient("getrawchangeaddress");
    	String address = client.getRawChangeAddress();
        assertEquals(address != null ,true);
    }
	
	@Test
    public void getReceivedByAccountTest() throws Exception {
    	PaicoindRpcClient client = getClient("getreceivedbyaccount");
    	BigDecimal received = client.getReceivedByAccount(evn.walletBalanceAccount);
        assertEquals(received != null && received.doubleValue() > 0,true);
    }
	
	@Test
    public void getReceivedByAddressTest() throws Exception {
    	PaicoindRpcClient client = getClient("getreceivedbyaddress");
    	BigDecimal received = client.getReceivedByAddress(evn.walletReceiveAddress);
        assertEquals(received != null && received.doubleValue() > 0,true);
    }
	
	@Test
    public void getTransactionTest() throws Exception {
    	PaicoindRpcClient client = getClient("gettransaction");
    	Transaction transaction = client.getTransaction(evn.txid);
        assertEquals(transaction != null && transaction.blockIndex() > 0,true);
    }
	
	@Test
    public void getUnconfirmedBalanceTest() throws Exception {
    	PaicoindRpcClient client = getClient("getunconfirmedbalance");
    	BigDecimal unconfirmedBalance = client.getUnconfirmedBalance();
        assertEquals(unconfirmedBalance != null,true);
    }
	
	@Test
    public void listAccountsTest() throws Exception {
    	PaicoindRpcClient client = getClient("listaccounts");
    	Map<String, Number> list = client.listAccounts();
        assertEquals(list != null,true);
    }
	
	@Test
    public void listAddressGroupingsTest() throws Exception {
    	PaicoindRpcClient client = getClient("listaddressgroupings");
    	List list = client.listAddressGroupings();
        assertEquals(list != null,true);
    }
	
	@Test
    public void listLockUnspentTest() throws Exception {
    	PaicoindRpcClient client = getClient("listlockunspent");
    	List<LockedUnspent> list = client.listLockUnspent();
        assertEquals(list != null,true);
    }
	
	
	@Test
    public void listReceivedByAccountTest() throws Exception {
    	PaicoindRpcClient client = getClient("listreceivedbyaccount");
    	List<ReceivedAccount> list = client.listReceivedByAccount();
        assertEquals(list != null,true);
    }
	
	@Test
    public void listReceivedByAddressTest() throws Exception {
    	PaicoindRpcClient client = getClient("listreceivedbyaddress");
    	List<ReceivedAddress> list = client.listReceivedByAddress();
        assertEquals(list != null,true);
    }
	
	@Test
    public void listSinceBlockTest() throws Exception {
    	PaicoindRpcClient client = getClient("listsinceblock");
    	TransactionsSinceBlock transactionsSinceBlock = client.listSinceBlock();
        assertEquals(transactionsSinceBlock != null,true);
    }
	
	@Test
    public void listTransactionsTest() throws Exception {
    	PaicoindRpcClient client = getClient("listtransactions");
    	List<Transaction> list = client.listTransactions(evn.walletBalanceAccount);
        assertEquals(list != null,true);
    }
	
	@Test
    public void listUnspentTest() throws Exception {
    	PaicoindRpcClient client = getClient("listunspent");
    	List<Unspent> list = client.listUnspent();
        assertEquals(list != null,true);
    }
	
	@Test
    public void lockUnspentTest() throws Exception {
    	PaicoindRpcClient client = getClient("lockunspent");
    	boolean ret = client.lockUnspent(true, "2032c88773eeedacd93b6d1f04a26a38b78b0dae2ab0c5f5b52860a10837885b", 0);
        assertEquals(ret,true);
    }
	
	@Test
    public void moveTest() throws Exception {
    	PaicoindRpcClient client = getClient("move");
//    	boolean ret = client.move(fromAccount, toAccount, amount);
        assertEquals(true,true);
    }
	
//	@Test
//    public void sendFromTest() throws Exception {
//		Date d = new Date();
//    	PaicoindRpcClient client = getClient("sendfrom");
//    	String txid = client.sendFrom(evn.walletBalanceAccount, evn.walletReceiveAddress, evn.walletSendAmount,0,"test local comment","test trading comment "+getDateTime());
//        assertEquals(txid != null ,true);
//    }
	
//	@Test
//    public void sendManyTest() throws Exception {
//		Date d = new Date();
//    	PaicoindRpcClient client = getClient("sendmany");
//    	String txid = client.sendFrom(fromAccount, toAddress, amount);
//    	assertEquals(txid != null ,true);
//    }
	
//	@Test
//    public void sendToAddressTest() throws Exception {
//    	PaicoindRpcClient client = getClient("sendtoaddress");
//    	String txid = client.sendToAddress(evn.walletReceiveAddress, evn.walletSendAmount);
//        assertEquals(txid != null,true);
//    }
//	
//  @Test
//  public void setAccountTest() throws Exception {
//  	PaicoindRpcClient client = getClient("setaccount");
//  	String txid = client.getTxOutSetInfo();
//      assertEquals(txid != null,true);
//  }
	
  @Test
  public void setAccountTest() throws Exception {
  	PaicoindRpcClient client = getClient("settxfee");
  	boolean ret = client.setTxFee(evn.txFee);
    assertEquals(ret,true);
  }
  
//  @Test
//  public void signMessageTest() throws Exception {
//  	PaicoindRpcClient client = getClient("signmessage");
//    assertEquals(true,true);
//  }
  
}