package com.reminder.penshop.main.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reminder.penshop.main.model.dao.MainMapper;
import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.DesignImageDTO;

@Service("mainService")
public class MainServiceImpl implements MainService {

	private final MainMapper mainMapper;
	
	@Autowired
	public MainServiceImpl(MainMapper mainMapper) {
		this.mainMapper = mainMapper;
	}
	
	@Override
	public List<ProductDTO> getTop8ProductByPopularity() {
		return mainMapper.getTop8ProductByPopularity();
	}

	@Override
	public List<ProductDTO> getLatest8ProductByEnrollDate() {
		return mainMapper.getLatest8ProductByEnrollDate();
	}

	@Override
	public List<DesignImageDTO> getSliderImages() {
		return mainMapper.getSliderImages();
	}

	@Override
	public List<DesignImageDTO> getBannerImage() {
		return mainMapper.getBannerImage();
	}
}
