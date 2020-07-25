/*
 * Paicoin-RPC-Client License
 * 
 * Copyright (c) 2013, Mikhail Yevchenko.
 * Copyright (c) 2020, Pai.Cash. All rights reserved.
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
 /*
 * Repackaged with simple additions for easier maven usage by Alessandro Polverini
 */
package cash.pai.rpcclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import cash.pai.krotjson.Base64Coder;
import cash.pai.krotjson.HexCoder;
import cash.pai.krotjson.JSON;

/**
 *
 */
public class PaicoinJSONRPCClient implements PaicoindRpcClient {

  private static final Logger logger = Logger.getLogger(PaicoindRpcClient.class.getPackage().getName());

  public static final URL DEFAULT_JSONRPC_URL;
  public static final URL DEFAULT_JSONRPC_TESTNET_URL;
  public static final URL DEFAULT_JSONRPC_REGTEST_URL;

  static {
    String user = "user";
    String password = "pass";
    String host = "localhost";
    String port = null;

    try {
      File f;
      File home = new File(System.getProperty("user.home"));

      if ((f = new File(home, ".paicoin" + File.separatorChar + "paicoin.conf")).exists()) {
      } else if ((f = new File(home, "AppData" + File.separatorChar + "Roaming" + File.separatorChar + "Paicoin" + File.separatorChar + "paicoin.conf")).exists()) {
      } else {
        f = null;
      }

      if (f != null) {
        logger.fine("Paicoin configuration file found");

        Properties p = new Properties();
        try (FileInputStream i = new FileInputStream(f)) {
          p.load(i);
        }

        user = p.getProperty("rpcuser", user);
        password = p.getProperty("rpcpassword", password);
        host = p.getProperty("rpcconnect", host);
        port = p.getProperty("rpcport", port);
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    }

    try {
      DEFAULT_JSONRPC_URL = new URL("http://" + user + ':' + password + "@" + host + ":" + (port == null ? "8566" : port) + "/");
      DEFAULT_JSONRPC_TESTNET_URL = new URL("http://" + user + ':' + password + "@" + host + ":" + (port == null ? "18566" : port) + "/");
      DEFAULT_JSONRPC_REGTEST_URL = new URL("http://" + user + ':' + password + "@" + host + ":" + (port == null ? "19566" : port) + "/");
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public final URL rpcURL;

  private URL noAuthURL;
  private String authStr;

  public PaicoinJSONRPCClient(String rpcUrl) throws MalformedURLException {
    this(new URL(rpcUrl));
  }

  public PaicoinJSONRPCClient(URL rpc) {
    this.rpcURL = rpc;
    try {
      noAuthURL = new URI(rpc.getProtocol(), null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), null).toURL();
    } catch (URISyntaxException ex) {
      throw new IllegalArgumentException(rpc.toString(), ex);
    } catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    authStr = rpc.getUserInfo() == null ? null : String.valueOf(Base64Coder.encode(rpc.getUserInfo().getBytes(Charset.forName("UTF-8"))));
  }

  public PaicoinJSONRPCClient(boolean testNet) {
    this(testNet ? DEFAULT_JSONRPC_TESTNET_URL : DEFAULT_JSONRPC_URL);
  }

  public PaicoinJSONRPCClient() {
    this(DEFAULT_JSONRPC_TESTNET_URL);
  }

  private HostnameVerifier hostnameVerifier = null;
  private SSLSocketFactory sslSocketFactory = null;

  public HostnameVerifier getHostnameVerifier() {
    return hostnameVerifier;
  }

  public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
    this.hostnameVerifier = hostnameVerifier;
  }

  public SSLSocketFactory getSslSocketFactory() {
    return sslSocketFactory;
  }

  public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
    this.sslSocketFactory = sslSocketFactory;
  }

  public static final Charset QUERY_CHARSET = Charset.forName("UTF-8");

  public byte[] prepareRequest(final String method, final Object... params) {
    return JSON.stringify(new LinkedHashMap() {
      {
    	put("id", "1");
        put("method", method);
        put("params", params);
        
      }
    }).getBytes(QUERY_CHARSET);
  }

  private static byte[] loadStream(InputStream in, boolean close) throws IOException {
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    for (;;) {
      int nr = in.read(buffer);

      if (nr == -1)
        break;
      if (nr == 0)
        throw new IOException("Read timed out");

      o.write(buffer, 0, nr);
    }
    return o.toByteArray();
  }

  @SuppressWarnings("rawtypes")
  public Object loadResponse(InputStream in, Object expectedID, boolean close) throws IOException, GenericRpcException {
    try {
      String r = new String(loadStream(in, close), QUERY_CHARSET);
      logger.log(Level.FINE, "Paicoin JSON-RPC response:\n{0}", r);
      try {
        Map response = (Map) JSON.parse(r);

        if (!expectedID.equals(response.get("id")))
          throw new PaicoinRPCException("Wrong response ID (expected: " + String.valueOf(expectedID) + ", response: " + response.get("id") + ")");

        if (response.get("error") != null)
          throw new PaicoinRPCException(new PaicoinRPCError((Map)response.get("error")));

        return response.get("result");
      } catch (ClassCastException ex) {
        throw new PaicoinRPCException("Invalid server response format (data: \"" + r + "\")");
      }
    } finally {
      if (close)
        in.close();
    }
  }

