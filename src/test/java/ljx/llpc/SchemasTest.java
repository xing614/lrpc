package ljx.llpc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ljx.llpc.service.SayHelloService;
import ljx.lrpc.config.api.LrpcJdkProxyFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/lllRPC-consumer.xml")
public class SchemasTest {
	@Autowired
	private SayHelloService sayHelloService;
	
	//@Test
	public void test() throws Exception {
		LrpcJdkProxyFactory proxyFactor = new LrpcJdkProxyFactory(SayHelloService.class);
	    SayHelloService sayHelloService = proxyFactor.getProxy();
	    sayHelloService.sayHello("123");
	    
	}
	@Test
	public void test2() {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/lllRPC-consumer.xml");
//		SayHelloService sa = (SayHelloService)context.getBean("sayHelloService");
//		sa.sayHello("111");
		sayHelloService.sayHello("111");
		System.out.println("aaa");
	}
}
