package com.example.demo1;

/**
 * 缓存客户端调用
 */
public class CacheClient {

    public static void main(String[] args) {
        CacheManager cacheManager = new CacheManager();

        cacheManager.put("key1","value1");
        cacheManager.put("key2","value2");

        /** 输出：
         * value1
         * value2
         */
        System.out.println(cacheManager.get("key1"));
        System.out.println(cacheManager.get("key2"));


    }


}
