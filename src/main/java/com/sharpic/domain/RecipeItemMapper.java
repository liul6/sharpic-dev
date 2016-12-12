package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by joey on 2016-12-11.
 */

@Mapper
public interface RecipeItemMapper {
    public List<RecipeItem> getClientRecipeItems(String clientName);
}
