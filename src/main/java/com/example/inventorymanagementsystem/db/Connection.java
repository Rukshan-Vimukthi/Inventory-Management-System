package com.example.inventorymanagementsystem.db;

import com.example.inventorymanagementsystem.models.Stock;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Connection {
    private java.sql.Connection connection;
    private static Connection connectionObject;
    private Statement statement;

    private Connection(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sandyafashioncorner", "root", "Sandun@2008.sd");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static Connection getInstance(){
        if (connectionObject == null){
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

    public ArrayList<Stock> getStocks(){
        ArrayList<Stock> rows = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `stock`");
            while (resultSet.next()){
                int id = resultSet.getInt(0);
                String date = resultSet.getString(1);
                String name = resultSet.getString(2);
                Stock stock = new Stock(id, date, name);
                rows.add(stock);
            }

        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return rows;
    }
}
