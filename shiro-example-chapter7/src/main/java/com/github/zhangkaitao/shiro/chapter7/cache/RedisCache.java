package com.github.zhangkaitao.shiro.chapter7.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import redis.clients.jedis.Jedis;

public class RedisCache implements Cache<String, Object> {
	
	private String buildShiroKey(String key) {
		return "shiro:"+key;
	}

	@Override
	public Object get(String key) throws CacheException {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			byte[] bytes = jedis.get(SerializationUtils.serialize(buildShiroKey(key)));
			if(bytes!=null&&bytes.length>0)return SerializationUtils.deserialize(bytes);
			return null;
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

	@Override
	public Object put(String key, Object value) throws CacheException {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			return jedis.set(SerializationUtils.serialize(buildShiroKey(key)), SerializationUtils.serialize((Serializable) value));
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

	@Override
	public Object remove(String key) throws CacheException {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			return jedis.del(SerializationUtils.serialize(buildShiroKey(key)));
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

	@Override
	public void clear() throws CacheException {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			jedis.flushDB();
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

	@Override
	public int size() {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			return jedis.keys("shiro:*").size();
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

	@Override
	public Set<String> keys() {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			return jedis.keys("shiro:*");
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

	@Override
	public Collection<Object> values() {
		Jedis jedis = null;
		List<Object> values = new ArrayList<Object>();
		try {
			jedis = RedisPool.getJedis();
			Set<String> kyes = jedis.keys("shiro:*");
			Iterator<String> it = kyes.iterator();
			while(it.hasNext()) {
				String key = it.next();
				byte[] bytes = jedis.get(SerializationUtils.serialize(buildShiroKey(key)));
				Object value = SerializationUtils.deserialize(bytes);
				values.add(value);
			}
			return values;
		} catch (Exception e) {
			throw new CacheException(e.getMessage());
		} finally {
			RedisPool.close(jedis);
		}
	}

}
