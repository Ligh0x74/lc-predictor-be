package com.example.lcpredictor.utils;

public class RedisKey {

    /**
     * 指定竞赛页面的 redis key
     *
     * @param pageIndex 页号
     * @return redis key
     */
    public static String contestKey(Integer pageIndex) {
        return String.format("contest:%d", pageIndex);
    }

    /**
     * 指定竞赛的指定预测页面的 redis key
     *
     * @param contestName 竞赛名称
     * @param pageIndex   页号
     * @return redis key
     */
    public static String predictKey(String contestName, Integer pageIndex) {
        return String.format("predict:%s:%d", contestName, pageIndex);
    }

    /**
     * 指定竞赛的所有预测页面的 redis key
     *
     * @param contestName 竞赛名称
     * @return redis key
     */
    public static String predictKey(String contestName) {
        return String.format("predict:%s:*", contestName);
    }
}
