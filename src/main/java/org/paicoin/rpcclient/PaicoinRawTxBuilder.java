/*
 * Paicoin-RPC-Client License
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paicoin.rpcclient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.paicoin.rpcclient.PaicoindRpcClient.RawTransaction;
import org.paicoin.rpcclient.PaicoindRpcClient.TxInput;
import org.paicoin.rpcclient.PaicoindRpcClient.TxOutput;

/**
 *
 */
public class PaicoinRawTxBuilder {

  public final PaicoindRpcClient bitcoin;

  public PaicoinRawTxBuilder(PaicoindRpcClient bitcoin) {
    this.bitcoin = bitcoin;
  }
  public Set<PaicoindRpcClient.TxInput> inputs = new LinkedHashSet<TxInput>();
  public List<PaicoindRpcClient.TxOutput> outputs = new ArrayList<TxOutput>();
  public List<String> privateKeys;

  private class Input extends PaicoindRpcClient.BasicTxInput {

    public Input(String txid, Integer vout) {
      super(txid, vout);
    }

    public Input(PaicoindRpcClient.TxInput copy) {
      this(copy.txid(), copy.vout());
    }

    @Override
    public int hashCode() {
      return txid.hashCode() + vout;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (!(obj instanceof PaicoindRpcClient.TxInput))
        return false;
      PaicoindRpcClient.TxInput other = (PaicoindRpcClient.TxInput) obj;
      return vout == other.vout() && txid.equals(other.txid());
    }

  }

  public PaicoinRawTxBuilder in(PaicoindRpcClient.TxInput in) {
    inputs.add(new Input(in.txid(), in.vout()));
    return this;
  }

  public PaicoinRawTxBuilder in(String txid, int vout) {
    in(new PaicoindRpcClient.BasicTxInput(txid, vout));
    return this;
  }

  public PaicoinRawTxBuilder out(String address, BigDecimal amount) {
    return out(address, amount, null);
  }

  public PaicoinRawTxBuilder out(String address, BigDecimal amount, byte[] data) {
    outputs.add(new PaicoindRpcClient.BasicTxOutput(address, amount, data));
    return this;
  }

  public PaicoinRawTxBuilder in(BigDecimal value) throws GenericRpcException {
    return in(value, 6);
  }

  public PaicoinRawTxBuilder in(BigDecimal value, int minConf) throws GenericRpcException {
    List<PaicoindRpcClient.Unspent> unspent = bitcoin.listUnspent(minConf);
    BigDecimal v = value;
    for (PaicoindRpcClient.Unspent o : unspent) {
      if (!inputs.contains(new Input(o))) {
        in(o);
        v = v.subtract(o.amount());
      }
      if (v.compareTo(BigDecimal.ZERO) < 0)
        break;
    }
    if (BigDecimal.ZERO.compareTo(v) < 0)
      throw new GenericRpcException("Not enough bitcoins (" + v + "/" + value + ")");
    return this;
  }

  private HashMap<String, PaicoindRpcClient.RawTransaction> txCache = new HashMap<String, RawTransaction>();

  private PaicoindRpcClient.RawTransaction tx(String txId) throws GenericRpcException {
    PaicoindRpcClient.RawTransaction tx = txCache.get(txId);
    if (tx != null)
      return tx;
    tx = bitcoin.getRawTransaction(txId);
    txCache.put(txId, tx);
    return tx;
  }

  public PaicoinRawTxBuilder outChange(String address) throws GenericRpcException {
    return outChange(address, BigDecimal.ZERO);
  }

  public PaicoinRawTxBuilder outChange(String address, BigDecimal fee) throws GenericRpcException {
    BigDecimal is = BigDecimal.ZERO;
    for (PaicoindRpcClient.TxInput i : inputs)
      is = is.add(tx(i.txid()).vOut().get(i.vout()).value());
    BigDecimal os = fee;
    for (PaicoindRpcClient.TxOutput o : outputs)
      os = os.add(o.amount());
    if (os.compareTo(is) < 0)
      out(address, is.subtract(os));
    return this;
  }
  
  public PaicoinRawTxBuilder addPrivateKey(String privateKey)
  {
	  if ( privateKeys == null )
		  privateKeys = new ArrayList<String>();
	  privateKeys.add(privateKey);
	  return this;
  }

  public String create() throws GenericRpcException {
    return bitcoin.createRawTransaction(new ArrayList<TxInput>(inputs), outputs);
  }

  public String sign() throws GenericRpcException {
    return bitcoin.signRawTransaction(create(), null, privateKeys);
  }

  public String send() throws GenericRpcException {
    return bitcoin.sendRawTransaction(sign());
  }

}
