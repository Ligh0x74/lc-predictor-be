package com.example.lcpredictor.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.dto.LcContestDTO;
import com.example.lcpredictor.mapper.LcContestMapper;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LcContestServiceImpl extends ServiceImpl<LcContestMapper, LcContest>
        implements LcContestService {

    @Override
    public Result<PageVo<LcContestDTO>> get(Integer pageIndex, Integer pageSize) {
        // 查询竞赛表
        Page<LcContest> page = new Page<>(pageIndex, pageSize);
        page.setRecords(lambdaQuery()
                .orderByDesc(LcContest::getStartTime)
                .list(page));
        // 转换数据格式
        List<LcContestDTO> res = new ArrayList<>();
        page.getRecords().forEach(contest -> {
            LcContestDTO contestDTO = new LcContestDTO();
            contestDTO.setContestName(Common.parseContestId(contest.getContestId()));
            contestDTO.setStartTime(contest.getStartTime());
            contestDTO.setPredictTime(contest.getPredictTime());
            res.add(contestDTO);
        });
        PageVo<LcContestDTO> pageVo = PageVo.pageInfo(page);
        pageVo.setRecords(res);
        return Result.success(pageVo);
    }
}
