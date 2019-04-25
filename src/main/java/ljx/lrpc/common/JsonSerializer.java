package ljx.lrpc.common;

import com.alibaba.fastjson.JSON;

/**
 * 数据转json
 * @author liang
 *
 */
public class JsonSerializer implements Serialization{

	public byte[] serialize(Object o) {
		// TODO Auto-generated method stub
		return JSON.toJSONBytes(o);
	}

	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
		// TODO Auto-generated method stub
		return JSON.parseObject(bytes, clazz);
	}


}
