package com.example.lcpredictor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.mapper.LcPredictMapper;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.service.LcUserService;
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
    LcUserService lcUserService;

    @Override
    public Result<PageVo<LcPredictDTO>> get(String contestName, Integer pageIndex, Integer pageSize) {
        Integer contestId = Common.parseContestName(contestName);
        Page<LcPredict> page = new Page<>(pageIndex, pageSize);
        page.setRecords(lambdaQuery()
                .eq(LcPredict::getContestId, contestId)
                .orderByAsc(LcPredict::getRank)
                .list(page));
        List<LcPredictDTO> res = BeanUtil.copyToList(page.getRecords(), LcPredictDTO.class);
        res.forEach(predictDTO -> {
            LcUser user = lcUserService.lambdaQuery()
                    .eq(LcUser::getDataRegion, predictDTO.getDataRegion())
                    .eq(LcUser::getUsername, predictDTO.getUsername())
                    .one();
            predictDTO.setNickname(user.getNickname());
            predictDTO.setAvatar(user.getAvatar());
        });
        PageVo<LcPredictDTO> pageVo = PageVo.pageInfo(page);
        pageVo.setRecords(res);
        return Result.success(pageVo);
    }
}
