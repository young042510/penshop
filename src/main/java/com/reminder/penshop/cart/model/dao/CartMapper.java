package com.reminder.penshop.cart.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.penshop.cart.model.dto.CartDTO;
import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;

@Mapper
public interface CartMapper {
	
	List<CartDTO> getCurrentCart(String username);
	
	CartDTO getCartItemByOptionNo(String username, int optionNo);
	
	int getMemberCartNo(String username);

	List<CartDTO> getCartItemList(CartDTO cartDTO);

	void addToCart(CartDTO cartDTO);

	void updateQuantityInCart(String memberId, int quantity, int optionNo);
	
	void deleteItemFromCart(String memberId, int optionNo);

	OptionDTO searchOptionInfoByOptionNo(int optionNo);

	ProductDTO searchProductInfoByOptionNo(int optionNo);

	BrandDTO searchBrandInfoByOptionNo(int optionNo);
}
