package cash.pai.rpcclient;

import java.util.logging.Level;
import java.util.logging.Logger;

import cash.pai.krotjson.JSON;

public class CommonPaicoinTestClient extends PaicoinJSONRPCClient {
	
	static final Logger logger = Logger.getLogger(CommonPaicoinTestClient.class.getPackage().getName());
	
	String[] expectedMethod;
    
    CommonPaicoinTestClient(boolean testNet,String... expectedMethod) {
        super(testNet);
        this.expectedMethod = expectedMethod;
    }
    
    private boolean isExpectedMethod(String method) {
    	if (expectedMethod == null) {
    		return true;
    	}
    	for(String s : expectedMethod) {
    		if (s.equalsIgnoreCase(method)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    @Override
    public Object query(String method, Object... o) throws GenericRpcException {
        if(!isExpectedMethod(method)) {
            throw new GenericRpcException("wrong method");
        }
        
        Object obj = super.query(method, o);
        logger.log(Level.INFO, method + "   "+JSON.stringify(o)+"\n" + JSON.stringFormat(obj));
        return obj;
    }
    
    
}
