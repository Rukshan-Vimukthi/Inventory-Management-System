package com.example.inventorymanagementsystem.state;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Data {
    private static Data instance;
    private ObservableList<Stock> stocks;
    private ObservableList<Color> colors;
    private ObservableList<Size> size;
    private ObservableList<ItemDetail> itemDetails;

    private ObservableList<Customer> topTenCustomers;

    private ObservableList<Role> roles;

    private Connection connection;

    private UserAnalytics userAnalytics;

    private ObservableList<User> users;

    private ObservableList<Customer> customers;

    /**
     * initializes the Data object instance when the class get loaded
     */
    static {
        Data.getInstance();
        System.out.println("Data instance created!");
    }

    private Data(){
        connection = Connection.getInstance();
        stocks = FXCollections.observableArrayList(connection.getStocks());
        colors = FXCollections.observableArrayList(connection.getColors());
        size = FXCollections.observableArrayList(connection.getSizes());
        itemDetails = FXCollections.observableArrayList(connection.getItemDetails());
        topTenCustomers = FXCollections.observableArrayList(connection.getTopTenCustomers());
        roles = FXCollections.observableArrayList(connection.getRoles());
        userAnalytics = connection.getUserAnalyticsResult();
        users = FXCollections.observableArrayList(connection.getUsers());
        customers = FXCollections.observableArrayList(connection.getCustomers());
    }

    public void refreshStock(){
        stocks.clear();
        stocks.addAll(connection.getStocks());
    }

    public void refreshColors(){
        colors.clear();
        colors.addAll(connection.getColors());
    }

    public void refreshSize(){
        size.clear();
        size.addAll(connection.getSizes());
    }

    public void refreshItemDetails(){
        itemDetails.clear();
        itemDetails.addAll(connection.getItemDetails());
    }

    public void refreshTopTenCustomers(){
        topTenCustomers.clear();
        topTenCustomers.addAll(connection.getTopTenCustomers());
    }

    public void refreshRoles(){
        roles.clear();
        roles.addAll(connection.getRoles());
    }

    public void refreshUsers(){
        users.clear();
        users.addAll(connection.getUsers());
    }

    public void refreshCustomers(){
        customers.clear();
        customers.addAll(connection.getCustomers());
    }

    public static Data getInstance(){
        if (instance == null){
            instance = new Data();
        }
        return instance;
    }

    public ObservableList<Stock> getStocks(){
        return stocks;
    }

    public ObservableList<Color> getColors() {
        return colors;
    }

    public ObservableList<Size> getSize() {
        return size;
    }

    public ObservableList<ItemDetail> getItemDetails() {
        return itemDetails;
    }

    public UserAnalytics getUserAnalytics(){
        return userAnalytics;
    }

    public ObservableList<Customer> getTopTenCustomers(){
        return topTenCustomers;
    }

    public ObservableList<Role> getRoles(){
        return roles;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObservableList<Customer> getCustomers() {
        return customers;
    }

    public void setStocks(List<Stock> newStocks){
        stocks.clear();
        stocks.addAll(newStocks);
    }

    public void setColors(List<Color> newStocks){
        colors.clear();
        colors.addAll(newStocks);
    }

    public void setSize(List<Size> newSize){
        size.clear();
        size.addAll(newSize);
    }

    public void setRoles(List<Role> newRoles){
        roles.clear();
        roles.addAll(newRoles);
    }

    public void setItemDetails(List<ItemDetail> newItemDetails){

    }
}
