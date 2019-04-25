package ljx.lrpc.registry;

/**
 * 地址和端口
 * @author liang
 *
 */
public class RegisterInfo {

    private String host;
    private int port;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public RegisterInfo(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

    
}
