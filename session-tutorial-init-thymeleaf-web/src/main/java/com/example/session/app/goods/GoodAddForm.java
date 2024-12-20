package com.example.session.app.goods;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GoodAddForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

    @NotNull
    private String goodsId;

    @NotNull
    @Min(1)
    private int quantity;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
