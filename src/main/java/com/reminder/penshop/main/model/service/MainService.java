package com.reminder.penshop.main.model.service;



import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.DesignImageDTO;

import java.util.List;

public interface MainService {

	List<ProductDTO> getTop8ProductByPopularity();

	List<ProductDTO> getLatest8ProductByEnrollDate();

	List<DesignImageDTO> getSliderImages();

	List<DesignImageDTO> getBannerImage();
}
