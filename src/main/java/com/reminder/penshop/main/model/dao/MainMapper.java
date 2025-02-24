package com.reminder.penshop.main.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.DesignImageDTO;

@Mapper
public interface MainMapper {

	List<ProductDTO> getTop8ProductByPopularity();

	List<ProductDTO> getLatest8ProductByEnrollDate();

	List<DesignImageDTO> getSliderImages();

	List<DesignImageDTO> getBannerImage();
}
