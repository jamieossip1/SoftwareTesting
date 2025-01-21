package com.example.ilpcoursework.Data;

import com.example.ilpcoursework.Codes.OrderStatus;
import com.example.ilpcoursework.Codes.OrderValidationCode;

public class OrderValidationResult {
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    // Constructor
    public OrderValidationResult(OrderStatus orderStatus, OrderValidationCode orderValidationCode) {
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
    }

    // Getters and Setters
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }

    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }

}
