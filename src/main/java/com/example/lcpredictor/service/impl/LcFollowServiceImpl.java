package com.example.lcpredictor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcFollow;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.mapper.LcFollowMapper;
import com.example.lcpredictor.service.LcFollowService;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.utils.ThreadLocals;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LcFollowServiceImpl extends ServiceImpl<LcFollowMapper, LcFollow>
        implements LcFollowService {

    @Autowired
    LcPredictService lcPredictService;

    @Autowired
    LcUserService lcUserService;

    @Override
    public Result<?> follow(String dataRegion, String username, Boolean isFollow) {
        // 创建 follow 和 wrapper 对象
        LcUserDTO userDTO = ThreadLocals.lcUserDTOThreadLocal.get();
        LcFollow follow = new LcFollow();
        follow.setSourceDataRegion(userDTO.getDataRegion());
        follow.setSourceUsername(userDTO.getUsername());
        follow.setTargetDataRegion(dataRegion);
        follow.setTargetUsername(username);
        LambdaQueryWrapper<LcFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LcFollow::getSourceDataRegion, userDTO.getDataRegion())
                .eq(LcFollow::getSourceUsername, userDTO.getUsername())
                .eq(LcFollow::getTargetDataRegion, dataRegion)
                .eq(LcFollow::getTargetUsername, username);
        // 如果需要关注, 且不在数据库中 (未关注), 如果存储数据; 如果需要取关, 则删除数据
        if (isFollow && !exists(wrapper)) {
            save(follow);
        } else if (!isFollow) {
            remove(wrapper);
        }
        return Result.success();
    }

    @Override
    public Result<PageVo<LcPredictDTO>> get(String contestName, Integer pageIndex, Integer pageSize) {
        // TODO: 如果分页会很麻烦, 需要查出所有关注用户的预测结果, 然后对 rank 排序, 再分页
        //  如果关注的人很多, 那么分页的性能会非常差 (或许可以建一个缓存中间结果的表)
        // 查询关注表, 手动分页需要判断是否越界
        LcUserDTO userDTO = ThreadLocals.lcUserDTOThreadLocal.get();
        List<LcFollow> follows = lambdaQuery().eq(LcFollow::getSourceDataRegion, userDTO.getDataRegion())
                .eq(LcFollow::getSourceUsername, userDTO.getUsername())
                .list();
        if (follows.size() <= (pageIndex - 1) * pageSize) {
            return Result.success();
        }
        List<LcPredictDTO> res = new ArrayList<>();
        // 查询预测表
        Integer contestId = Common.parseContestName(contestName);
        for (LcFollow f : follows) {
            LcPredict predict = lcPredictService.lambdaQuery()
                    .eq(LcPredict::getContestId, contestId)
                    .eq(LcPredict::getDataRegion, f.getTargetDataRegion())
                    .eq(LcPredict::getUsername, f.getTargetUsername())
                    .one();
            if (predict != null) {
                res.add(BeanUtil.copyProperties(predict, LcPredictDTO.class));
            }
        }
        // 排序, 分页
        res.sort(Comparator.comparingInt(LcPredictDTO::getRank));
        PageVo<LcPredictDTO> pageVo = new PageVo<>();
        pageVo.setCurrent((long) pageIndex);
        pageVo.setSize((long) pageSize);
        pageVo.setPages((long) (res.size() + pageSize - 1) / pageSize);
        pageVo.setTotal((long) res.size());
        res = res.subList((pageIndex - 1) * pageSize, Math.min(pageIndex * pageSize, res.size()));
        pageVo.setRecords(res);
        // 查询用户表
        res.forEach(predictDTO -> {
            LcUser user = lcUserService.lambdaQuery()
                    .eq(LcUser::getDataRegion, predictDTO.getDataRegion())
                    .eq(LcUser::getUsername, predictDTO.getUsername())
                    .one();
            predictDTO.setNickname(user.getNickname());
            predictDTO.setAvatar(user.getAvatar());
        });
        return Result.success(pageVo);
    }
}
