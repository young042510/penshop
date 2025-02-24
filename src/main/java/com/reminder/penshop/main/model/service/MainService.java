package com.reminder.penshop.main.model.service;

import java.util.List;

import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.DesignImageDTO;

public interface MainService {

	List<ProductDTO> getTop8ProductByPopularity();

	List<ProductDTO> getLatest8ProductByEnrollDate();

	List<DesignImageDTO> getSliderImages();

	List<DesignImageDTO> getBannerImage();
}
