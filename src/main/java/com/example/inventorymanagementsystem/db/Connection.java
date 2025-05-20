package com.example.inventorymanagementsystem.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private java.sql.Connection connection;
    private Connection connectionObject;
    private Statement statement;

    private Connection(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql:://localhost:3306/sandyafashioncorner");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getInstance(){
        if (connection == null){
            connectionObject = new Connection();
        }
        return connectionObject;
    }

    public void addNewColor(String colorCode){
        try {
            statement = connection.createStatement();
            boolean isSuccess = statement.execute("INSERT INTO `color` (`color`) VALUES(`%s`)".formatted(colorCode));
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    public List<String> getColors(){
        List<String> colorList = new ArrayList<>();
        return colorList;
    }

    public void addNewItem(){

    }

    public void getItems(){

    }

    public void addNewSize(){

    }

    public void getSizes(){

    }

    public void addNewUser(){

    }

    public void getUsers(){

    }

    public void addNewCustomer(){

    }

    public void getNewCustomer(){

    }
}
