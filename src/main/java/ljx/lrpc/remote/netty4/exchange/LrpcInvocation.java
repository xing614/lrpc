package ljx.lrpc.remote.netty4.exchange;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//请求数据，被包囊在LrpcRequest
public class LrpcInvocation implements Serializable{
	
	private static final long serialVersionUID = -4355285085441097045L;
	
	private String className;
	
    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;
    //额外数据
    private Map<String, String> attachments;
    
    public LrpcInvocation() {
    }
    
    public LrpcInvocation(String className,Method method, Object[] arguments) {
        this(className,method.getName(), method.getParameterTypes(), arguments, null);
    }

    public LrpcInvocation(String className,Method method, Object[] arguments, Map<String, String> attachment) {
        this(className,method.getName(), method.getParameterTypes(), arguments, attachment);
    }

    public LrpcInvocation(String className,String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this(className,methodName, parameterTypes, arguments, null);
    }

    public LrpcInvocation(String className,String methodName, Class<?>[] parameterTypes, Object[] arguments, Map<String, String> attachments) {
        this.className = className;
    	this.methodName = methodName;
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
    }
    
    
    public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments == null ? new Object[0] : arguments;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
    }

    public void setAttachment(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<String, String>();
        }
        attachments.put(key, value);
    }
    
    public void addAttachments(Map<String, String> attachments) {
        if (attachments == null) {
            return;
        }
        if (this.attachments == null) {
            this.attachments = new HashMap<String, String>();
        }
        this.attachments.putAll(attachments);
    }

	@Override
	public String toString() {
		return "LrpcInvocation [className=" + className + ", methodName=" + methodName + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + ", arguments=" + Arrays.toString(arguments) + ", attachments="
				+ attachments + "]";
	}
    
    
}
