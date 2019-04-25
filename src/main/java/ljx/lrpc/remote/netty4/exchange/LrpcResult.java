package ljx.lrpc.remote.netty4.exchange;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//返回结果数据，保存在LrpcResonse
public class LrpcResult implements Serializable{
	
	private static final long serialVersionUID = -6925924956850004727L;
	
    private Object result;

    private Throwable exception;
    
    private Map<String, String> attachments = new HashMap<String, String>();
    
    public LrpcResult() {
    }

    public LrpcResult(Object result) {
        this.result = result;
    }

    public LrpcResult(Throwable exception) {
        this.exception = exception;
    }

    public Object recreate() throws Throwable {
        if (exception != null) {
            throw exception;
        }
        return result;
    }
    
    public Object getResult() {
        return getValue();
    }
    
    public void setResult(Object result) {
        setValue(result);
    }

    public Object getValue() {
        return result;
    }

    public void setValue(Object value) {
        this.result = value;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(Map<String, String> map) {
        if (map != null && map.size() > 0) {
            attachments.putAll(map);
        }
    }

    public String getAttachment(String key) {
        return attachments.get(key);
    }

    public String getAttachment(String key, String defaultValue) {
        String result = attachments.get(key);
        if (result == null || result.length() == 0) {
            result = defaultValue;
        }
        return result;
    }

    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

    @Override
    public String toString() {
        return "LrpcResult [result=" + result + ", exception=" + exception + "]";
    }
}
