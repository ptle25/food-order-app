package com.example.foodorder.model;

import java.util.List;

public class OrderModel {
    String dateOrder;
    List<CartModel> foods;
    String documentID;
    String userID;

    public OrderModel() {
    }

    public OrderModel(String dateOrder, List<CartModel> foods, String documentID, String userID) {
        this.dateOrder = dateOrder;
        this.foods = foods;
        this.documentID = documentID;
        this.userID = userID;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public List<CartModel> getFoods() {
        return foods;
    }

    public void setFoods(List<CartModel> foods) {
        this.foods = foods;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
