package cash.pai.rpcclient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaicoinTestBase {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	static final PaicoinTestEvn evn = PaicoinTestEvn.getInstence();
	
	public PaicoindRpcClient getClient(String... expectedMethod) {
		return new CommonPaicoinTestClient(evn.testNet,expectedMethod);
	}
	
	public String getDateTime() {
		return sdf.format(new Date());
	}

}
