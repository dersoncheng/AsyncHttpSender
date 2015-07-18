package derson.com.httpsender.AsyncHttpClient.toolbox;

import android.content.Context;
import android.content.SharedPreferences;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import derson.com.httpsender.AsyncHttpClient.http.HttpCallback;
import derson.com.httpsender.AsyncHttpClient.http.HttpFactory;
import derson.com.httpsender.AsyncHttpClient.http.impl.AsyncHttpHandler;
import derson.com.httpsender.AsyncHttpClient.http.impl.SyncHttpHandler;

/***
 * 缓存管理器
 * 
 * @author yangzhi
 */
public class CacheManager {
	private final static String TAG = CacheManager.class.getSimpleName();

	private static long maxInternalCacheSize = 100 * 1024 * 1024; // 最大文件缓存容量，单位：byte

	public static File cacheDirInternal; // 文件缓存目录
	public static File tempCacheDirInternal; // 中转文件缓存目录

	public final static int dataCacheExpire = 3600; // json数据缓存时间 单位 秒

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMdd");// 缓存目录加入（日期）

	private static SharedPreferences savePrefs;

	// 内存缓存
	private static ConcurrentHashMap<String, byte[]> memoryCache = new ConcurrentHashMap<String, byte[]>();
	// 内存缓存大小
	private static long memoryCacheSize = 0;
	// 内存缓存大小限制
	private static long memoryCacheLimit = 512 * 1024;// 0.5M

	private static final String SP_IS_LONG_TIME = "IS_LONG_TIME_";

	public static long getMaxInternalCacheSize() {
		return maxInternalCacheSize;
	}

	public static void setMaxInternalCacheSize(long maxInternalCacheSize) {
		CacheManager.maxInternalCacheSize = maxInternalCacheSize;
	}

