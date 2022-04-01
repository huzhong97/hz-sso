package com.example.hz.sso.core.util;

import com.example.hz.sso.core.UserModel.SsoUser;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * jedis client
 * 2022年3月30日16点58分
 * @author huzhong
 */
@Slf4j
public class JedisUtil {

    public static String address;

    public static void init(String address) {
        JedisUtil.address = address;
    }


    private static ShardedJedisPool jedisPool;
    private static ReentrantLock INSTANCE_LOCK = new ReentrantLock(false);

    /**
     *
     * @return
     */
    private static ShardedJedis getInstance() {
        if (JedisUtil.jedisPool == null) {
            try {
                if (JedisUtil.INSTANCE_LOCK.tryLock(2, TimeUnit.SECONDS)) {
                    try {
                        if (JedisUtil.jedisPool == null){
                            JedisPoolConfig config = new JedisPoolConfig();
                            config.setMaxTotal(200);
                            config.setMaxIdle(50);
                            config.setMinIdle(8);
                            config.setMaxWaitMillis(10000);
                            config.setTestOnBorrow(true);
                            config.setTestOnReturn(false);
                            config.setTestWhileIdle(true);
                            config.setTimeBetweenEvictionRunsMillis(30000);
                            config.setNumTestsPerEvictionRun(10);
                            config.setMinEvictableIdleTimeMillis(60000);

                            List<JedisShardInfo> addresses = new LinkedList<>();

                            String[] Addr = JedisUtil.address.split(",");
                            for (int i = 0; i < Addr.length; ++i) {
                                JedisShardInfo jedisShardInfo = new JedisShardInfo(Addr[i]);
                                addresses.add(jedisShardInfo);
                            }
                            JedisUtil.jedisPool = new ShardedJedisPool(config, addresses);
                            log.info("JedisUtil.jedisPool init Success!");
                        }
                    } finally {
                        JedisUtil.INSTANCE_LOCK.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (JedisUtil.jedisPool == null) {
            throw new NullPointerException("JedisUtil.jedisPool is null");
        }
        ShardedJedis shardedJedis = JedisUtil.jedisPool.getResource();
        return shardedJedis;
    }


    // ------------  serialize and deserialize ----------------------
    private static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("serialize(" + obj + ") exception");
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        byte[] bytes = null;
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);
        try {
            bytes = ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException("serialize(" + obj.getClass() + ")Object(" + obj + ") exception", e);
        } finally {
            buffer.clear();
        }
        return bytes;
    }

    private static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("deserialize exception, bytes null");
        }
        T instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("deserialize obj newInstance error, ", e);
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        ProtobufIOUtil.mergeFrom(bytes, instance, schema);
        return instance;
    }


    //--------------- jedis get & set -----------------------

    public static String setObj(String key, Object obj, int seconds) {
        String res = null;
        try (ShardedJedis jedis = JedisUtil.getInstance();) {
            res = jedis.setex(key.getBytes(), seconds, serialize(obj));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

    public static Object getObj(String key) {
        Object obj = null;
        try (ShardedJedis jedis = JedisUtil.getInstance();) {
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null && bytes.length > 0) {
                obj = deserialize(bytes, SsoUser.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return obj;
    }

    public static long del(String key) {
        Long res = null;
        try (ShardedJedis jedis = JedisUtil.getInstance();) {
            res = jedis.del(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

}
