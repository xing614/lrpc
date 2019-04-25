package ljx.llpc.service.impl;

import ljx.llpc.service.SayHelloService;

public class SayHelloServiceImpl implements SayHelloService {
    @Override
    public String sayHello(String name) {
    	System.out.println("hello+"+name);
        return "hello," + name;
    }
}
