package ljx.lrpc.remote.netty4.exchange;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * rpc请求数据 调用服务对应的类 方法 参数类型 变量
 * @author liang
 *
 */
public class LrpcRequest{
    /**
     * 请求id
     */
    private final long mId;
    
    private String mVersion;
    
    private Object mData;
    
    private static final AtomicLong INVOKE_ID = new AtomicLong(0);
    
    public LrpcRequest() {
        mId = newId();
    }

    public LrpcRequest(long id) {
        mId = id;
    }
    
    private static long newId() {
        return INVOKE_ID.getAndIncrement();
    }
    
    private static String safeToString(Object data) {
        if (data == null) return null;
        String dataStr;
        try {
            dataStr = data.toString();
        } catch (Throwable e) {
            dataStr = "<Fail toString of " + data.getClass() + ", cause: " +
                    e.toString() + ">";
        }
        return dataStr;
    }

	public String getmVersion() {
		return mVersion;
	}

	public void setmVersion(String mVersion) {
		this.mVersion = mVersion;
	}

	public Object getmData() {
		return mData;
	}

	public void setmData(Object mData) {
		this.mData = mData;
	}

	public long getmId() {
		return mId;
	}

	@Override
	public String toString() {
		return "LrpcRequest [mId=" + mId + ", mVersion=" + mVersion + ", mData=" + mData.toString() + "]";
	}

}