	// 缓存任务集合
	private static ArrayList<CacheTask> cacheTasks = new ArrayList<CacheTask>();
	// 缓存线程
	private static Thread cacheThread = new Thread() {
		public void run() {
			while (true) {
				while (null != cacheTasks && !cacheTasks.isEmpty()
						&& cacheTasks.size() > 0) {
					CacheTask task = cacheTasks.get(0);
					// 保存缓存
					saveCache(task);
					if (cacheTasks != null && !cacheTasks.isEmpty()) {
						// 清除任务
						cacheTasks.remove(0);
					}
				}
				try {
					synchronized (this) {
						// 等待通知
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	};

	private static String cache_path;

	// // 启动缓存线程
	// static {
	//
	//
	// }

	/***
	 * 初始化缓存目录
	 * 
	 * @param cacheDirName
	 *            最外层目录名字 (例如"hupu/games/cache")
	 * @param context
	 */
	public static void initCacheDir(String cacheDirName, Context context) {
		savePrefs = context.getSharedPreferences("httpcache",
				Context.MODE_WORLD_READABLE);
		// initCacheDir("cachedir", HPBaseApplication.getInstance());
		// 初始化缓存目录
		CacheManager.cacheDirInternal = new File(FileUtils.getCachePath(context, cacheDirName));
		if (!CacheManager.cacheDirInternal.exists()
				|| CacheManager.cacheDirInternal.isFile()) {
			CacheManager.cacheDirInternal.mkdirs();
		}
		// 初始化临时存储目录
		CacheManager.tempCacheDirInternal = new File(
				CacheManager.cacheDirInternal, "temp");
		if (!CacheManager.tempCacheDirInternal.exists()
				|| CacheManager.tempCacheDirInternal.isFile()) {
			CacheManager.tempCacheDirInternal.mkdirs();
		}
		cacheThread.start();
	}

	/***
	 * 根据外部key获取缓存缓存系统标准key
	 * 
	 * @param key
	 *            外部key
	 * @return 缓存系统标准key
	 */
	private static String getCacheKey(String key) {
		String stdKey = null;
		if (key != null && key.trim().length() > 0) {
			stdKey = UUID.nameUUIDFromBytes(key.getBytes()).toString();
		}
		return stdKey;
	}

	/***
	 * 根据外部key获取缓存文件名
	 * 
	 * @param key
	 *            外部key
	 * @return 缓存文件名
	 */
	private static String getCacheFileName(String key) {
		return getCacheKey(key);
	}

	/***
	 * 写入缓存
	 * 
	 * @param key
	 *            外部key
	 * @param content 缓存内容
	 * @param expire
	 *            有效时间
	 * @param type
	 *            缓存类型
	 */
	public static void setCache(String key, byte[] content, long expire,
			boolean isLongTimeCache) {
		CacheTask task = new CacheTask();
		task.setKey(key);
		task.setContent(content);
		task.setExpire(expire);
		task.isLongTimeCache = isLongTimeCache;
		cacheTasks.add(task);
		synchronized (cacheThread) {
			cacheThread.notify();
		}
	}

	/***
	 * 保存缓存(存储内存缓存后将byte数组写入文件，并做数据库保存)
	 * 
	 * @param tempObject
	 */
	private synchronized static void saveCache(CacheTask task) {
		// 存入内存缓存
		saveAsMemory(task);
		// 将缓存写入文件
		saveAsFile(task);
	}

	/***
	 * 将byte数组保存到文件
	 * 
	 * @param tempObject
	 *            临时缓存对象
	 * @return 缓存文件
	 */
	private static File saveByteToFile(CacheTask task) {
		if (null == task) {
			return null;
		}
		// 保存文件
		File cacheFile = null;// 缓存文件
		File tempFile = null;// 临时缓存文件
		File cacheDir = null;// 缓存目录
		if (null == task.getKey() || null == task.getContent()
				|| task.getContent().length <= 0) {
			return null;
		}
		String fileName = getCacheFileName(task.getKey());// 文件名
		if (null == fileName) {
			return null;
		}
		String date = dateFormat.format(new Date());

		// 构造缓存文件名
		if (null == cacheDirInternal || !cacheDirInternal.exists()
				|| null == tempCacheDirInternal
				|| !tempCacheDirInternal.exists()) {
			return null;
		}
		cacheDir = new File(cacheDirInternal, date);
		cacheFile = new File(cacheDir, fileName);
		tempFile = new File(tempCacheDirInternal, fileName);
		if (cacheDir != null && !cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		try {
			// 将byte数组写入文件
			FileUtils.writeFile(tempFile, task.getContent());
			// 将临时缓存搬到缓存文件
			if (tempFile != null && tempFile.exists() && tempFile.isFile()) {
				if (cacheFile != null && cacheFile.exists()
						&& cacheFile.isFile()) {
					cacheFile.delete();
				}
				// 文件移动
				FileUtils.move(tempFile, cacheFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cacheFile;
	}

	/***
	 * 读取缓存
	 * 
	 * @param key
	 *            外部key
	 * @return 缓存数据
	 */
	public static byte[] getCache(CacheParams cacheParams) {
		byte[] content = null;
		if (null == cacheParams) {
			return null;
		}
		String key = cacheParams.getCacheUrl();
		// 验证url缓存是否过期
		long expireTime = savePrefs.getLong(key, -1);
		if (cacheParams.getCacheType() == CacheParams.CacheSpec.TYPE_CACHE_NO_REFRESH_INTIME
				|| (expireTime != -1 && expireTime > System.currentTimeMillis())) {
			if (null != memoryCache) {
				if (memoryCache.containsKey(key)) {
					return memoryCache.get(key);
				}
			}
			String date = dateFormat.format(new Date());
			File cacheDir = new File(cacheDirInternal, date);
			File cacheFile = new File(cacheDir, getCacheFileName(key));
			if (cacheFile != null && cacheFile.exists() && cacheFile.isFile()) {
				try {
					content = FileUtils.readFileToByte(cacheFile);
				} catch (IOException e) {
					content = null;
					e.printStackTrace();
				}
			} else {
				content = null;
			}
		}
		return content;
	}

	/***
	 * 清除所有缓存(内存缓存和文件缓存)
	 * 
	 * @param cacheType
	 *            缓存类型
	 */
	public synchronized static void clearAllCache() {

		// 清除临时缓存
		clearTempCache();

		// 清除应用缓存
		if (cacheDirInternal.exists() && cacheDirInternal.isDirectory()) {
			FileUtils.deleteDirectory(cacheDirInternal, false);
			// 初始化临时存储目录
			tempCacheDirInternal = new File(cacheDirInternal, "temp");
			if (!tempCacheDirInternal.exists() || tempCacheDirInternal.isFile()) {
				tempCacheDirInternal.mkdirs();
			}
		}
	}

	/**
	 * 清除临时缓存数据
	 * 
	 */
	private synchronized static void clearTempCache() {
		// 清除中转缓存
		if (tempCacheDirInternal.exists() && tempCacheDirInternal.isDirectory()) {
			FileUtils.deleteDirectory(tempCacheDirInternal, false);
		}
	}

//	/***
//	 * 获取可用缓存空间大小
//	 *
//	 * @param cacheType
//	 *            缓存类型
//	 * @return 可用缓存空间大小 字节
//	 */
//	private synchronized static long getAvailableCacheSize(int cacheType) {
//		return HPFileUtils.getAvailableStorageSize(cacheDirInternal);
//	}

	/***
	 * 获取已经使用的缓存大小
	 * 
	 * @param cacheType
	 * @return 已经使用的缓存大小 字节
	 */
	public synchronized static long getUsedCacheSize() {
		File cacheDir = cacheDirInternal;
		if (null == cacheDir || !cacheDir.exists()) {
			return 0;
		}
		return FileUtils.getDirSize(cacheDir);
	}

	/**
	 * 
	 * @param task
	 */
	private static void saveAsMemory(CacheTask task) {
		if (null != memoryCache) {
			memoryCache.put(task.key, task.content);
			memoryCacheSize += task.content.length;
			checkCacheSize();
		}
	}

	private static void saveAsFile(CacheTask task) {
		checkFileSize();
		File cacheFile = saveByteToFile(task);
		// 保存url-过期时间
		if (null != cacheFile && cacheFile.exists() && cacheFile.length() > 0) {
			savePrefs
					.edit()
					.putLong(task.key,
							System.currentTimeMillis() + task.getExpire())
					.putBoolean(SP_IS_LONG_TIME + cacheFile.getName(),
							task.isLongTimeCache).commit();

		}
	}

	private static void checkCacheSize() {
		if (memoryCacheSize > memoryCacheLimit) {
			// 内存缓存超过大小限制，清除内存缓存
			memoryCache.clear();
		}
	}

	private static void checkFileSize() {
		long cacheFileSize = getUsedCacheSize();
		if (cacheFileSize > maxInternalCacheSize) {
			// 文件缓存超过指定大小，删除临时目录和缓存文件
			clearAllCache();
			// 删除shareprf
			if (null != savePrefs) {
				savePrefs.edit().clear().commit();
			}
		} else {
			// 没有超过缓存大小限制，删除时间过期的文件缓存
			ArrayList<String> expireKey = getExpireKey(System
					.currentTimeMillis());
			if (expireKey.size() > 0) {
				String date = dateFormat.format(new Date());
				File cacheDir = new File(cacheDirInternal, date);
				File cacheFile;
				for (String del : expireKey) {
					cacheFile = new File(cacheDir, getCacheFileName(del));
					boolean isLongTimeCache = savePrefs.getBoolean(
							SP_IS_LONG_TIME + cacheFile.getName(), false);
					if (!isLongTimeCache)
						FileUtils.deleteFile(cacheFile);
				}
			}
		}
	}

	private static ArrayList<String> getExpireKey(long compareTime) {
		ArrayList<String> result = new ArrayList<String>();
		if (null != savePrefs) {
			Map<String, Object> entrys = (Map<String, Object>) savePrefs.getAll();
			Set<String> keys = entrys.keySet();
			if (null != keys) {
				for (String key : keys) {
					if (key.contains("SP_IS_LONG_TIME"))
						continue;
					Object obj = entrys.get(key);
					if (obj instanceof Long) {
						long saveExpireTime = (Long) obj;
						if (saveExpireTime < compareTime) {
							savePrefs.edit().remove(key);
							result.add(key);
						}
					}
				}
			}
		}
		return result;
	}

    public static boolean onlyUseCacheSync(int reqType,
                                       HttpCallback callback, CacheParams cacheParams) {
        int cache_type = CacheParams.CacheSpec.TYPE_REFRESH;
        if (null != cacheParams) {
            cache_type = cacheParams.getCacheType();
        }
        if (cache_type != CacheParams.CacheSpec.TYPE_REFRESH) {
            byte[] cacheData = CacheManager.getCache(cacheParams);
            if (null != cacheData) {
                SyncHttpHandler httpHandler = new SyncHttpHandler.Builder(
                        callback, reqType).setUseCache(true).create();
                httpHandler.onSuccess(200, null, cacheData);
            } else {
                // 缓存过期
                cache_type = CacheParams.CacheSpec.TYPE_REFRESH;
            }
        }

        boolean isCacheInTime = false;
        // 验证url缓存是否过期
        if (cacheParams!=null&&savePrefs != null) {
            long expireTime = savePrefs.getLong(cacheParams.getCacheUrl(), -1);
            if (expireTime != -1 && expireTime > System.currentTimeMillis()) {
                isCacheInTime = true;
            }
        }
        return (cache_type == CacheParams.CacheSpec.TYPE_CACHE || (cache_type == CacheParams.CacheSpec.TYPE_CACHE_NO_REFRESH_INTIME && isCacheInTime));
    }

	public static boolean onlyUseCache(int reqType, HttpFactory httpRes,
			HttpCallback callback, CacheParams cacheParams) {
		int cache_type = CacheParams.CacheSpec.TYPE_REFRESH;
		if (null != cacheParams) {
			cache_type = cacheParams.getCacheType();
		}
		if (cache_type != CacheParams.CacheSpec.TYPE_REFRESH) {
			byte[] cacheData = CacheManager.getCache(cacheParams);
			if (null != cacheData) {
				AsyncHttpHandler httpHandler = new AsyncHttpHandler.Builder(
						callback, reqType, httpRes).setUseCache(true).create();
				httpHandler.onSuccess(200, null, cacheData);
			} else {
				// 缓存过期
				cache_type = CacheParams.CacheSpec.TYPE_REFRESH;
			}
		}

		boolean isCacheInTime = false;
		// 验证url缓存是否过期
		if (cacheParams!=null&&savePrefs != null) {
			long expireTime = savePrefs.getLong(cacheParams.getCacheUrl(), -1);
			if (expireTime != -1 && expireTime > System.currentTimeMillis()) {
				isCacheInTime = true;
			}
		}
		return (cache_type == CacheParams.CacheSpec.TYPE_CACHE || (cache_type == CacheParams.CacheSpec.TYPE_CACHE_NO_REFRESH_INTIME && isCacheInTime));
	}

	/***
	 * 缓存对象
	 * 
	 * @author yangzhi
	 * 
	 */
	private static class CacheTask {
		private String key; // 缓存key值
		private byte[] content; // 缓存内容
		private long expire; // 过期时间
		public boolean isLongTimeCache; // 是否是长时间保存的Cache

		public long getExpire() {
			return expire;
		}

		public void setExpire(long expire) {
			this.expire = expire;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public byte[] getContent() {
			return content;
		}

		public void setContent(byte[] content) {
			this.content = content;
		}
	}

}
