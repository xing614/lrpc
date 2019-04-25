package ljx.lrpc.config.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class LllRPCNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		// TODO Auto-generated method stub
		registerBeanDefinitionParser("reference", new LllRPCBeanDefinitionParser());
	}

}
