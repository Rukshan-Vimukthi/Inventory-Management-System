package com.example.inventorymanagementsystem.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    private java.sql.Connection connection;
    private Connection connectionObject;

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
}
