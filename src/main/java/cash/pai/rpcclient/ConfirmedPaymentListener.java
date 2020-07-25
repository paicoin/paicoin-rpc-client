/*
 * Copyright (c) 2013, Mikhail Yevchenko. All rights reserved. PROPRIETARY/CONFIDENTIAL.
 * Copyright (c) 2020, Pai.Cash. All rights reserved.
 * 
 */
package cash.pai.rpcclient;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import cash.pai.rpcclient.PaicoindRpcClient.Transaction;

/**
 *
 */
public abstract class ConfirmedPaymentListener extends SimplePaicoinPaymentListener {

    public int minConf;

    public ConfirmedPaymentListener(int minConf) {
        this.minConf = minConf;
    }

    public ConfirmedPaymentListener() {
        this(6);
    }

    protected Set<String> processed = Collections.synchronizedSet(new HashSet<String>());

    protected boolean markProcess(String txId) {
        return processed.add(txId);
    }

    @Override
    public void transaction(Transaction transaction) {
        if (transaction.confirmations() < minConf)
            return;
        if (!markProcess(transaction.txId()))
            return;
        confirmed(transaction);
    }

    public abstract void confirmed(Transaction transaction);

}
