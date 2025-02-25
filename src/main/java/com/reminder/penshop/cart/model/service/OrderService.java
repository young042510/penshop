package com.reminder.penshop.cart.model.service;

import com.reminder.penshop.cart.model.dto.DeliveryDTO;
import com.reminder.penshop.cart.model.dto.OrderDetailDTO;
import com.reminder.penshop.cart.model.dto.PaymentDTO;
import com.reminder.penshop.cart.model.dto.PointDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;

import net.sf.json.JSONArray;

public interface OrderService {

	boolean orderAndPay(OrderDetailDTO orderDetailDTO, JSONArray optionNoArr, JSONArray optionQtArr, JSONArray orderPriceArr, DeliveryDTO deliveryDTO, PointDTO pointDTO, PaymentDTO paymentDTO);

	OptionDTO getWishItemByOptionNo(String username, int optionNo);

	int getTheNumberOfEachOrder(String orderNo);
}
