package ljx.lrpc.registry;

/**
 * 
 * @author liang
 *
 */
public class Constants {

    public static final int DEFAULT_ZK_SESSION_TIMEOUT_MS = 60 * 1000;//默认超时时间
    public static final int DEFAULT_ZK_CONNECTION_TIMEOUT_MS = 25 * 1000;//默认超时时间
    public static final String DEFAULT_ZK_ROOT_PATH = "/frpc";//根路径
    public static final String DEFAULT_ZK_REGISTRY_DATA_PATH = DEFAULT_ZK_ROOT_PATH + "/registry";
    public static final int DEFAULT_ZK_BASE_SLEEP_TIME_MS = 1000;
    public static final int DEFAULT_ZK_MAX_RETRIES = 3;//最大重试次数
    public static final int DEFAULT_TIMEOUT = 1000;
}
