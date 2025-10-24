package com.cosmocats.marketplace.web.mapper;

import com.cosmocats.marketplace.domain.Category;
import com.cosmocats.marketplace.web.dto.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);
}

