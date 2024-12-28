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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LcPredictServiceImpl extends ServiceImpl<LcPredictMapper, LcPredict>
        implements LcPredictService {

    @Autowired
    private LcUserMapper lcUserMapper;

    @Autowired
    private LcFollowMapper lcFollowMapper;

    @Override
    public Result<PageVo<LcPredictDTO>> get(String contestName, Integer pageIndex, Integer pageSize) {
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
            // 如果用户已登录, 则包含关注信息
            predictDTO.setIsFollow(isFollow(predictDTO.getDataRegion(), predictDTO.getUsername()));
        });
        PageVo<LcPredictDTO> pageVo = PageVo.pageInfo(page);
        pageVo.setRecords(res);
        return Result.success(pageVo);
    }

    /**
     * 判断当前登录用户是否关注指定用户
     *
     * @param dataRegion 数据区域
     * @param username   用户名
     * @return 是否关注
     */
    public boolean isFollow(String dataRegion, String username) {
        LcUserDTO userDTO = ThreadLocals.lcUserDTOThreadLocal.get();
        // 用户未登录
        if (userDTO == null) {
            return false;
        }
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
            predictDTO.setIsFollow(isFollow(predictDTO.getDataRegion(), predictDTO.getUsername()));
        }
        return Result.success(predictDTO);
    }
}
