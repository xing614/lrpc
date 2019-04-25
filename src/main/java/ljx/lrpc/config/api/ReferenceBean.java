package ljx.lrpc.config.api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
//继承工厂bean,getObject方法返回代理类
//继承
public class ReferenceBean<T> extends ReferenceConfig<T> implements FactoryBean,ApplicationContextAware{

	@Override
	public Object getObject() throws Exception {
		// TODO Auto-generated method stub
		return get();
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return getInterfaceClass();
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		
	}

}
