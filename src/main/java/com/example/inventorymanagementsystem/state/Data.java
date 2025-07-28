package com.example.inventorymanagementsystem.state;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data {
    private static Data instance;
    private ObservableList<Stock> stocks;
    private ObservableList<Color> colors;
    private ObservableList<Size> size;
    private ObservableList<ItemDetail> itemDetails;

    private ObservableList<Customer> topTenCustomers;
    private ObservableList<Customer> filteredCustomers;

    private ObservableList<Role> roles;

    private Connection connection;

    private UserAnalytics userAnalytics;

    private ObservableList<User> users;

    private ObservableList<Customer> customers;

    private ObservableList<LiableCustomers> liableCustomers;

    private SimpleIntegerProperty totalLiableCustomers;

    private ObservableList<ItemDetail> customerLiableItems;

    private ObservableList<CustomerSale> customerSales;

    private Double totalAccountsReceivable = 0.0D;
    private Double totalPoints = 0.0D;

    /**
     * initializes the Data object instance when the class get loaded
     */
//    static{
//        Data.getInstance();
//        System.out.println("Data instance created!");
//    }

    private Data() throws SQLException {
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
        filteredCustomers = FXCollections.observableArrayList(connection.getCustomers());
        totalLiableCustomers = new SimpleIntegerProperty();
        customerLiableItems = FXCollections.observableArrayList(new ArrayList<>());
        customerSales = FXCollections.observableArrayList(connection.getCustomerSales(null, 0));

        // Turn the liabilities map into an observable list which contains LiableCustomers objects
        liableCustomers = FXCollections.observableArrayList();
        refreshLiableCustomers();

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
        filteredCustomers.clear();
        customers.addAll(connection.getCustomers());
        filteredCustomers.addAll(connection.getCustomers());
    }

    public void refreshCustomerSales(String date, int customerID){
        customerSales.clear();
        customerSales.addAll(connection.getCustomerSales(date, customerID));
    }

    public void refreshCustomerLiableItems(Customer customer){
        List<ItemDetail> freshItems = connection.getCustomerLiableItems(customer);

        Platform.runLater(() -> {
            customerLiableItems.clear();
            customerLiableItems.addAll(freshItems);
        });
    }

    public void setCustomerLiableItems(List<ItemDetail> items){
        try {
            customerLiableItems.clear();
            customerLiableItems.addAll(items);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Data getInstance() throws SQLException{
        if (instance == null){
            instance = new Data();
        }
        instance.refreshLiableCustomers();
        return instance;
    }

    public void refreshLiableCustomers(){
        refreshCustomers();
        liableCustomers.clear();
        totalAccountsReceivable = 0.0D;
        totalPoints = 0.0D;
        int liableCustomerCount = 0;

        for (Map.Entry<Customer, Map<Integer, Sale>> entry : connection.getLiabilities().entrySet()){
            Customer customer = entry.getKey();

            double totalLiabilities = 0;

            for (Map.Entry<Integer, Sale> saleEntry : entry.getValue().entrySet()){
                double receivedMoney = saleEntry.getValue().getReceivedMoney();
                double cost = saleEntry.getValue().getTotalCost();

                if (receivedMoney < cost) {
                    totalLiabilities += (cost - receivedMoney);
                }else{
                    totalPoints += (receivedMoney - cost);
                }
            }

            if (totalLiabilities > 0.0D){
                liableCustomerCount += 1;
            }

            totalAccountsReceivable += totalLiabilities;
            LiableCustomers liableCustomer = new LiableCustomers(customer, totalLiabilities);
            liableCustomers.add(liableCustomer);
        }

        totalLiableCustomers.setValue(liableCustomerCount);
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
        itemDetails.clear();
        itemDetails.addAll(newItemDetails);
    }

    public void setUsers(List<User> filteredUsers){
        users.clear();
        users.addAll(filteredUsers);
    }

    public void setCustomers(List<Customer> filteredCustomers){
        customers.clear();
        customers.addAll(filteredCustomers);
    }

    public ObservableList<LiableCustomers> getLiableCustomers() {
        return liableCustomers;
    }

    public double getTotalAccountsReceivable(){
        return totalAccountsReceivable * -1;
    }

    public int getTotalLiableCustomers(){
        return totalLiableCustomers.get();
    }

    public double getTotalPoints(){
        return totalPoints;
    }

    public ObservableList<ItemDetail> getCustomerLiableItems(){
        return customerLiableItems;
    }

    public ObservableList<CustomerSale> getCustomerSales(){
        return customerSales;
    }

    public ObservableList<Customer> filterCustomers(String keyWord){
        if (keyWord != null){
            filteredCustomers.clear();
            for (Customer customer : customers){
                String customerName = "";
                if (customer.getFirstName() != null){
                    customerName += customer.getFirstName();
                }

                if (customer.getLastName() != null){
                    customerName += " " + customer.getLastName();
                }

                if (customerName.contains(keyWord) || (customer.getPhone() != null && customer.getPhone().contains(keyWord))){
                    filteredCustomers.add(customer);
                }
            }
        }
        return filteredCustomers;
    }
}
