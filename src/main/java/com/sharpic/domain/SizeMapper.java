package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by joey on 2016-12-08.
 */

@Mapper
public interface SizeMapper {
    public List<Size> getSizes();

    public void deleteSize(int sizeId);
}
