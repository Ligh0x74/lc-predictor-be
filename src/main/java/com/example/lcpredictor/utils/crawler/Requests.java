package com.example.lcpredictor.utils.crawler;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求工具类
 */
@Slf4j
public class Requests {

    /**
     * 请求失败的重试次数
     */
    public static final int RETRY = 3;

    /**
     * 每次请求的间隔时间
     */
    public static final int WAIT_MILLIS = 200;

    /**
     * 请求失败的等待时间
     */
    public static final int RETRY_WAIT_MILLIS = 10000;

    /**
     * 执行指定的请求, 并返回响应的 JSON 数据
     * 如果请求失败, 例如: 状态码不是 200, 或者抛出异常(由于网络超时), 则重试请求
     * 如果已经重试 {@link Requests#RETRY} 次, 则抛出异常
     *
     * @param req HttpRequest 请求对象
     * @return 请求响应的 JSON 数据
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    public static String request(HttpRequest req) throws InterruptedException {
        Thread.sleep(Requests.WAIT_MILLIS);
        for (int cnt = 0; cnt <= RETRY; cnt++) {
            try {
                HttpResponse response = req.header(Header.ACCEPT, "application/json").execute();
                if (response.getStatus() == 200) {
                    return response.body();
                }
                log.error(response.toString());
            } catch (IORuntimeException e) {
                log.error(e.getMessage());
            }
            Thread.sleep(RETRY_WAIT_MILLIS);
        }
        throw new RuntimeException("REQUEST FAILED: " + req);
    }

    /**
     * 抓取最近结束的竞赛信息
     *
     * @return 请求成功, 则为最近结束竞赛的 JSON 数据, 否则为 null
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    public static String request() throws InterruptedException {
        String url = "https://leetcode.cn/graphql";
        String body = """
                {\
                    "query":"query contestHistory($pageNum: Int!, $pageSize: Int) {\
                        contestHistory(pageNum: $pageNum, pageSize: $pageSize) {\
                            contests {\
                                titleSlug\
                                startTime\
                            }\
                        }\
                    }",\
                    "variables":{\
                        "pageNum":1,\
                        "pageSize":1\
                    }\
                }\
                """;
        String res = request(HttpRequest.post(url).body(body));
        log.info(String.format("REQUEST RECENT CONTEST INFO -> RESPONSE: BODY %s", res));
        return res;
    }

    /**
     * 抓取指定的竞赛页面数据
     *
     * @param contestName 竞赛名称
     * @param pageIndex   页号
     * @return 请求成功, 则为竞赛页面的 JSON 数据, 否则为 null
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    public static String request(String contestName, int pageIndex) throws InterruptedException {
        String url = String.format("https://leetcode.cn/contest/api/ranking/%s/?pagination=%d&region=global"
                , contestName, pageIndex);
        String res = request(HttpRequest.get(url));
        log.info(String.format("REQUEST: CONTEST %s PAGE %d -> RESPONSE: BODY %s",
                contestName, pageIndex, res));
        return res;
    }

    /**
     * 根据数据区域获取请求的 URL
     *
     * @param dataRegion 数据区域: CN/US
     * @return URL
     */
    private static String getUrl(String dataRegion) {
        if (dataRegion.equals("CN")) {
            return "https://leetcode.cn/graphql/noj-go/";
        } else {
            return "https://leetcode.com/graphql/";
        }
    }

    /**
     * 根据数据区域和用户名获取请求体
     *
     * @param dataRegion 数据区域: CN/US
     * @param username   用户名
     * @return 请求体, JSON 格式
     */
    private static String getBody(String dataRegion, String username) {
        if (dataRegion.equals("CN")) {
            return String.format("""
                    {\
                        "query":"query userContestRankingInfo($userSlug: String!) {\
                              userContestRanking(userSlug: $userSlug) {\
                                attendedContestsCount\
                                rating\
                              }\
                        }",\
                        "variables":{\
                            "userSlug":"%s"\
                        }\
                    }\
                    """, username);
        } else {
            return String.format("""
                    {\
                        "query":"query userContestRankingInfo($username: String!) {\
                              userContestRanking(username: $username) {\
                                attendedContestsCount\
                                rating\
                              }\
                        }",\
                        "variables":{\
                            "username":"%s"\
                        }\
                    }\
                    """, username);
        }
    }

    /**
     * 根据数据区域和用户名请求用户数据
     *
     * @param dataRegion 数据区域: CN/US
     * @param username   用户名
     * @return 请求成功, 则为用户的 JSON 数据, 否则为 null
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    public static String request(String dataRegion, String username) throws InterruptedException {
        String res = request(HttpRequest.post(getUrl(dataRegion))
                .body(getBody(dataRegion, username)));
        log.info(String.format("REQUEST: DATA_REGION %s USER %s -> RESPONSE: BODY %s",
                dataRegion, username, res));

        return res;
    }

    public static void main(String[] args) throws InterruptedException {
        request();
        request("weekly-contest-428", 1);
        request("CN", "yawn_sean");
    }
}
