package com.cosmocats.marketplace.web.mapper;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.web.dto.ProductDTO;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "category", source = "category")
    ProductDTO toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tags", source = "tags")
    Product toEntity(ProductCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProductCreateDTO dto, @MappingTarget Product product);
}

