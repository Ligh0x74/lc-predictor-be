package com.example.lcpredictor.utils.crawler;

/**
 * 公共的字段和方法
 */
public class Common {

    /**
     * 竞赛页面每页包含的用户数量
     */
    public static final int USER_PER_PAGE = 25;

    /**
     * 将竞赛名称转换为数据库中存储的竞赛编号, 计算方法: 周赛场次 * 2 + 1, 双周赛场次 * 2
     *
     * @param contestName 竞赛名称, 格式: biweekly-contest-xxx, weekly-contest-xxx
     * @return 竞赛编号
     */
    public static Integer parseContestName(String contestName) {
        int x = Integer.parseInt(contestName.substring(contestName.lastIndexOf('-') + 1));
        return x * 2 + (contestName.charAt(0) == 'b' ? 0 : 1);
    }
}
