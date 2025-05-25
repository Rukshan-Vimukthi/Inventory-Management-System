package com.example.inventorymanagementsystem.db;

import com.example.inventorymanagementsystem.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
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
    private double price;

    public Connection(){
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

    /**
     * Update the color
     * @param id - color id
     * @param colorCode - new color code
     * @return boolean - true if update is successful. false otherwise
     */
    public boolean updateNewColor(int id, String colorCode){
        return false;
    }

    /**
     * Gets colors from the database and return an instance of Lost of colors
     * @return List of Color instances
     */
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

    public boolean addNewItem(String name, Integer qty, Double price, Double sellingPrice, int stockID, int sizeID, int colorID){
        try{
            statement = connection.createStatement();
            boolean isExecuted = statement.execute(
                    "INSERT INTO `item` (`name`, " +
                            "`selling_price`, `stock_id`)" +
                            "VALUES('%s', '%f', '%d')".formatted(
                                    name, sellingPrice, stockID
                            ));
            if (isExecuted){
                isExecuted = statement.execute("INSERT INTO `item_has_size` (" +
                        ("`item_id`, `item_stock_id`, `size_id`, `ordered_qty`, `cost`) " +
                                "('%d', '%d', '%d', '%d', '%f')").formatted(
                                        1, stockID, sizeID, qty, price));

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

    public boolean insertCustomerItem(int id, int customer_id, int amount, double price, String date) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO customer_has_item_has_size (id, customer_id, amount, price, date) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, id);
            ps.setInt(2, customer_id);
            ps.setInt(3, amount);
            ps.setDouble(4, price);
            ps.setString(5, date);

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all the details related to each item in the database.
     * @return List of type ItemDetail which holds all the information related to each item
     */

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
                Double price = resultSet.getDouble("item_has_size.cost");
                Double sellingPrice = resultSet.getDouble("item_has_size.selling_price");
                int stockID = resultSet.getInt("stock.id");
                String stockDate = resultSet.getString("stock.date");
                String stockName = resultSet.getString("stock.name");
                int itemHasSizeID = resultSet.getInt("item_has_size.id");
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
                        itemColor,
                        itemHasSizeID
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

    /**
     * Gets all the sizes from the database and return them
     * @return list of Size objects
     */
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
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rows;
    }

    // Fetch all Item Statuses from the Database
    public List<ItemStatus> getStatus() {
        List<ItemStatus> rows = new ArrayList<>();
        String query = "SELECT * FROM item_status";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String statusText = resultSet.getString("status");
                rows.add(new ItemStatus(id, statusText));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rows;
    }

    /**
     * Add a new user
     * @param firstName - first name of the user
     * @param lastName - last name of the user
     * @param email - email of the user
     * @param roleID - roleID - call getRoleIDs() method to get the roles available
     * @return boolean - true if the user is successfully added. false otherwise
     */
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

    /**
     * Get and returns all the users from the database
     */
    public void getUsers(){

    }

    public ResultSet getUser(String userName, String password){
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    ("SELECT * " +
                            "FROM `user` " +
                            "WHERE `username` = '%s' " +
                            "AND `password` = '%s'"
                    ).formatted(userName, password));
            resultSet.next();
            try {
                System.out.println(resultSet.getString("username"));
                return resultSet;
            }catch(SQLException e){
                e.printStackTrace();
                return null;
            }
//            System.out.println(resultSet.getFetchSize());
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return null;
    }

    /**
     * Add new customer
     */
    public void addNewCustomer(){

    }

    /**
     * Gets customers from the database and return the result
     */
    public void addCustomers(String first_name, String last_name, String phone, String email){
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeSales (int customerId, int itemHasSizeId, int amount, int price, int item_status_id) {
        String theCurrentDate = LocalDate.now().toString();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO customer_has_item_has_size (customer_id, item_has_size_id, amount, price, date, item_Status_id) VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, customerId);
            stmt.setInt(2, itemHasSizeId);
            stmt.setInt(3, amount);
            stmt.setInt(4, price);
            stmt.setString(5, theCurrentDate);
            stmt.setInt(6, item_status_id);

            stmt.executeUpdate();
            System.out.println("sale recorded successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<CheckoutItem> getCheckoutItems() {
        ObservableList<CheckoutItem> itemList = FXCollections.observableArrayList();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id, customer_id, item_has_size_id, amount, price, date, item_status_id FROM customer_has_item_has_size"
            );
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                int itemHasSizeId = rs.getInt("item_has_size_id");
                int amount = rs.getInt("amount");
                int price = rs.getInt("price");
                String date = rs.getString("date");
                int itemStatusId = rs.getInt("item_status_id");

                CheckoutItem item = new CheckoutItem(id, customerId, itemHasSizeId, amount, price, date, itemStatusId);
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemList;
    }


    /**
     * Get user roles from the database
     * @return List of type Role which contains role id and the role type (ex. Admin, User)
     */
    public List<Role> getRoles(){
        ArrayList<Role> roles = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `role`");
            while (resultSet.next()){
                int roleID = resultSet.getInt(1);
                String roleType = resultSet.getString(2);
                Role role = new Role(roleID, roleType);
                roles.add(role);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return roles;
    }


    /**
     * Add new stock
     */
    public boolean addNewStock(String date, String name){
        try{
            statement = connection.createStatement();
            return statement.execute("INSERT INTO `stock` (`date`, `name`) VALUES('%s', '%s)".formatted(date, name));
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
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
