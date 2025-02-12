package com.reminder.penshop.main.model.dao;

import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.DesignImageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MainMapper {
    List<ProductDTO> getTop8ProductByPopularity();

    List<ProductDTO> getLatest8ProductByEnrollDate();

    List<DesignImageDTO> getSliderImages();

    List<DesignImageDTO> getBannerImage();
}
