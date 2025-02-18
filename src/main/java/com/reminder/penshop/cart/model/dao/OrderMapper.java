package com.reminder.penshop.cart.model.dao;

import com.reminder.penshop.cart.model.dto.*;
import com.reminder.penshop.product.model.dto.OptionDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    int addOrderInfo(OrderDTO orderDTO);

    int decreaseStockAmount(int orderQuantity, int optionNo);

    int addOrderDetail(OrderDetailDTO orderDetailDTO);

    int addPaymentInfo(PaymentDTO paymentDTO);

    OptionDTO getWishItemByOptionNo(String username, int optionNo);

    int addDeliveryInfo(DeliveryDTO deliveryDTO);

    int addPointInfo(PointDTO pointDTO);

    int getTheNumberOfEachOrder(String orderNo);
}