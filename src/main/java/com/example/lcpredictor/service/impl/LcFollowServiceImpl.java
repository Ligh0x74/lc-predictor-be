package com.example.lcpredictor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcFollow;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.mapper.LcFollowMapper;
import com.example.lcpredictor.service.LcFollowService;
import com.example.lcpredictor.utils.ThreadLocals;
import com.example.lcpredictor.vo.Result;
import org.springframework.stereotype.Service;

@Service
public class LcFollowServiceImpl extends ServiceImpl<LcFollowMapper, LcFollow>
        implements LcFollowService {

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
}
