package ljx.lrpc.config.api;

import ljx.lrpc.remote.netty4.client.LrpcProxy;

public class ReferenceConfig<T> {
	
	private Class<?> interfaceClass;
	//接口代理类引用
	private transient volatile T ref;
	
	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}
	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}
	
	public synchronized T get() {
		if(ref == null) {
			init();
		}
		return ref;
	}
	
	private void init() {
		// TODO Auto-generated method stub
		ref = new LrpcJdkProxyFactory(interfaceClass).getProxy();
	}
	
}
