package ljx.lrpc.config.spring.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import ljx.lrpc.config.api.ReferenceBean;

public class LllRPCBeanDefinitionParser extends AbstractSingleBeanDefinitionParser  {
	
	protected Class getBeanClass(Element element) {
		return ReferenceBean.class;
	}
	//重写解析
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String interfaceClass = element.getAttribute("interface");
        if (StringUtils.hasText(interfaceClass)) {
            bean.addPropertyValue("interfaceClass", interfaceClass);
        }
    }
}
