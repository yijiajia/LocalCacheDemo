package com.example.demo1;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static Map<String,Object> cacheMap = new ConcurrentHashMap<String,Object>();

    public static final int CLEAR_TIME = 5; // 定时5秒清除一次

    public CacheManager() {
        initClearThread();
    }

    public void initClearThread() {
        Thread thread = new Thread(new TimeOutRunnable());
        thread.setDaemon(true); // 设置为守护线程后，会随着主线程的结束而结束
        thread.setName("clear-cache-thread");
        thread.start();
    }

    public void put(String key,Object value) {
        long curTime = System.currentTimeMillis();
        CacheObject cacheObject = new CacheObject(value,curTime + 10 * 60 * 1000 * 1000);
        cacheMap.put(key,cacheObject);
    }

    public Object get(String key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }
        CacheObject cacheObject = (CacheObject)cacheMap.get(key);
        if(cacheObject.isExpire(key)){
            cacheMap.remove(key);
            return null;
        }else {
            return cacheObject.getValue();
        }
    }

    private static class CacheObject {
        private Object value;
        private long expireTime;    // 过期时间，毫秒

        public CacheObject(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }


        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public boolean isExpire(String key) {
            if(!cacheMap.containsKey(key)) {
                return true;
            }
            CacheObject cacheObject = (CacheObject)cacheMap.get(key);
            return cacheObject.getExpireTime() <= System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "CacheObject{" +
                    "value=" + value +
                    ", expireTime=" + expireTime +
                    '}';
        }


    }


    /**
     * 处理过期的线程
     */
    private static class TimeOutRunnable implements Runnable {

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(CLEAR_TIME * 1000 * 1000);
                    Iterator<Map.Entry<String,Object>> iterator = cacheMap.entrySet().iterator();
                    while(iterator.hasNext()) {
                        Map.Entry<String,Object> entry = iterator.next();
                        String key = entry.getKey();
                        CacheObject value  = (CacheObject)entry.getValue();
                        if(value.isExpire(key)) {
                            iterator.remove();
                        }
                    }
                }catch (Exception e) {

                }
            }
        }
    }


}
