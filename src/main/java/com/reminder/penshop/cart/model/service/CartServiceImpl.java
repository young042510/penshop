package com.reminder.penshop.cart.model.service;

import com.reminder.penshop.cart.model.dto.CartDTO;
import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;

import java.util.List;

public class CartServiceImpl implements CartService {

    private CartService cartService;

    @Override
    public List<CartDTO> getCurrentCart(String memberId) {
        return cartService.getCurrentCart(memberId);
    }

    @Override
    public CartDTO getCartItemByOptionNo(String username, int optionNo) {
        return cartService.getCartItemByOptionNo(username, optionNo);
    }

    @Override
    public List<CartDTO> getCartItemList(CartDTO cartDTO) {
        return cartService.getCartItemList(cartDTO);
    }

    @Override
    public OptionDTO searchOptionInfoByOptionNo(int optionNo) {
        return cartService.searchOptionInfoByOptionNo(optionNo);
    }

    @Override
    public ProductDTO searchProductInfoByOptionNo(int optionNo) {
        return cartService.searchProductInfoByOptionNo(optionNo);
    }

    @Override
    public BrandDTO searchBrandInfoByOptionNo(int optionNo) {
        return cartService.searchBrandInfoByOptionNo(optionNo);
    }
}
