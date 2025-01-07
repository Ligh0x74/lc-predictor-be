package com.example.lcpredictor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcFollow;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.mapper.LcFollowMapper;
import com.example.lcpredictor.mapper.LcPredictMapper;
import com.example.lcpredictor.mapper.LcUserMapper;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.utils.ThreadLocals;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class LcPredictServiceImpl extends ServiceImpl<LcPredictMapper, LcPredict>
        implements LcPredictService {

    @Autowired
    private LcUserMapper lcUserMapper;

    @Autowired
    private LcFollowMapper lcFollowMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<PageVo<LcPredictDTO>> get(String contestName, Integer pageIndex, Integer pageSize) {
        // 查询 redis, 缓存预测表 + 用户表生成的数据, 关注信息依赖于登录用户, 所以不缓存
        String key = String.format("predict:%s:%d", contestName, pageIndex);
        @SuppressWarnings("unchecked")
        PageVo<LcPredictDTO> pageVo = (PageVo<LcPredictDTO>) redisTemplate.opsForValue().get(key);
        if (pageVo == null) {
            // 查询预测表
            Integer contestId = Common.parseContestName(contestName);
            Page<LcPredict> page = new Page<>(pageIndex, pageSize);
            page.setRecords(lambdaQuery()
                    .eq(LcPredict::getContestId, contestId)
                    .orderByAsc(LcPredict::getRanking)
                    .list(page));
            // 转换数据格式, 查询用户表
            List<LcPredictDTO> res = BeanUtil.copyToList(page.getRecords(), LcPredictDTO.class);
            res.forEach(predictDTO -> {
                LcUser user = lcUserMapper.selectOneByDataRegionAndUsername(
                        predictDTO.getDataRegion(), predictDTO.getUsername()
                );
                predictDTO.setNickname(user.getNickname());
                predictDTO.setAvatar(user.getAvatar());
            });
            pageVo = PageVo.pageInfo(page);
            pageVo.setRecords(res);
            redisTemplate.opsForValue().set(String.format("predict:%s:%d", contestName, pageIndex),
                    pageVo, Duration.ofHours(1));
        }
        // 如果用户已登录, 则包含关注信息
        LcUserDTO userDTO = ThreadLocals.lcUserDTOThreadLocal.get();
        if (userDTO != null) {
            pageVo.getRecords().forEach(predictDTO -> predictDTO.setIsFollow(
                    isFollow(userDTO, predictDTO.getDataRegion(), predictDTO.getUsername())));
        }
        return Result.success(pageVo);
    }

    /**
     * 判断当前登录用户是否关注指定用户
     *
     * @param dataRegion 数据区域
     * @param username   用户名
     * @return 是否关注
     */
    public boolean isFollow(LcUserDTO userDTO, String dataRegion, String username) {
        LcFollow follow = lcFollowMapper.selectOneBySourceDataRegionAndSourceUsernameAndTargetDataRegionAndTargetUsername(
                userDTO.getDataRegion(), userDTO.getUsername(), dataRegion, username
        );
        return follow != null;
    }

    @Override
    public Result<LcPredictDTO> get(String contestName, String dataRegion, String username) {
        // 查询预测表
        Integer contestId = Common.parseContestName(contestName);
        LcPredict predict = baseMapper.selectOneByContestIdAndDataRegionAndUsername(
                contestId, dataRegion, username
        );
        LcPredictDTO predictDTO = BeanUtil.copyProperties(predict, LcPredictDTO.class);
        // 如果用户在预测表中 (参赛), 则查询用户表
        if (predictDTO != null) {
            LcUser user = lcUserMapper.selectOneByDataRegionAndUsername(
                    predictDTO.getDataRegion(), predictDTO.getUsername());
            predictDTO.setNickname(user.getNickname());
            predictDTO.setAvatar(user.getAvatar());
            // 判断是否登录, 如果登录则查询是否关注
            LcUserDTO userDTO = ThreadLocals.lcUserDTOThreadLocal.get();
            if (userDTO != null) {
                predictDTO.setIsFollow(isFollow(userDTO, predictDTO.getDataRegion(), predictDTO.getUsername()));
            }
        }
        return Result.success(predictDTO);
    }
}
