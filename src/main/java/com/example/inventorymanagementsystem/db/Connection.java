package com.example.inventorymanagementsystem.db;

import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.ItemDetail;
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sandyafashioncorner", "root", "root@techlix2002");
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

    public boolean addNewColor(String colorCode){
        try {
            // create the statement
            statement = connection.createStatement();

            // write the SQL query to insert color into the color table
            return statement.execute("INSERT INTO `color` (`color`) VALUES(`%s`)".formatted(colorCode));
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return false;
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

    public boolean addNewItem(String name, Double price, Double sellingPrice, int stockID, int sizeID, int colorID){
        try{
            statement = connection.createStatement();
            boolean isExecuted = statement.execute(
                    "INSERT INTO `item` (`name`, `price`, " +
                            "`selling_price`, `stock_id`)" +
                            "VALUES('%s', '%f', '%f', '%d')".formatted(
                                    name, price, sellingPrice, stockID
                            ));
            if (isExecuted){
                isExecuted = statement.execute("INSERT INTO `item_has_size` (" +
                        "`item_id`, `size_id`) ('%d', '%d')".formatted(1, sizeID));

                if (isExecuted){
                    isExecuted = statement.execute("INSERT INTO `color_has_item_has_size` (" +
                            "`color_id`, `item_has_size_id`) VALUES('%d', '%d')".formatted(
                                    colorID, 1
                            ));
                    return isExecuted;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<ItemDetail> getItemDetails(){
        ArrayList<ItemDetail> itemDetails = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `item` " +
                    "INNER JOIN `item_has_size` " +
                    "ON `item`.`id` = `item_has_size`.`item_id`" +
                    "INNER JOIN `stock` ON `item`.`stock_id` = `stock`.`id` " +
                    "INNER JOIN `size` ON `size`.`id` = `item_has_size`.`size_id` " +
                    "INNER JOIN `color_has_item_has_size` " +
                    "ON `item_has_size`.`id` = `color_has_item_has_size`.`item_has_size_id` " +
                    "INNER JOIN `color` ON `color_has_item_has_size`.`color_id` = `color`.`id`");

            while (resultSet.next()){
                int itemID = resultSet.getInt("item.id");
                String name = resultSet.getString("item.name");
                Double price = resultSet.getDouble("item.price");
                Double sellingPrice = resultSet.getDouble("item.selling_price");
                int stockID = resultSet.getInt("stock.id");
                String stockDate = resultSet.getString("stock.date");
                String stockName = resultSet.getString("stock.name");
                int itemSizeID = resultSet.getInt("size.id");
                String itemSize = resultSet.getString("size.size");
                int itemColorID = resultSet.getInt("color.id");
                String itemColor = resultSet.getString("color.color");

                ItemDetail itemDetail = new ItemDetail(
                        itemID,
                        name,
                        price,
                        sellingPrice,
                        stockID,
                        stockDate,
                        stockName,
                        itemSizeID,
                        itemSize,
                        itemColorID,
                        itemColor
                );

                System.out.println(name);

                itemDetails.add(itemDetail);
            }
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return itemDetails;
    }

    /**
     * Insert a new size value to the size table
     * @param size
     * @return boolean - true if the item is added successfully to the table. false otherwise
     */
    public boolean addNewSize(String size){
        try {
            statement = connection.createStatement();
            return statement.execute("INSERT INTO `size` (`size`) VALUES ('%s')".formatted(size));
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
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

    public boolean addNewUser(String firstName, String lastName, String email, int roleID){
        try{
            statement = connection.createStatement();
            return statement.execute("INSERT INTO `user` " +
                    "(`firstName`, `lastName`, `email`, `role_id`) " +
                    "VALUES('%s', '%s', '%s')".formatted(firstName, lastName, email, roleID));
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
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
