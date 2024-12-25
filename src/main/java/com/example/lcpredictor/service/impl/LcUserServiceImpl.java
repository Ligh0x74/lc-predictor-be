package com.example.lcpredictor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.mapper.LcUserMapper;
import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.utils.crawler.Requests;
import com.example.lcpredictor.vo.Result;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LcUserServiceImpl extends ServiceImpl<LcUserMapper, LcUser>
        implements LcUserService {

    @Override
    public Result<LcUserDTO> login(String dataRegion, String username, HttpSession session) throws InterruptedException {
        // 查询用户表
        LcUser user = lambdaQuery().eq(LcUser::getDataRegion, dataRegion)
                .eq(LcUser::getUsername, username).one();
        if (user == null) {
            // 如果用户不在数据库中, 则请求用户信息, 然后将数据存储到数据库中
            user = new LcUser();
            user.setDataRegion(dataRegion);
            user.setUsername(username);
            parse(user, request(dataRegion, username));
            save(user);
        }
        LcUserDTO userDTO = new LcUserDTO();
        BeanUtil.copyProperties(user, userDTO);
        session.setAttribute("userDTO", userDTO);
        return Result.success(userDTO);
    }

    /**
     * 根据数据区域获取请求的 URL
     *
     * @param dataRegion 数据区域: CN/US
     * @return URL
     */
    private static String getUrl(String dataRegion) {
        if (dataRegion.equals("CN")) {
            return "https://leetcode.cn/graphql/";
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
                        "query":"query userProfilePublicProfile($userSlug: String!) {\
                            userProfilePublicProfile(userSlug: $userSlug) {\
                                profile {\
                                    realName\
                                    userAvatar\
                                }\
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
                        "query":"query userPublicProfile($username: String!) {
                            matchedUser(username: $username) {
                                profile {
                                  userAvatar
                                  realName
                                }
                            }
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
        String res = Requests.request(HttpRequest.post(getUrl(dataRegion))
                .body(getBody(dataRegion, username)));
        log.info(String.format("REQUEST: DATA_REGION %s USER %s -> RESPONSE: BODY %s",
                dataRegion, username, res));
        return res;
    }

    /**
     * 解析用户的 JSON 数据, 存储到用户对象中
     *
     * @param user 用户对象
     * @param json 用户的 JSON 数据
     */
    private static void parse(LcUser user, String json) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        if (user.getDataRegion().equals("CN")) {
            jsonObject = jsonObject.getByPath("data.userProfilePublicProfile.profile", JSONObject.class);
        } else {
            jsonObject = jsonObject.getByPath("data.matchedUser.profile", JSONObject.class);
        }
        user.setNickname(jsonObject.getStr("realName"));
        user.setAvatar(jsonObject.getStr("userAvatar"));
    }
}
