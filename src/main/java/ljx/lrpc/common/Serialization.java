package ljx.lrpc.common;


public interface Serialization {

    /**
     * java to binary
     *
     * @param o
     * @return
     */
    byte[] serialize(Object o);

    /**
     * binary to java
     *
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
