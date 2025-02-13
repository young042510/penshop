package com.reminder.penshop.cart.model.service;

import com.reminder.penshop.cart.model.dto.CartDTO;
import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;

import java.util.List;

public interface CartService {

    List<CartDTO> getCurrentCart(String memberId);

    CartDTO getCartItemByOptionNo(String username, int optionNo);

    List<CartDTO> getCartItemList(CartDTO cartDTO);

    OptionDTO searchOptionInfoByOptionNo(int optionNo);

    ProductDTO searchProductInfoByOptionNo(int optionNo);

    BrandDTO searchBrandInfoByOptionNo(int optionNo);
}
