package com.example.foodorder.model;

import java.util.List;

public class OrderHistoryModel {
    String documentID;
    String dateOrder;
    String totalPrice;
    List<CartModel> foods;

    public OrderHistoryModel() {
    }



    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartModel> getFoods() {
        return foods;
    }

    public void setFoods(List<CartModel> foods) {
        this.foods = foods;
    }
}
