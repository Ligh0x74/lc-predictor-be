package com.example.lcpredictor.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页页面类
 */
@Getter
@Setter
public class PageVo<T> {

    /**
     * 当前页号
     */
    private Long current;

    /**
     * 每页数量
     */
    private Long size;

    /**
     * 总页面数
     */
    private Long pages;

    /**
     * 总数据量
     */
    private Long total;

    /**
     * 页面数据
     */
    private List<T> records;

    /**
     * 将 Page 转为 PageVo
     */
    public static <T, S> PageVo<T> pageInfo(Page<S> page) {
        PageVo<T> pageVo = new PageVo<T>();
        pageVo.setCurrent(page.getCurrent());
        pageVo.setSize(page.getSize());
        pageVo.setPages(page.getPages());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }
}
