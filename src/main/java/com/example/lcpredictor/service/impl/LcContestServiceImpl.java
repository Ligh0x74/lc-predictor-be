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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class LcContestServiceImpl extends ServiceImpl<LcContestMapper, LcContest>
        implements LcContestService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<PageVo<LcContestDTO>> get(Integer pageIndex, Integer pageSize) {
        // 查询 redis, 缓存竞赛表
        String key = String.format("contest:%d", pageIndex);
        @SuppressWarnings("unchecked")
        PageVo<LcContestDTO> pageVo = (PageVo<LcContestDTO>) redisTemplate.opsForValue().get(key);
        if (pageVo == null) {
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
            pageVo = PageVo.pageInfo(page);
            pageVo.setRecords(res);
            redisTemplate.opsForValue().set(String.format("contest:%d", pageIndex),
                    pageVo, Duration.ofHours(1));
        }
        return Result.success(pageVo);
    }
}
