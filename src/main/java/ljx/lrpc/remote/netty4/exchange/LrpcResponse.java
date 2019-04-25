package ljx.lrpc.remote.netty4.exchange;

import java.io.Serializable;

/**
 * rpc响应结果
 * @author liang
 *
 */
public class LrpcResponse{

	   /**
     * ok.
     */
    public static final byte OK = 20;

    /**
     * clien side timeout.
     */
    public static final byte CLIENT_TIMEOUT = 30;

    /**
     * server side timeout.
     */
    public static final byte SERVER_TIMEOUT = 31;

    /**
     * request format error.
     */
    public static final byte BAD_REQUEST = 40;

    /**
     * response format error.
     */
    public static final byte BAD_RESPONSE = 50;

    /**
     * service not found.
     */
    public static final byte SERVICE_NOT_FOUND = 60;

    /**
     * service error.
     */
    public static final byte SERVICE_ERROR = 70;

    /**
     * internal server error.
     */
    public static final byte SERVER_ERROR = 80;

    /**
     * internal server error.
     */
    public static final byte CLIENT_ERROR = 90;

    /**
     * server side threadpool exhausted and quick return.
     */
    public static final byte SERVER_THREADPOOL_EXHAUSTED_ERROR = 100;

    private long mId = 0;

    private String mVersion;

    private byte mStatus = OK;

    private String mErrorMsg;

    private Object mResult;
    
    public LrpcResponse() {
    }

    public LrpcResponse(long id) {
        mId = id;
    }

    public LrpcResponse(long id, String version) {
        mId = id;
        mVersion = version;
    }

	public long getmId() {
		return mId;
	}

	public void setmId(long mId) {
		this.mId = mId;
	}

	public String getmVersion() {
		return mVersion;
	}

	public void setmVersion(String mVersion) {
		this.mVersion = mVersion;
	}

	public byte getmStatus() {
		return mStatus;
	}

	public void setmStatus(byte mStatus) {
		this.mStatus = mStatus;
	}

	public String getmErrorMsg() {
		return mErrorMsg;
	}

	public void setmErrorMsg(String mErrorMsg) {
		this.mErrorMsg = mErrorMsg;
	}

	public Object getmResult() {
		return mResult;
	}

	public void setmResult(Object mResult) {
		this.mResult = mResult;
	}

	@Override
	public String toString() {
		return "LrpcResponse [mId=" + mId + ", mVersion=" + mVersion + ", mStatus=" + mStatus + ", mErrorMsg="
				+ mErrorMsg + ", mResult=" + mResult + "]";
	}
    
    

    
    
}
