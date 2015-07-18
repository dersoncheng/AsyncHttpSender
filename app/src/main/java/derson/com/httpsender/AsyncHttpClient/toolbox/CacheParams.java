package derson.com.httpsender.AsyncHttpClient.toolbox;

/**
 * 缓存参数
 * 
 * @author yangzhi
 *
 */
public class CacheParams {

	private int expireTime; // 过期时间
	private int cacheType; // 缓存刷新类型
	private String cacheUrl; // 标示一次服务的token

	/**
	 * 构造一个缓存对象
	 * 
	 * @param storeType
	 *            存储类型
	 * @param expireTime
	 *            过期时间
	 * @param isRefresh
	 *            是否刷新
	 */
	public CacheParams(int expireTime, int isRefresh) {
		this.expireTime = expireTime;
		this.cacheType = isRefresh;
	}
//
//	public CacheParams(int isRefresh) {
//		this.cacheType = isRefresh;
//	}

	public CacheParams(int spec) {
		this.cacheType = CacheSpec.getType(spec);
		this.expireTime = CacheSpec.getExpire(spec);
		if(this.expireTime <= 0) {
			this.expireTime = Integer.MAX_VALUE;
		}
	}
	
	public void setCacheType(int isRefresh) {
		this.cacheType = isRefresh;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public int getCacheType() {
		return cacheType;
	}

	public void setCacheUrl(String cacheUrl) {
		this.cacheUrl = cacheUrl;
	}

	public String getCacheUrl() {
		return this.cacheUrl;
	}
	
	public static class CacheSpec {
		// 高2位作为cachetype的标志位，低位作为过期时间的标志位
		private static final int TYPE_SHIFT = 30;
		private static final int TYPE_MASK = 0X3 << TYPE_SHIFT;
		// 不取缓存，发送服务刷新
		public static final int TYPE_REFRESH = 0 << TYPE_SHIFT;
		// 取缓存
		public static final int TYPE_CACHE = 1 << TYPE_SHIFT;
		// 取缓存再发服务刷新
		public static final int TYPE_CACHE_AND_REFRESH = 2 << TYPE_SHIFT;
		// 缓存时间内不再发服务刷新
		public static final int TYPE_CACHE_NO_REFRESH_INTIME = 3 << TYPE_SHIFT;
		
		public static int makeCacheSpec(int cahcetype, int expire) {
			return cahcetype + expire;
		}
		
		public static int makeCacheSpec(int cachetype) {
			return cachetype;
		}
		
		public static int getType(int cacheSpec) {
			return cacheSpec & TYPE_MASK;
		}
		
		public static int getExpire(int cacheSpec) {
			return cacheSpec & ~TYPE_MASK;
		}
	}
}
