package com.github.zhangkaitao.shiro.chapter7.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

public class RedisCacheManager implements CacheManager {
	
	private Map<String,Cache<String,Object>> caches;
	
	public RedisCacheManager() {
		RedisPool.init("127.0.0.1", 6379, null);
		caches = new ConcurrentHashMap<String, Cache<String,Object>>();
	}

	@Override
	public Cache<String,Object> getCache(String name) throws CacheException {
		Cache<String,Object> cache = caches.get(name);
		if(cache==null) {
			cache = new RedisCache();
			caches.put(name, cache);
		}
		return cache;
	}

}
