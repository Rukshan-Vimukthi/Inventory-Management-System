package com.example.inventorymanagementsystem.db;

import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a singleton class. Use to do insert, update, delete, retrieve items from the database. to use methods to do those tasks with the
 * database use Connection.getInstance() to get the instance of the database connection. then call the methods available
 * to perform the operations with the database.
 */
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

    /**
     * create the instance of the Connection object if there is not any instance created.
     * @return Connection instance to perform CRUD operations with the database
     */
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

    public List<Color> getColors(){
        ArrayList<Color> rows = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `color`");
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String colorCode = resultSet.getString(2);
                Color color = new Color(id, colorCode);
                rows.add(color);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return rows;
    }

    public void addNewItem(){

    }

    public void getItems(){

    }

    public void addNewSize(){

    }

    public List<Size> getSizes(){
        ArrayList<Size> rows = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `size`");
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String sizeText = resultSet.getString(2);
                Size size = new Size(id, sizeText);
                rows.add(size);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return rows;
    }

    public void addNewUser(){

    }

    public void getUsers(){

    }

    public void addNewCustomer(){

    }

    public void getNewCustomer(){

    }

    /**
     * Retrieves and return the records in the stock table in the database
     * @return ArrayList that stores Stocks retrieved from the database
     */
    public ArrayList<Stock> getStocks(){
        ArrayList<Stock> rows = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `stock`");
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String name = resultSet.getString(3);
                System.out.println(id + " " + date + " " + name);
                Stock stock = new Stock(id, date, name);
                rows.add(stock);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return rows;
    }
}
