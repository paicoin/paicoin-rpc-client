package org.paicoin.rpcclient;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaicoinTestEvn {
	private static final Logger logger = Logger.getLogger(PaicoindRpcClient.class.getPackage().getName());
	
	private static PaicoinTestEvn instence;
	
	public boolean testNet = true;
	public long version = 2010000;
	public long protocolVersion = 70015;
	public String subversion = "/Turing:2.1.0/";
	
	//钱包余额私钥
	public String walletBalancePrivKey = "";
	public String walletBalanceAddress = "";
	public String walletBalanceAccount = "";
	
	//钱包目标私钥
	public String walletReceivePrivKey = "";
	public String walletReceiveAddress = "";
	public String walletReceiveAccount = "";
	
	public BigDecimal walletSendAmount = BigDecimal.valueOf(0.0001);
	
	
	public String walletRootPath = "/root/.paicoin/";
	
	public String txid = "";
	
	public BigDecimal txFee = BigDecimal.valueOf(0.000001);
	
	public static PaicoinTestEvn getInstence() {
		if (instence == null) {
			instence = new PaicoinTestEvn();
		}
		return instence;
	}
	
	public PaicoinTestEvn() {
		super();
		try {
	      File f;
	      File home = new File(System.getProperty("user.home"));
	      
	      if ((f = new File(home, ".paicoin" + File.separatorChar + "paicoin_rpc_test.conf")).exists()) {
	      } else {
	        f = null;
	      }
	      
	      if (f != null) {
	        logger.fine("Paicoin RPC test configuration file found");

	        Properties p = new Properties();
	        try (FileInputStream i = new FileInputStream(f)) {
	          p.load(i);
	        }

	        testNet = "true".equalsIgnoreCase(p.getProperty("isTestNet", "true"));
	        version = Long.parseLong(p.getProperty("version", "160100"));
	        protocolVersion = Long.parseLong(p.getProperty("protocolVersion", "70015"));
	        subversion = p.getProperty("subversion", "/Turing:0.16.1/");
	        
	        walletBalancePrivKey = p.getProperty("walletBalancePrivKey", "");
	        walletBalanceAddress = p.getProperty("walletBalanceAddress", "");
	        walletBalanceAccount = p.getProperty("walletBalanceAccount", "");
	        
	        walletReceivePrivKey = p.getProperty("walletReceivePrivKey", "");
	        walletReceiveAddress = p.getProperty("walletReceiveAddress", "");
	        walletReceiveAccount = p.getProperty("walletReceiveAccount", "");
	        
	        walletSendAmount = BigDecimal.valueOf(Double.valueOf(p.getProperty("walletSendAmount", "0.0001")));
	        
	        walletRootPath = p.getProperty("walletRootPath", "/root/.paicoin/");
	        txid = p.getProperty("txid", "");
	        txFee = BigDecimal.valueOf(Double.valueOf(p.getProperty("txFee", "0.000001")));
	        
	      }
	    } catch (Exception ex) {
	      logger.log(Level.SEVERE, null, ex);
	    }
	}
}