  public Object query(String method, Object... o) throws GenericRpcException {
    HttpURLConnection conn;
    try {
      conn = (HttpURLConnection) noAuthURL.openConnection();

      conn.setDoOutput(true);
      conn.setDoInput(true);

      if (conn instanceof HttpsURLConnection) {
        if (hostnameVerifier != null)
          ((HttpsURLConnection) conn).setHostnameVerifier(hostnameVerifier);
        if (sslSocketFactory != null)
          ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
      }

            
      ((HttpURLConnection) conn).setRequestProperty("Authorization", "Basic " + authStr);
      
      conn.connect();
      
      byte[] r = prepareRequest(method, o);
      logger.log(Level.FINE, "Paicoin JSON-RPC request:\n{0}", new String(r, QUERY_CHARSET));
      conn.getOutputStream().write(r);
      conn.getOutputStream().close();
      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        InputStream errorStream = conn.getErrorStream();
        throw new PaicoinRPCException(method, 
                                      Arrays.deepToString(o), 
                                      responseCode, 
                                      conn.getResponseMessage(), 
                                      errorStream == null ? null : new String(loadStream(errorStream, true)));
      }
      return loadResponse(conn.getInputStream(), "1", true);
    } catch (IOException ex) {
      throw new PaicoinRPCException(method, Arrays.deepToString(o), ex);
    }
  }

  public String createRawTransaction(List<TxInput> inputs, List<TxOutput> outputs) throws GenericRpcException {
    List<Map> pInputs = new ArrayList<Map>();

    for (final TxInput txInput : inputs) {
      pInputs.add(new LinkedHashMap() {
        {
          put("txid", txInput.txid());
          put("vout", txInput.vout());
        }
      });
    }

    Map<String, Object> pOutputs = new LinkedHashMap<String, Object>();
    
    for (TxOutput txOutput : outputs) {
      pOutputs.put(txOutput.address(), txOutput.amount());
      if (txOutput.data() != null) {
        String hex = HexCoder.encode(txOutput.data());
        pOutputs.put("data", hex);
      }
    }

    return (String) query("createrawtransaction", pInputs, pOutputs);
  }

  public String dumpPrivKey(String address) throws GenericRpcException {
    return (String) query("dumpprivkey", address);
  }

  public String getAccount(String address) throws GenericRpcException {
    return (String) query("getaccount", address);
  }

  public String getAccountAddress(String account) throws GenericRpcException {
    return (String) query("getaccountaddress", account);
  }

  public List<String> getAddressesByAccount(String account) throws GenericRpcException {
    return (List<String>) query("getaddressesbyaccount", account);
  }

  public BigDecimal getBalance() throws GenericRpcException {
    return (BigDecimal) query("getbalance");
  }

  public BigDecimal getBalance(String account) throws GenericRpcException {
    return (BigDecimal) query("getbalance", account);
  }

  public BigDecimal getBalance(String account, int minConf) throws GenericRpcException {
    return (BigDecimal) query("getbalance", account, minConf);
  }

  public SmartFeeResult estimateSmartFee(int blocks) {
    return new SmartFeeResultMapWrapper((Map) query("estimatesmartfee", blocks));
  }

  private class InfoWrapper extends MapWrapper implements Info, Serializable {

    public InfoWrapper(Map m) {
      super(m);
    }

    public BigDecimal balance() {
      return mapBigDecimal("balance");
    }

    public int blocks() {
      return mapInt("blocks");
    }

    public int connections() {
      return mapInt("connections");
    }

    public BigDecimal difficulty() {
      return mapBigDecimal("difficulty");
    }

    public String errors() {
      return mapStr("errors");
    }

    public long keyPoolOldest() {
      return mapLong("keypoololdest");
    }

    public long keyPoolSize() {
      return mapLong("keypoolsize");
    }

    public BigDecimal payTxFee() {
      return mapBigDecimal("paytxfee");
    }

    public long protocolVersion() {
      return mapLong("protocolversion");
    }

    public String proxy() {
      return mapStr("proxy");
    }

    public BigDecimal relayFee() {
      return mapBigDecimal("relayfee");
    }

    public boolean testnet() {
      return mapBool("testnet");
    }

    public int timeOffset() {
      return mapInt("timeoffset");
    }

    public long version() {
      return mapLong("version");
    }

    public long walletVersion() {
      return mapLong("walletversion");
    }

  }

  private class TxOutSetInfoWrapper extends MapWrapper implements TxOutSetInfo, Serializable {

    public TxOutSetInfoWrapper(Map m) {
      super(m);
    }

    public long height() {
      return mapInt("height");
    }

    public String bestBlock() {
      return mapStr("bestBlock");
    }

    public long transactions() {
      return mapInt("transactions");
    }

    public long txouts() {
      return mapInt("txouts");
    }

    public long bytesSerialized() {
      return mapInt("bytes_serialized");
    }

    public String hashSerialized() {
      return mapStr("hash_serialized");
    }

    public BigDecimal totalAmount() {
      return mapBigDecimal("total_amount");
    }
  }

  private class WalletInfoWrapper extends MapWrapper implements WalletInfo, Serializable {

    public WalletInfoWrapper(Map m) {
      super(m);
    }

    public long walletVersion() {
      return mapLong("walletversion");
    }

    public BigDecimal balance() {
      return mapBigDecimal("balance");
    }
    public BigDecimal unconfirmedBalance() {
      return mapBigDecimal("unconfirmed_balance");
    }

    public BigDecimal immatureBalance() {
      return mapBigDecimal("immature_balance");
    }

    public long txCount() {
      return mapLong("txcount");
    }

    public long keyPoolOldest() {
      return mapLong("keypoololdest");
    }

    public long keyPoolSize() {
      return mapLong("keypoolsize");
    }

    public long unlockedUntil() {
      return mapLong("unlocked_until");
    }

    public BigDecimal payTxFee() {
      return mapBigDecimal("paytxfee");
    }

    public String hdMasterKeyId() {
      return mapStr("hdmasterkeyid");
    }
  }

  private class NetworkInfoWrapper extends MapWrapper implements NetworkInfo, Serializable {

    public NetworkInfoWrapper(Map m) {
      super(m);
    }

    public long version() {
      return mapLong("version");
    }

    public String subversion() {
      return mapStr("subversion");
    }

    public long protocolVersion() {
      return mapLong("protocolversion");
    }

    public String localServices() {
      return mapStr("localservices");
    }

    public boolean localRelay() {
      return mapBool("localrelay");
    }

    public long timeOffset() {
      return mapLong("timeoffset");
    }

    public long connections() {
      return mapLong("connections");
    }

    public List<Network> networks() {
      List<Map> maps = (List<Map>) m.get("networks");
      List<Network> networks = new LinkedList<Network>();
      for (Map m : maps) {
        Network net = new NetworkWrapper(m);
        networks.add(net);
      }
      return networks;
    }

    public BigDecimal relayFee() {
      return mapBigDecimal("relayfee");
    }

    public List<String> localAddresses() {
      return (List<String>) m.get("localaddresses");
    }

    public String warnings() {
      return mapStr("warnings");
    }
  }

  private class NetworkWrapper extends MapWrapper implements Network, Serializable {

    public NetworkWrapper(Map m) {
      super(m);
    }

    public String name() {
      return mapStr("name");
    }

    public boolean limited() {
      return mapBool("limited");
    }

    public boolean reachable() {
      return mapBool("reachable");
    }

    public String proxy() {
      return mapStr("proxy");
    }

    public boolean proxyRandomizeCredentials() {
      return mapBool("proxy_randomize_credentials");
    }
  }

  private class MultiSigWrapper extends MapWrapper implements MultiSig, Serializable {

    public MultiSigWrapper(Map m) {
      super(m);
    }

    public String address() {
      return mapStr("address");
    }

    public String redeemScript() {
      return mapStr("redeemScript");
    }
  }

  private class NodeInfoWrapper extends MapWrapper implements NodeInfo, Serializable {

    public NodeInfoWrapper(Map m) {
      super(m);
    }

    public String addedNode() {
      return mapStr("addednode");
    }

    public boolean connected() {
      return mapBool("connected");
    }

    public List<Address> addresses() {
      List<Map> maps = (List<Map>) m.get("addresses");
      List<Address> addresses = new LinkedList<Address>();
      for (Map m : maps) {
        Address add = new AddressWrapper(m);
        addresses.add(add);
      }
      return addresses;
    }
  }

  private class AddressWrapper extends MapWrapper implements Address, Serializable {

    public AddressWrapper(Map m) {
      super(m);
    }

    public String address() {
      return mapStr("address");
    }

    public String connected() {
      return mapStr("connected");
    }
  }

  @SuppressWarnings("serial")
  private class TransactionWrapper extends MapWrapper implements Transaction, Serializable {

    @SuppressWarnings("rawtypes")
    public TransactionWrapper(Map m) {
      super(m);
    }

    public String account() {
      return mapStr(m, "account");
    }

    public String address() {
      return mapStr(m, "address");
    }

    public String category() {
      return mapStr(m, "category");
    }

    public BigDecimal amount() {
      return mapBigDecimal(m, "amount");
    }

    public BigDecimal fee() {
      return mapBigDecimal(m, "fee");
    }

    public int confirmations() {
      return mapInt(m, "confirmations");
    }

    public String blockHash() {
      return mapStr(m, "blockhash");
    }

    public int blockIndex() {
      return mapInt(m, "blockindex");
    }

    public Date blockTime() {
      return mapCTime(m, "blocktime");
    }

    public String txId() {
      return mapStr(m, "txid");
    }

    
    public Date time() {
      return mapCTime(m, "time");
    }

    
    public Date timeReceived() {
      return mapCTime(m, "timereceived");
    }

    
    public String comment() {
      return mapStr(m, "comment");
    }

    
    public String commentTo() {
      return mapStr(m, "to");
    }

    
    public boolean generated() {
      return mapBool(m, "generated");
    }

    private RawTransaction raw = null;

    
    public RawTransaction raw() {
      if (raw == null)
        try {
          raw = getRawTransaction(txId());
        } catch (GenericRpcException ex) {
          logger.warning(ex.getMessage());
        }
      return raw;
    }

    
    public String toString() {
      return m.toString();
    }
  }
  
  @SuppressWarnings("serial")
  private class TxOutWrapper extends MapWrapper implements TxOut, Serializable {

    @SuppressWarnings("rawtypes")
    public TxOutWrapper(Map m) {
      super(m);
    }

    
    public String bestBlock() {
      return mapStr("bestblock");
    }

    
    public long confirmations() {
      return mapLong("confirmations");
    }

    
    public BigDecimal value() {
      return mapBigDecimal("value");
    }

    
    public String asm() {
      return mapStr("asm");
    }

    
    public String hex() {
      return mapStr("hex");
    }

    
    public long reqSigs() {
      return mapLong("reqSigs");
    }

    
    public String type() {
      return mapStr("type");
    }

    
    public List<String> addresses() {
      return (List<String>) m.get("addresses");
    }

    
    public long version() {
      return mapLong("version");
    }

    
    public boolean coinBase() {
      return mapBool("coinbase");
    }
  }

  
  
  private class BlockTemplateWrapper extends MapWrapper implements BlockTemplate, Serializable {

	public BlockTemplateWrapper(Map m) {
      super(m);
    }
	
	@Override
	public List<String> capabilities() {
		return (List<String>) m.get("capabilities");
	}

	@Override
	public long version() {
		return mapLong("version");
	}

	@Override
	public List<String> rules() {
		return (List<String>) m.get("rules");
	}

	@Override
	public Map<String, String> vbavailable() {
		return (Map<String, String> ) m.get("vbavailable");
	}

	@Override
	public int vbrequired() {
		return mapInt("vbrequired");
	}

	@Override
	public String previousblockhash() {
		return mapStr("previousblockhash");
	}

	@Override
	public List<String> transactions() {
		return (List<String>) m.get("transactions");
	}

	@Override
	public Map<String, String> coinbaseaux() {
		return (Map<String, String> ) m.get("coinbaseaux");
	}

	@Override
	public long coinbasevalue() {
		return mapLong("coinbasevalue");
	}

	@Override
	public String longpollid() {
		return mapStr("longpollid");
	}

	@Override
	public String target() {
		return mapStr("target");
	}

	@Override
	public long mintime() {
		return mapLong("mintime");
	}

	@Override
	public List<String> mutable() {
		return (List<String>) m.get("mutable");
	}

	@Override
	public String noncerange() {
		return mapStr("noncerange");
	}

	@Override
	public long sigoplimit() {
		return mapLong("sigoplimit");
	}

	@Override
	public long sizelimit() {
		return mapLong("sizelimit");
	}

	@Override
	public long weightlimit() {
		return mapLong("weightlimit");
	}

	@Override
	public long curtime() {
		return mapLong("curtime");
	}

	@Override
	public String bits() {
		return mapStr("bits");
	}

	@Override
	public long height() {
		return mapLong("height");
	}
	  
  }
  
  private class MiningInfoWrapper extends MapWrapper implements MiningInfo, Serializable {

    public MiningInfoWrapper(Map m) {
      super(m);
    }

    
    public int blocks() {
      return mapInt("blocks");
    }

    
    public int currentBlockSize() {
      return mapInt("currentblocksize");
    }

    
    public int currentBlockWeight() {
      return mapInt("currentblockweight");
    }

    
    public int currentBlockTx() {
      return mapInt("currentblocktx");
    }

    
    public BigDecimal difficulty() {
      return mapBigDecimal("difficulty");
    }

    
    public String errors() {
      return mapStr("errors");
    }

    
    public BigDecimal networkHashps() {
      return mapBigDecimal("networkhashps");
    }

    
    public int pooledTx() {
      return mapInt("pooledtx");
    }

    
    public boolean testNet() {
      return mapBool("testnet");
    }

    
    public String chain() {
      return mapStr("chain");
    }
  }

  private class BlockChainInfoMapWrapper extends MapWrapper implements BlockChainInfo, Serializable {

    public BlockChainInfoMapWrapper(Map m) {
      super(m);
    }

    
    public String chain() {
      return mapStr("chain");
    }

    
    public int blocks() {
      return mapInt("blocks");
    }

    
    public String bestBlockHash() {
      return mapStr("bestblockhash");
    }

    
    public BigDecimal difficulty() {
      return mapBigDecimal("difficulty");
    }

    
    public BigDecimal verificationProgress() {
      return mapBigDecimal("verificationprogress");
    }

    
    public String chainWork() {
      return mapStr("chainwork");
    }
  }

  @SuppressWarnings("serial")
  private class SmartFeeResultMapWrapper extends MapWrapper implements SmartFeeResult, Serializable {

    public SmartFeeResultMapWrapper(Map<String, ?> m) {
      super(m);
    }

    
    public BigDecimal feeRate() {
      return mapBigDecimal("feerate");
    }

    
    public int blocks() {
      return mapInt("blocks");
    }

    
    public String errors() {
      return mapStr("errors");
    }
  }

  @SuppressWarnings("serial")
  private class BlockMapWrapper extends MapWrapper implements Block, Serializable {

    public BlockMapWrapper(Map<String, ?> m) {
      super(m);
    }

    
    public String hash() {
      return mapStr("hash");
    }

    
    public int confirmations() {
      return mapInt("confirmations");
    }

    
    public int size() {
      return mapInt("size");
    }

    
    public int height() {
      return mapInt("height");
    }

    
    public int version() {
      return mapInt("version");
    }

    
    public String merkleRoot() {
      return mapStr("merkleroot");
    }

    
    public String chainwork() {
      return mapStr("chainwork");
    }

    
    @SuppressWarnings("unchecked")
    public List<String> tx() {
      return (List<String>) m.get("tx");
    }

    
    public Date time() {
      return mapCTime("time");
    }

    
    public long nonce() {
      return mapLong("nonce");
    }

    
    public String bits() {
      return mapStr("bits");
    }

    
    public BigDecimal difficulty() {
      return mapBigDecimal("difficulty");
    }

    
    public String previousHash() {
      return mapStr("previousblockhash");
    }

    
    public String nextHash() {
      return mapStr("nextblockhash");
    }

    
    public Block previous() throws GenericRpcException {
      if (!m.containsKey("previousblockhash"))
        return null;
      return getBlock(previousHash());
    }

    
    public Block next() throws GenericRpcException {
      if (!m.containsKey("nextblockhash"))
        return null;
      return getBlock(nextHash());
    }

  }

  
  public Block getBlock(int height) throws GenericRpcException {
    String hash = (String) query("getblockhash", height);
    return getBlock(hash);
  }

  
  @SuppressWarnings({ "unchecked" })
  public Block getBlock(String blockHash) throws GenericRpcException {
    return new BlockMapWrapper((Map<String, ?>) query("getblock", blockHash));
  }

  
  public String getRawBlock(String blockHash) throws GenericRpcException {
    return (String) query("getblock", blockHash, false);
  }

  
  public String getBlockHash(int height) throws GenericRpcException {
    return (String) query("getblockhash", height);
  }

  public List<ChainTips> getChainTips() throws GenericRpcException {
	List<Map> list = ((List<Map>) query("getchaintips"));
    List<ChainTips> chainTipsList = new LinkedList<ChainTips>();
    for (Map m : list) {
    	ChainTipsWrapper ctw = new ChainTipsWrapper(m);
    	chainTipsList.add(ctw);
    }
    return chainTipsList;
  }
  
  @SuppressWarnings({ "unchecked" })
  public BlockChainInfo getBlockChainInfo() throws GenericRpcException {
    return new BlockChainInfoMapWrapper((Map<String, ?>) query("getblockchaininfo"));
  }

  
  public int getBlockCount() throws GenericRpcException {
    return ((Number) query("getblockcount")).intValue();
  }

  
  @SuppressWarnings({ "unchecked" })
  public Info getInfo() throws GenericRpcException {
    return new InfoWrapper((Map<String, ?>) query("getinfo"));
  }

  
  @SuppressWarnings({ "unchecked" })
  public TxOutSetInfo getTxOutSetInfo() throws GenericRpcException {
    return new TxOutSetInfoWrapper((Map<String, ?>) query("gettxoutsetinfo"));
  }

  
  @SuppressWarnings({ "unchecked" })
  public NetworkInfo getNetworkInfo() throws GenericRpcException {
    return new NetworkInfoWrapper((Map<String, ?>) query("getnetworkinfo"));
  }

  public BlockTemplate getBlockTemplate() throws GenericRpcException{
	  return new BlockTemplateWrapper((Map<String, ?>) query("getblocktemplate"));
  }
  
  @SuppressWarnings({ "unchecked" })
  public MiningInfo getMiningInfo() throws GenericRpcException {
    return new MiningInfoWrapper((Map<String, ?>) query("getmininginfo"));
  }

  public List<NodeInfo> getAddedNodeInfo() throws GenericRpcException {
	  return getAddedNodeInfo(null);
  }
  
  public List<NodeInfo> getAddedNodeInfo(String node) throws GenericRpcException {
    List<Map> list = ((List<Map>) query("getaddednodeinfo", node));
    List<NodeInfo> nodeInfoList = new LinkedList<NodeInfo>();
    for (Map m : list) {
      NodeInfoWrapper niw = new NodeInfoWrapper(m);
      nodeInfoList.add(niw);
    }
    return nodeInfoList;
  }

  
  public MultiSig createMultiSig(int nRequired, List<String> keys) throws GenericRpcException {
    return new MultiSigWrapper((Map) query("createmultisig", nRequired, keys));
  }

  
  public WalletInfo getWalletInfo() {
    return new WalletInfoWrapper((Map) query("getwalletinfo"));
  }

  
  public String getNewAddress() throws GenericRpcException {
    return (String) query("getnewaddress");
  }

  
  public String getNewAddress(String account) throws GenericRpcException {
    return (String) query("getnewaddress", account);
  }

  
  public String getNewAddress(String account, String addressType) throws GenericRpcException {
    return (String) query("getnewaddress", account, addressType);
  }

  
  public List<String> getRawMemPool() throws GenericRpcException {
    return (List<String>) query("getrawmempool");
  }

  
  public String getBestBlockHash() throws GenericRpcException {
    return (String) query("getbestblockhash");
  }

  
  public String getRawTransactionHex(String txId) throws GenericRpcException {
    return (String) query("getrawtransaction", txId);
  }

  private class RawTransactionImpl extends MapWrapper implements RawTransaction, Serializable {

    public RawTransactionImpl(Map<String, Object> tx) {
      super(tx);
    }

    
    public String hex() {
      return mapStr("hex");
    }

    
    public String txId() {
      return mapStr("txid");
    }

    
    public int version() {
      return mapInt("version");
    }

    
    public long lockTime() {
      return mapLong("locktime");
    }

    
    public String hash() {
      return mapStr("hash");
    }

    
    public long size() {
      return mapLong("size");
    }

    
    public long vsize() {
      return mapLong("vsize");
    }

    private class InImpl extends MapWrapper implements In, Serializable {

      public InImpl(Map m) {
        super(m);
      }

      
      public String txid() {
        return mapStr("txid");
      }

      
      public Integer vout() {
        return mapInt("vout");
      }

      
      public Map<String, Object> scriptSig() {
        return (Map) m.get("scriptSig");
      }

      
      public long sequence() {
        return mapLong("sequence");
      }

      
      public RawTransaction getTransaction() {
        try {
          return getRawTransaction(mapStr("txid"));
        } catch (GenericRpcException ex) {
          throw new RuntimeException(ex);
        }
      }

      
      public Out getTransactionOutput() {
        return getTransaction().vOut().get(mapInt("vout"));
      }

      
      public String scriptPubKey() {
        return mapStr("scriptPubKey");
      }

      
      public String address() {
          return mapStr("address");
      }
    }

    
    public List<In> vIn() {
      final List<Map<String, Object>> vIn = (List<Map<String, Object>>) m.get("vin");
      return new AbstractList<In>() {

        
        public In get(int index) {
          return new InImpl(vIn.get(index));
        }

        
        public int size() {
          return vIn.size();
        }
      };
    }

    private class OutImpl extends MapWrapper implements Out, Serializable {

      public OutImpl(Map m) {
        super(m);
      }

      
      public BigDecimal value() {
        return mapBigDecimal("value");
      }

      
      public int n() {
        return mapInt("n");
      }

      private class ScriptPubKeyImpl extends MapWrapper implements ScriptPubKey, Serializable {

        public ScriptPubKeyImpl(Map m) {
          super(m);
        }

        
        public String asm() {
          return mapStr("asm");
        }

        
        public String hex() {
          return mapStr("hex");
        }

        
        public int reqSigs() {
          return mapInt("reqSigs");
        }

        
        public String type() {
          return mapStr("type");
        }

        
        public List<String> addresses() {
          return (List) m.get("addresses");
        }

      }

      
      public ScriptPubKey scriptPubKey() {
        return new ScriptPubKeyImpl((Map) m.get("scriptPubKey"));
      }

      
      public TxInput toInput() {
        return new BasicTxInput(transaction().txId(), n());
      }

      
      public RawTransaction transaction() {
        return RawTransactionImpl.this;
      }

    }

    
    public List<Out> vOut() {
      final List<Map<String, Object>> vOut = (List<Map<String, Object>>) m.get("vout");
      return new AbstractList<Out>() {

        
        public Out get(int index) {
          return new OutImpl(vOut.get(index));
        }

        
        public int size() {
          return vOut.size();
        }
      };
    }

    
    public String blockHash() {
      return mapStr("blockhash");
    }

    
    public Integer confirmations() {
      Object o = m.get("confirmations");
      return o == null ? null : ((Number)o).intValue();
    }

    
    public Date time() {
      return mapCTime("time");
    }

    
    public Date blocktime() {
      return mapCTime("blocktime");
    }

    
    public long height()
    {
        return mapLong("height");
    }

  }

  private class DecodedScriptImpl extends MapWrapper implements DecodedScript, Serializable {

    public DecodedScriptImpl(Map m) {
      super(m);
    }

    
    public String asm() {
      return mapStr("asm");
    }

    
    public String hex() {
      return mapStr("hex");
    }

    
    public String type() {
      return mapStr("type");
    }

    
    public int reqSigs() {
      return mapInt("reqSigs");
    }

    
    public List<String> addresses() {
      return (List) m.get("addresses");
    }

    
    public String p2sh() {
      return mapStr("p2sh");
    }
  }

  public class NetTotalsImpl extends MapWrapper implements NetTotals, Serializable {

    public NetTotalsImpl(Map m) {
      super(m);
    }

    
    public long totalBytesRecv() {
      return mapLong("totalbytesrecv");
    }

    
    public long totalBytesSent() {
      return mapLong("totalbytessent");
    }

    
    public long timeMillis() {
      return mapLong("timemillis");
    }

    public class uploadTargetImpl extends MapWrapper implements uploadTarget, Serializable {

      public uploadTargetImpl(Map m) {
        super(m);
      }

      
      public long timeFrame() {
        return mapLong("timeframe");
      }

      
      public int target() {
        return mapInt("target");
      }

      
      public boolean targetReached() {
        return mapBool("targetreached");
      }

      
      public boolean serveHistoricalBlocks() {
        return mapBool("servehistoricalblocks");
      }

      
      public long bytesLeftInCycle() {
        return mapLong("bytesleftincycle");
      }

      
      public long timeLeftInCycle() {
        return mapLong("timeleftincycle");
      }
    }

    
    public NetTotals.uploadTarget uploadTarget() {
      return new uploadTargetImpl((Map) m.get("uploadtarget"));
    }
  }

  
  public RawTransaction getRawTransaction(String txId) throws GenericRpcException {
    return new RawTransactionImpl((Map) query("getrawtransaction", txId, 1));
  }

  
  public BigDecimal getReceivedByAddress(String address) throws GenericRpcException {
    return (BigDecimal) query("getreceivedbyaddress", address);
  }

  
  public BigDecimal getReceivedByAddress(String address, int minConf) throws GenericRpcException {
    return (BigDecimal) query("getreceivedbyaddress", address, minConf);
  }

  
  public void importPrivKey(String paicoinPrivKey) throws GenericRpcException {
    query("importprivkey", paicoinPrivKey);
  }

  
  public void importPrivKey(String paicoinPrivKey, String label) throws GenericRpcException {
    query("importprivkey", paicoinPrivKey, label);
  }

  
  public void importPrivKey(String paicoinPrivKey, String label, boolean rescan) throws GenericRpcException {
    query("importprivkey", paicoinPrivKey, label, rescan);
  }

  
  public Object importAddress(String address, String label, boolean rescan) throws GenericRpcException {
    query("importaddress", address, label, rescan);
    return null;
  }

  
  @SuppressWarnings("unchecked")
  public Map<String, Number> listAccounts() throws GenericRpcException {
    return (Map<String, Number>) query("listaccounts");
  }

  
  @SuppressWarnings("unchecked")
  public Map<String, Number> listAccounts(int minConf) throws GenericRpcException {
    return (Map<String, Number>) query("listaccounts", minConf);
  }

  
  @SuppressWarnings("unchecked")
  public Map<String, Number> listAccounts(int minConf, boolean watchonly) throws GenericRpcException {
    return (Map<String, Number>) query("listaccounts", minConf, watchonly);
  }

  private static class ReceivedAddressListWrapper extends AbstractList<ReceivedAddress> {

    private final List<Map<String, Object>> wrappedList;

    public ReceivedAddressListWrapper(List<Map<String, Object>> wrappedList) {
      this.wrappedList = wrappedList;
    }

    
    public ReceivedAddress get(int index) {
      final Map<String, Object> e = wrappedList.get(index);
      return new ReceivedAddress() {

        
        public String address() {
          return (String) e.get("address");
        }

        
        public String account() {
          return (String) e.get("account");
        }

        
        public BigDecimal amount() {
          return (BigDecimal) e.get("amount");
        }

        
        public int confirmations() {
          return ((Number) e.get("confirmations")).intValue();
        }

        
        public String toString() {
          return e.toString();
        }

      };
    }

    
    public int size() {
      return wrappedList.size();
    }
  }
  
  private static class ReceivedAccountListWrapper extends AbstractList<ReceivedAccount> {

    private final List<Map<String, Object>> wrappedList;

    public ReceivedAccountListWrapper(List<Map<String, Object>> wrappedList) {
      this.wrappedList = wrappedList;
    }

    
    public ReceivedAccount get(int index) {
      final Map<String, Object> e = wrappedList.get(index);
      return new ReceivedAccount() {

        
        public String account() {
          return (String) e.get("account");
        }

        
        public BigDecimal amount() {
          return (BigDecimal) e.get("amount");
        }

        
        public int confirmations() {
          return ((Number) e.get("confirmations")).intValue();
        }

        
        public String toString() {
          return e.toString();
        }

      };
    }

    
    public int size() {
      return wrappedList.size();
    }
  }
  
  public List<List<List<String>>> listAddressGroupings() throws GenericRpcException {
	  return (List) query("listaddressgroupings");
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<LockedUnspent> listLockUnspent() {
    
    return new ListMapWrapper<LockedUnspent>((List<Map>) query("listlockunspent")) {
      
      @SuppressWarnings({ "serial" })
      protected LockedUnspent wrap(final Map m) {
        
        return new LockedUnspent() {

          
          public String txId() {
            return (String) m.get("txid");
          }

          
          public int vout() {
            return ((Long) m.get("vout")).intValue();
          }
        };
      }
    };
  }

  
  public List<ReceivedAddress> listReceivedByAddress() throws GenericRpcException {
    return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress"));
  }

  
  public List<ReceivedAddress> listReceivedByAddress(int minConf) throws GenericRpcException {
    return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", minConf));
  }

  
  public List<ReceivedAddress> listReceivedByAddress(int minConf, boolean includeEmpty) throws GenericRpcException {
    return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", minConf, includeEmpty));
  }

  public List<ReceivedAccount> listReceivedByAccount() throws GenericRpcException {
    return new ReceivedAccountListWrapper((List) query("listreceivedbyaccount"));
  }

  
  public List<ReceivedAccount> listReceivedByAccount(int minConf) throws GenericRpcException {
    return new ReceivedAccountListWrapper((List) query("listreceivedbyaccount", minConf));
  }

  
  public List<ReceivedAccount> listReceivedByAccount(int minConf, boolean includeEmpty) throws GenericRpcException {
    return new ReceivedAccountListWrapper((List) query("listreceivedbyaccount", minConf, includeEmpty));
  }
	  
  private class TransactionListMapWrapper extends ListMapWrapper<Transaction> {

    public TransactionListMapWrapper(List<Map> list) {
      super(list);
    }

    
    protected Transaction wrap(final Map m) {
      return new TransactionWrapper(m);
    }
  }

  private class TransactionsSinceBlockImpl implements TransactionsSinceBlock, Serializable {

    public final List<Transaction> transactions;
    public final String lastBlock;

    public TransactionsSinceBlockImpl(Map r) {
      this.transactions = new TransactionListMapWrapper((List) r.get("transactions"));
      this.lastBlock = (String) r.get("lastblock");
    }

    
    public List<Transaction> transactions() {
      return transactions;
    }

    
    public String lastBlock() {
      return lastBlock;
    }

  }

  
  public TransactionsSinceBlock listSinceBlock() throws GenericRpcException {
    return new TransactionsSinceBlockImpl((Map) query("listsinceblock"));
  }

  
  public TransactionsSinceBlock listSinceBlock(String blockHash) throws GenericRpcException {
    return new TransactionsSinceBlockImpl((Map) query("listsinceblock", blockHash));
  }

  
  public TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations) throws GenericRpcException {
    return new TransactionsSinceBlockImpl((Map) query("listsinceblock", blockHash, targetConfirmations));
  }

  
  public List<Transaction> listTransactions() throws GenericRpcException {
    return new TransactionListMapWrapper((List) query("listtransactions"));
  }

  
  public List<Transaction> listTransactions(String account) throws GenericRpcException {
    return new TransactionListMapWrapper((List) query("listtransactions", account));
  }

  
  public List<Transaction> listTransactions(String account, int count) throws GenericRpcException {
    return new TransactionListMapWrapper((List) query("listtransactions", account, count));
  }

  
  public List<Transaction> listTransactions(String account, int count, int skip) throws GenericRpcException {
    return new TransactionListMapWrapper((List) query("listtransactions", account, count, skip));
  }

  private class UnspentListWrapper extends ListMapWrapper<Unspent> {

    public UnspentListWrapper(List<Map> list) {
      super(list);
    }

    
    protected Unspent wrap(final Map m) {
      return new UnspentWrapper(m);
    }
  }

  private class UnspentWrapper implements Unspent {
    
    final Map m;
    
    UnspentWrapper(Map m) {
      this.m = m;
    }

    
    public String txid() {
      return MapWrapper.mapStr(m, "txid");
    }

    
    public Integer vout() {
      return MapWrapper.mapInt(m, "vout");
    }

    
    public String address() {
      return MapWrapper.mapStr(m, "address");
    }

    
    public String scriptPubKey() {
      return MapWrapper.mapStr(m, "scriptPubKey");
    }

    
    public String account() {
      return MapWrapper.mapStr(m, "account");
    }

    
    public BigDecimal amount() {
      return MapWrapper.mapBigDecimal(m, "amount");
    }

    
    public byte[] data() {
      return MapWrapper.mapHex(m, "data");
    }

    
    public int confirmations() {
      return MapWrapper.mapInt(m, "confirmations");
    }

    
    public String toString() {
      return m.toString();
    }
  }
  
  
  public List<Unspent> listUnspent() throws GenericRpcException {
    return new UnspentListWrapper((List) query("listunspent"));
  }

  
  public List<Unspent> listUnspent(int minConf) throws GenericRpcException {
    return new UnspentListWrapper((List) query("listunspent", minConf));
  }

  
  public List<Unspent> listUnspent(int minConf, int maxConf) throws GenericRpcException {
    return new UnspentListWrapper((List) query("listunspent", minConf, maxConf));
  }

  
  public List<Unspent> listUnspent(int minConf, int maxConf, String... addresses) throws GenericRpcException {
    return new UnspentListWrapper((List) query("listunspent", minConf, maxConf, addresses));
  }

  public boolean lockUnspent(boolean unlock, String txid, int vout) throws GenericRpcException {
    Map<String, Object> params = new LinkedHashMap<String, Object>();
    params.put("txid", txid);
    params.put("vout", vout);
    return Boolean.valueOf(String.valueOf( query("lockunspent", unlock, Arrays.asList(params).toArray())));
  }

  
  public boolean move(String fromAccount, String toAddress, BigDecimal amount) throws GenericRpcException {
    return Boolean.valueOf(String.valueOf( query("move", fromAccount, toAddress, amount)));
  }

  
  public boolean move(String fromAccount, String toAddress, BigDecimal amount, String comment) throws GenericRpcException {
    return Boolean.valueOf(String.valueOf( query("move", fromAccount, toAddress, amount, 0, comment)));
  }

  
  public boolean move(String fromAccount, String toAddress, BigDecimal amount, int minConf) throws GenericRpcException {
    return Boolean.valueOf(String.valueOf( query("move", fromAccount, toAddress, amount, minConf)));
  }

  
  public boolean move(String fromAccount, String toAddress, BigDecimal amount, int minConf, String comment) throws GenericRpcException {
    return Boolean.valueOf(String.valueOf( query("move", fromAccount, toAddress, amount, minConf, comment)));
  }

  
  public String sendFrom(String fromAccount, String toAddress, BigDecimal amount) throws GenericRpcException {
    return (String) query("sendfrom", fromAccount, toAddress, amount);
  }

  
  public String sendFrom(String fromAccount, String toAddress, BigDecimal amount, int minConf) throws GenericRpcException {
    return (String) query("sendfrom", fromAccount, toAddress, amount, minConf);
  }

  
  public String sendFrom(String fromAccount, String toAddress, BigDecimal amount, int minConf, String comment) throws GenericRpcException {
    return (String) query("sendfrom", fromAccount, toAddress, amount, minConf, comment);
  }

  
  public String sendFrom(String fromAccount, String toAddress, BigDecimal amount, int minConf, String comment, String commentTo) throws GenericRpcException {
    return (String) query("sendfrom", fromAccount, toAddress, amount, minConf, comment, commentTo);
  }

  
  public String sendRawTransaction(String hex) throws GenericRpcException {
    return (String) query("sendrawtransaction", hex);
  }

  
  public String sendToAddress(String toAddress, BigDecimal amount) throws GenericRpcException {
    return (String) query("sendtoaddress", toAddress, amount);
  }

  
  public String sendToAddress(String toAddress, BigDecimal amount, String comment) throws GenericRpcException {
    return (String) query("sendtoaddress", toAddress, amount, comment);
  }

  
  public String sendToAddress(String toAddress, BigDecimal amount, String comment, String commentTo) throws GenericRpcException {
    return (String) query("sendtoaddress", toAddress, amount, comment, commentTo);
  }

  public String signRawTransaction(String hex) throws GenericRpcException {
    return signRawTransaction(hex, null, null, "ALL");
  }

  
  public String signRawTransaction(String hex, List<? extends TxInput> inputs, List<String> privateKeys) throws GenericRpcException {
    return signRawTransaction(hex, inputs, privateKeys, "ALL");
  }

  public String signRawTransaction(String hex, List<? extends TxInput> inputs, List<String> privateKeys, String sigHashType) {
    List<Map> pInputs = null;

    if (inputs != null) {
      pInputs = new ArrayList<Map>();
      for (final TxInput txInput : inputs) {
        pInputs.add(new LinkedHashMap() {
          {
            put("txid", txInput.txid());
            put("vout", txInput.vout());
            put("scriptPubKey", txInput.scriptPubKey());
            if (txInput instanceof ExtendedTxInput) {
              ExtendedTxInput extin = (ExtendedTxInput) txInput;
              put("redeemScript", extin.redeemScript());
              put("amount", extin.amount());
            }
          }
        });
      }
    }

    Map result = (Map) query("signrawtransaction", hex, pInputs, privateKeys, sigHashType); //if sigHashType is null it will return the default "ALL"
    if ((Boolean) result.get("complete"))
      return (String) result.get("hex");
    else
      throw new GenericRpcException("Incomplete");
  }

  public RawTransaction decodeRawTransaction(String hex) throws GenericRpcException {
    Map result = (Map) query("decoderawtransaction", hex);
    RawTransaction rawTransaction = new RawTransactionImpl(result);
    return rawTransaction.vOut().get(0).transaction();
  }

  
  public AddressValidationResult validateAddress(String address) throws GenericRpcException {
    final Map validationResult = (Map) query("validateaddress", address);
    return new AddressValidationResult() {

      
      public boolean isValid() {
        return ((Boolean) validationResult.get("isvalid"));
      }

      
      public String address() {
        return (String) validationResult.get("address");
      }

      
      public boolean isMine() {
        return ((Boolean) validationResult.get("ismine"));
      }

      
      public boolean isScript() {
        return ((Boolean) validationResult.get("isscript"));
      }

      
      public String pubKey() {
        return (String) validationResult.get("pubkey");
      }

      
      public boolean isCompressed() {
        return ((Boolean) validationResult.get("iscompressed"));
      }

      
      public String account() {
        return (String) validationResult.get("account");
      }

      
      public String toString() {
        return validationResult.toString();
      }

    };
  }

  
  public void setGenerate(boolean b) throws PaicoinRPCException {
    query("setgenerate", b);
  }

  
  public List<String> generate(int numBlocks) throws PaicoinRPCException {
    return (List<String>) query("generate", numBlocks);
  }

  
  public List<String> generate(int numBlocks, long maxTries) throws PaicoinRPCException {
    return (List<String>) query("generate", numBlocks, maxTries);
  }

  
  public List<String> generateToAddress(int numBlocks, String address) throws PaicoinRPCException {
    return (List<String>) query("generatetoaddress", numBlocks, address);
  }

  
  public BigDecimal estimateFee(int nBlocks) throws GenericRpcException {
    return (BigDecimal) query("estimatefee", nBlocks);
  }

  
  public BigDecimal estimatePriority(int nBlocks) throws GenericRpcException {
    return (BigDecimal) query("estimatepriority", nBlocks);
  }

  
  public void invalidateBlock(String hash) throws GenericRpcException {
    query("invalidateblock", hash);
  }

  
  public void reconsiderBlock(String hash) throws GenericRpcException {
    query("reconsiderblock", hash);

  }

  private class PeerInfoWrapper extends MapWrapper implements PeerInfoResult, Serializable {

    public PeerInfoWrapper(Map m) {
      super(m);
    }

    
    public long getId() {
      return mapLong("id");
    }

    
    public String getAddr() {
      return mapStr("addr");
    }

    
    public String getAddrLocal() {
      return mapStr("addrlocal");
    }

    
    public String getServices() {
      return mapStr("services");
    }

    
    public long getLastSend() {
      return mapLong("lastsend");
    }

    
    public long getLastRecv() {
      return mapLong("lastrecv");
    }

    
    public long getBytesSent() {
      return mapLong("bytessent");
    }

    
    public long getBytesRecv() {
      return mapLong("bytesrecv");
    }

    
    public long getConnTime() {
      return mapLong("conntime");
    }

    
    public int getTimeOffset() {
      return mapInt("timeoffset");
    }

    
    public BigDecimal getPingTime() {
      return mapBigDecimal("pingtime");
    }

    
    public long getVersion() {
      return mapLong("version");
    }

    
    public String getSubVer() {
      return mapStr("subver");
    }

    
    public boolean isInbound() {
      return mapBool("inbound");
    }

    
    public int getStartingHeight() {
      return mapInt("startingheight");
    }

    
    public long getBanScore() {
      return mapLong("banscore");
    }

    
    public int getSyncedHeaders() {
      return mapInt("synced_headers");
    }

    
    public int getSyncedBlocks() {
      return mapInt("synced_blocks");
    }

    
    public boolean isWhiteListed() {
      return mapBool("whitelisted");
    }

  }

  
  public List<PeerInfoResult> getPeerInfo() throws GenericRpcException {
    final List<Map> l = (List<Map>) query("getpeerinfo");
//    final List<PeerInfoResult> res = new ArrayList<>(l.size());
//    for (Map m : l)
//      res.add(new PeerInfoWrapper(m));
//    return res;
    return new AbstractList<PeerInfoResult>() {

      
      public PeerInfoResult get(int index) {
        return new PeerInfoWrapper(l.get(index));
      }

      
      public int size() {
        return l.size();
      }
    };
  }

  
  public void stop() {
    query("stop");
  }

  
  public String getRawChangeAddress() throws GenericRpcException {
    return (String) query("getrawchangeaddress");
  }

  
  public long getConnectionCount() throws GenericRpcException {
    return Long.parseLong(String.valueOf(query("getconnectioncount")));
  }

  
  public BigDecimal getUnconfirmedBalance() throws GenericRpcException {
    return (BigDecimal) query("getunconfirmedbalance");
  }

  
  public BigDecimal getDifficulty() throws GenericRpcException {
    return (BigDecimal) query("getdifficulty");
  }

  public MempoolInfo getMempoolInfo() throws GenericRpcException{
	  return new MempoolInfoWrapper((Map) query("getmempoolinfo"));
  }
  
  public NetTotals getNetTotals() throws GenericRpcException {
    return new NetTotalsImpl((Map) query("getnettotals"));
  }

  
  public DecodedScript decodeScript(String hex) throws GenericRpcException {
    return new DecodedScriptImpl((Map) query("decodescript", hex));
  }

  
  public void ping() throws GenericRpcException {
    query("ping");
  }

  //It doesn't work!
  
  public boolean getGenerate() throws PaicoinRPCException {
    return Boolean.valueOf(String.valueOf( query("getgenerate")));
  }

  
  public BigDecimal getNetworkHashPs() throws GenericRpcException {
    return (BigDecimal) query("getnetworkhashps");
  }

  public BigDecimal getNetworkHashPs(long height) throws GenericRpcException {
	    return (BigDecimal) query("getnetworkhashps",height);
  }
  
  
  public boolean setTxFee(BigDecimal amount) throws GenericRpcException {
    return Boolean.valueOf(String.valueOf( query("settxfee", amount)));
  }

  /**
   *
   * @param node example: "192.168.0.6:8333"
   * @param command must be either "add", "remove" or "onetry"
   * @throws GenericRpcException
   */
  
  public void addNode(String node, String command) throws GenericRpcException {
    query("addnode", node, command);
  }

  
  public void backupWallet(String destination) throws GenericRpcException {
    query("backupwallet", destination);
  }

  
  public String signMessage(String paicoinAdress, String message) throws GenericRpcException {
    return (String) query("signmessage", paicoinAdress, message);
  }

  
  public void dumpWallet(String filename) throws GenericRpcException {
    query("dumpwallet", filename);
  }

  
  public void importWallet(String filename) throws GenericRpcException {
    query("importwallet", filename);
  }

  
  public void keyPoolRefill() throws GenericRpcException {
    keyPoolRefill(100); //default is 100 if you don't send anything
  }

  public void keyPoolRefill(long size) throws GenericRpcException {
    query("keypoolrefill", size);
  }

  
  public BigDecimal getReceivedByAccount(String account) throws GenericRpcException {
    return getReceivedByAccount(account, 1);
  }

  public BigDecimal getReceivedByAccount(String account, int minConf) throws GenericRpcException {
    return (BigDecimal)query("getreceivedbyaccount", account, minConf);
  }

  
  public void encryptWallet(String passPhrase) throws GenericRpcException {
    query("encryptwallet", passPhrase);
  }

  
  public void walletPassPhrase(String passPhrase, long timeOut) throws GenericRpcException {
    query("walletpassphrase", passPhrase, timeOut);
  }

  
  public boolean verifyMessage(String paicoinAddress, String signature, String message) throws GenericRpcException {
    return Boolean.valueOf(String.valueOf( query("verifymessage", paicoinAddress, signature, message)));
  }

  
  public String addMultiSigAddress(int nRequired, List<String> keyObject) throws GenericRpcException {
    return (String) query("addmultisigaddress", nRequired, keyObject);
  }

  
  public String addMultiSigAddress(int nRequired, List<String> keyObject, String account) throws GenericRpcException {
    return (String) query("addmultisigaddress", nRequired, keyObject, account);
  }

  
  public boolean verifyChain() {
    return verifyChain(3, 6); //3 and 6 are the default values
  }

  public boolean verifyChain(int checklevel, int numblocks) {
    return Boolean.valueOf(String.valueOf( query("verifychain", checklevel, numblocks)));
  }

  /**
   * Attempts to submit new block to network. The 'jsonparametersobject'
   * parameter is currently ignored, therefore left out.
   *
   * @param hexData
   */
  
  public void submitBlock(String hexData) {
    query("submitblock", hexData);
  }

  
  public Transaction getTransaction(String txId) {
    return new TransactionWrapper((Map) query("gettransaction", txId));
  }

  
  public TxOut getTxOut(String txId, long vout) throws GenericRpcException {
    return new TxOutWrapper((Map) query("gettxout", txId, vout, true));
  }

  public TxOut getTxOut(String txId, long vout, boolean includemempool) throws GenericRpcException {
    return new TxOutWrapper((Map) query("gettxout", txId, vout, includemempool));
  }

  public String getTxOutProof(String[] txids) throws GenericRpcException {
	  return getTxOutProof(txids,null);
  }
  
  public String getTxOutProof(String[] txids,String blockhash) throws GenericRpcException {
	  if (txids == null) {
		  throw new GenericRpcException("txids can not empty!");
	  }
	  if (blockhash == null || "".equalsIgnoreCase(blockhash.trim())) {
		  return String.valueOf( query("gettxoutproof", txids));
	  } else {
		  return String.valueOf( query("gettxoutproof", txids,blockhash));
	  }
  }
  
  public List<String> verifyTxOutProof(String proof) throws GenericRpcException {
	  return (List<String>) query("verifytxoutproof",proof);
  }
  
  /**
   * the result returned by
   * {@link PaicoinJSONRPCClient#getAddressBalance(String)}
   * 
   * @author frankchen
   * @create 2018年6月21日 上午10:38:17
   */
  private static class AddressBalanceWrapper extends MapWrapper implements AddressBalance, Serializable
  {
      public AddressBalanceWrapper(Map<String, Object> r)
      {
          super(r);
      }

      public long getBalance()
      {
          return this.mapLong("balance");
      }

      public long getReceived()
      {
          return this.mapLong("received");
      }
  }

  /**
   * the result return by {@link PaicoinJSONRPCClient#getAddressUtxo(String)}
   */
  private static class AddressUtxoWrapper implements AddressUtxo
  {
      private String address;
      private String txid;
      private int    outputIndex;
      private String script;
      private long   satoshis;
      private long   height;

      public AddressUtxoWrapper(Map<String, Object> result)
      {
          address = getOrDefault(result, "address", "").toString();
          txid = getOrDefault(result, "txid", "").toString();
          outputIndex = getOrDefault(result, "outputIndex", 0);
          script = getOrDefault(result, "script", "").toString();
          satoshis = getOrDefault(result, "satoshis", 0L);
          height = getOrDefault(result, "height", -1L);
      }
      
      <T extends Object> T getOrDefault(Map<String, Object> result, String key, T defval) {
        T val = (T) result.get(key);
        return val != null ? val : defval;
      }

      public String getAddress()
      {
          return address;
      }

      public String getTxid()
      {
          return txid;
      }

      public int getOutputIndex()
      {
          return outputIndex;
      }

      public String getScript()
      {
          return script;
      }

      public long getSatoshis()
      {
          return satoshis;
      }

      public long getHeight()
      {
          return height;
      }
  }
  
  private static class AddressUtxoList extends ListMapWrapper<AddressUtxo>
  {
      public AddressUtxoList(List<Map> list)
      {
          super((List<Map>)list);
      }

      
      protected AddressUtxo wrap(Map m)
      {
          return new AddressUtxoWrapper(m);
      }
  }
  
  public AddressBalance getAddressBalance(String address)
  {
      return new AddressBalanceWrapper((Map<String, Object>)query("getaddressbalance", address));
  }

  public List<AddressUtxo> getAddressUtxo(String address)
  {
      return new AddressUtxoList((List<Map>)query("getaddressutxos", address));
  }
  
  private class ChainTipsWrapper extends MapWrapper implements ChainTips, Serializable {
	  
    public ChainTipsWrapper(Map m) {
      super(m);
    }

	@Override
	public long height() {
		return this.mapLong("height");
	}

	@Override
	public String hash() {
		return this.mapStr("hash");
	}

	@Override
	public int branchlen() {
		return this.mapInt("branchlen");
	}

	@Override
	public String status() {
		return this.mapStr("status");
	}
  }
  
  private class MempoolInfoWrapper extends MapWrapper implements MempoolInfo, Serializable {
	  
    public MempoolInfoWrapper(Map m) {
      super(m);
    }

	@Override
	public int size() {
		return this.mapInt("size");
	}

	@Override
	public int bytes() {
		return this.mapInt("bytes");
	}

	@Override
	public int usage() {
		return this.mapInt("usage");
	}

	@Override
	public long maxmempool() {
		return this.mapLong("maxmempool");
	}

	@Override
	public BigDecimal mempoolminfee() {
		return this.mapBigDecimal("mempoolminfee");
	}

	
  }
  
}
