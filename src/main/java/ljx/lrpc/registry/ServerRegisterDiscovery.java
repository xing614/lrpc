package ljx.lrpc.registry;

public interface ServerRegisterDiscovery {

	String discovery();
	
	void register(int port);
}
