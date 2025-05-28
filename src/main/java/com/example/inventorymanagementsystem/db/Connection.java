package com.example.inventorymanagementsystem.db;

import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.state.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

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

    public java.sql.Connection getJdbcConnection() {
        return connection;
    }

    public ItemHasSize getItemHasSize(ItemDetail itemDetail){
        ItemHasSize itemHasSize = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT *  FROM `item_has_size` " +
                            "WHERE `item_id` = %d AND `size_id` = %d".formatted(itemDetail.getId(), itemDetail.getSizeID()));
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                int itemID = resultSet.getInt("item_id");
                int stockID = resultSet.getInt("item_stock_id");
                int sizeID = resultSet.getInt("size_id");
                int orderedQty = resultSet.getInt("ordered_qty");
                int remainingQty = resultSet.getInt("remaining_qty");
                double cost = resultSet.getDouble("cost");
                double sellingPrice = resultSet.getDouble("price");
                itemHasSize = new ItemHasSize(id, itemID, stockID, sizeID, orderedQty, cost, sellingPrice,  remainingQty);
            }
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return itemHasSize;
    }

    public int getTotalSoldQuantity() {
        int soldQuantity = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery(
                    "SELECT SUM(ordered_qty - remaining_qty) AS total_sold FROM item_has_size"
            );

            if (query.next()) {
                soldQuantity = query.getInt("total_sold");
            }
            query.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldQuantity;
    }

    public int getOrderedQuantityValue() {
        int orderedItemsValue = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery("SELECT SUM(price) AS totalPrice FROM item_has_size");

            if (query.next()) {
                orderedItemsValue = query.getInt("totalPrice");
            }
            query.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderedItemsValue;
    }

    public int getTotalProducts() {
        int totalProducts = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery("SELECT SUM(remaining_qty) AS totalProducts FROM item_has_size");

            if (query.next()) {
                totalProducts = query.getInt("totalProducts");
            }
            query.close();
            statement.close();


        }catch (SQLException e){
            e.printStackTrace();
        }
        return totalProducts;
    }

    public int getTotalProductValue() {
        int totalProductValue = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery("SELECT SUM(price) AS totalPrice FROM item_has_size");

            if (query.next()) {
                totalProductValue = query.getInt("totalPrice");
            }
            query.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalProductValue;
    }

    public int getRemainingProductsSum() {
        int totalRemainingProducts = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery("SELECT SUM(remaining_qty) AS remainingAmount FROM item_has_size");

            if (query.next()) {
                totalRemainingProducts = query.getInt("remainingAmount");
            }
            query.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return totalRemainingProducts;
    }

    public static int getLowStockItemCount(Connection connection) {
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        int count = 0;

        for (ItemHasSize item : allItems) {
            if (item.getRemainingQuantity() < 20) {
                count++;
            }
        }

        return count;
    }

    public Map<String, List<SoldProducts>> getTopAndBottomSellingProducts() {
        Map<String, List<SoldProducts>> soldResult = new HashMap<>();
        List<SoldProducts> topSelling = new ArrayList<>();
        List<SoldProducts> bottomSelling = new ArrayList<>();

        String topQuery =  "SELECT item_id, SUM(ordered_qty - remaining_qty) AS total_sold " +
                "FROM item_has_size GROUP BY item_id " +
                "ORDER BY total_sold DESC LIMIT 3";

        String bottomQuery = "SELECT item_id, SUM(ordered_qty - remaining_qty) AS total_sold " +
                "FROM item_has_size GROUP BY item_id " +
                "ORDER BY total_sold ASC LIMIT 3";

        try (Statement stmt = connection.createStatement()){
            ResultSet rsTop = stmt.executeQuery(topQuery);
            while (rsTop.next()) {
                topSelling.add(new SoldProducts(rsTop.getInt("item_id"), rsTop.getInt("total_sold")));
            }

            // Bottom 3
            ResultSet rsBottom = stmt.executeQuery(bottomQuery);
            while (rsBottom.next()) {
                bottomSelling.add(new SoldProducts(rsBottom.getInt("item_id"), rsBottom.getInt("total_sold")));
            }

            soldResult.put("top", topSelling);
            soldResult.put("bottom", bottomSelling);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldResult;
    };

    public String getItemNameById(int itemId) {
        String name = "Unknown";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM item WHERE id = " + itemId);
            if (rs.next()) {
                name = rs.getString("name");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Get the items in the item_has_size table
     * @param
     * @return ItemHasSize object that contains all the information for the specified item
     */
    public ArrayList<ItemHasSize> getAllItemHasSizes() {
        ArrayList<ItemHasSize> items = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `item_has_size`");
            while (resultSet.next()) {
                items.add(new ItemHasSize(
                        resultSet.getInt("id"),
                        resultSet.getInt("item_id"),
                        resultSet.getInt("item_stock_id"),
                        resultSet.getInt("size_id"),
                        resultSet.getInt("ordered_qty"),
                        resultSet.getDouble("cost"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("remaining_qty")
                ));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return items;
    }

    public int addNewColor(String colorCode){
        try {
            // create the statement
            statement = connection.createStatement();

            // write the SQL query to insert color into the color table
            boolean success = statement.execute("INSERT INTO `color` (`color`) VALUES('%s')".formatted(colorCode), Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            System.out.println("New Color ID: " + id);
            Data.getInstance().refreshColors();
            return id;
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return -1;
    }

    /**
     * Update the color
     * @param id - color id
     * @param colorCode - new color code
     * @return boolean - true if update is successful. false otherwise
     */
    public boolean updateNewColor(int id, String colorCode){
        try{
            statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE `color` SET `color` = '%s' WHERE `id` = %d".formatted(colorCode, id));
            System.out.println(result);
            Data.getInstance().refreshColors();
            return result > 0;
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public List<Color> filterColors(String colorCode){
        ArrayList<Color> rows = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `color` WHERE `color` LIKE '%" + colorCode + "%'");
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String colorCodeValue = resultSet.getString(2);
                Color color = new Color(id, colorCodeValue);
                rows.add(color);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return rows;
    }

    /**
     * Get the color by using the color code
     * @param colorCode - color code (ex. #FF00FF)
     * @return Color - color object for the color code specified.
     * null if there is no any color match with the color code specified
     */
    public Color getColorByCode(String colorCode){
        Color color = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `color` WHERE `color` = '" + colorCode + "'");
            resultSet.next();
            int id = resultSet.getInt(1);
            String colorCodeValue = resultSet.getString(2);
            color = new Color(id, colorCodeValue);
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return color;
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

    public boolean deleteColor(int id){
        try{
            statement = connection.createStatement();
            boolean isDeleted = statement.execute("DELETE FROM `color` WHERE `id` = %d".formatted(id));
            Data.getInstance().refreshColors();
            return isDeleted;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add a new item (product) to the database
     *
     * @param name - Name of the item
     * @param qty - Ordered Quantity there were at the time when the item get inserted
     * @param price - Unit price that had been assigned to the item when placed an order
     * @param sellingPrice - Selling price of the item
     * @param stockID - ID of the stock to which the item belongs
     * @param sizeID - ID of the size which the item has
     * @param colorID - ID of the color which the item has
     * @return boolean - true if insertion is successful. false otherwise
     */
    public boolean addNewItem(String name, Integer qty, Double price, Double sellingPrice, int stockID, int sizeID, int colorID){
        System.out.println(name);
        System.out.println(qty);
        System.out.println(price);
        System.out.println(sellingPrice);
        System.out.println(stockID);
        System.out.println(sizeID);
        System.out.println(colorID);
        try{
            statement = connection.createStatement();
            statement.execute(
                    "INSERT INTO `item` (`name`, " +
                            "`price`, `stock_id`)" +
                            "VALUES('%s', '%f', '%d')".formatted(
                                    name, sellingPrice, stockID
                            ), Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            statement.execute("INSERT INTO `item_has_size` (" +
                        "`item_id`, `item_stock_id`, `size_id`, `ordered_qty`, `cost`, `price`, `remaining_qty`) " +
                                "VALUES(%d, %d, %d, %d, %f, %f, %d)".formatted(
                                        id, stockID, sizeID, qty, price, sellingPrice, qty), Statement.RETURN_GENERATED_KEYS);
            generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            id = generatedKeys.getInt(1);
            statement.execute("INSERT INTO `color_has_item_has_size` (" +
                            "`color_id`, `item_has_size_id`) VALUES('%d', '%d')".formatted(
                                    colorID, id
                            ), Statement.RETURN_GENERATED_KEYS);
            Data.getInstance().refreshItemDetails();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update the information of the item selected
     * @return
     */
    public boolean updateItem(int itemID, int itemHasSizeID, int colorHasItemHasSizeId, String name, Integer qty, Integer remainingQty, Double price, Double sellingPrice, int stockID, int sizeID, int colorID){
        try{
            statement = connection.createStatement();
            statement.execute(
                    "UPDATE `item` SET `name` = '%s', `price` = %f, `stock_id` = %d WHERE `id` = %d".formatted(
                            name, price, stockID, itemID
                    ));


            statement.execute(
                    "UPDATE `item_has_size` SET `item_id` = %d, `item_stock_id` = %d, `size_id` = %d, `ordered_qty` = %d, `cost` = %f, `price` = %f, `remaining_qty` = %d WHERE `id` = %d ".formatted(
                            itemID, stockID, sizeID, qty, price, sellingPrice, remainingQty, itemHasSizeID
                    ));

            statement.execute("UPDATE `color_has_item_has_size` SET " +
                    "`color_id` = %d WHERE `id` = %d".formatted(
                            colorID, colorHasItemHasSizeId
                    ));
            Data.getInstance().refreshItemDetails();
            return true;
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
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
                    "LEFT JOIN `item_has_size` " +
                    "ON `item`.`id` = `item_has_size`.`item_id`" +
                    "LEFT JOIN `stock` ON `item`.`stock_id` = `stock`.`id` " +
                    "LEFT JOIN `size` ON `size`.`id` = `item_has_size`.`size_id` " +
                    "LEFT JOIN `color_has_item_has_size` " +
                    "ON `item_has_size`.`id` = `color_has_item_has_size`.`item_has_size_id` " +
                    "LEFT JOIN `color` ON `color_has_item_has_size`.`color_id` = `color`.`id`");

            while (resultSet.next()){
                int itemID = resultSet.getInt("item.id");
                String name = resultSet.getString("item.name");
                Double price = resultSet.getDouble("item_has_size.cost");
                Double sellingPrice = resultSet.getDouble("item_has_size.price");
                int orderedQty = resultSet.getInt("item_has_size.ordered_qty");
                int remainingQty = resultSet.getInt("item_has_size.remaining_qty");
                int stockID = resultSet.getInt("stock.id");
                String stockDate = resultSet.getString("stock.date");
                String stockName = resultSet.getString("stock.name");
                int itemHasSizeID = resultSet.getInt("item_has_size.id");
                int itemSizeID = resultSet.getInt("size.id");
                String itemSize = resultSet.getString("size.size");
                int itemColorID = resultSet.getInt("color.id");
                String itemColor = resultSet.getString("color.color");
                int colorHasItemHasSizeID = resultSet.getInt("color_has_item_has_size.id");

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
                        itemHasSizeID,
                        colorHasItemHasSizeID,
                        orderedQty,
                        remainingQty
                );
                System.out.println(name);

                itemDetails.add(itemDetail);
            }

        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return itemDetails;
    }

    // For setting the dates as filters for showing the sales
    public ResultSet getFilteredSalesData (String filter) throws SQLException {
        String query =  "SELECT chs.date, chs.item_has_size_id FROM customer_has_item_has_size chs WHERE ";

        if (filter == null) {
            filter = "";
        }

        switch (filter) {
            case "Today":
                query += "DATE(chs.date) = CURDATE()";
                break;
            case "Yesterday":
                query += "DATE(chs.date) = CURDATE() - INTERVAL 1 DAY";
                break;
            case "Last 7 Days":
                query += "chs.date >= CURDATE() - INTERVAL 7 DAY";
                break;
            case "This Month":
                query += "MONTH(chs.date) = MONTH(CURDATE()) AND YEAR(chs.date) = YEAR(CURDATE())";
                break;
            case "Last Month":
                query += "MONTH(chs.date) = MONTH(CURDATE() - INTERVAL 1 MONTH) AND YEAR(chs.date) = YEAR(CURDATE() - INTERVAL 1 MONTH)";
                break;
            case "This Year":
                query += "YEAR(chs.date) = YEAR(CURDATE())";
                break;
            case "Last Year":
                query += "YEAR(chs.date) = YEAR(CURDATE() - INTERVAL 1 YEAR)";
                break;
            default:
                query = "SELECT chs.date, chs.item_has_size_id FROM customer_has_item_has_size chs";
                break;
        }
        PreparedStatement stmt = connection.prepareStatement(query);
        return stmt.executeQuery();
    }

    public void filterItems(String color, String size, Stock stock, double price, String name, double sellingPrice){
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `item` " +
                    "INNER JOIN `item_has_size` " +
                    "ON `item`.`id` = `item_has_size`.`item_id`" +
                    "INNER JOIN `stock` ON `item`.`stock_id` = `stock`.`id` " +
                    "INNER JOIN `size` ON `size`.`id` = `item_has_size`.`size_id` " +
                    "INNER JOIN `color_has_item_has_size` " +
                    "ON `item_has_size`.`id` = `color_has_item_has_size`.`item_has_size_id` " +
                    "INNER JOIN `color` ON `color_has_item_has_size`.`color_id` = `color`.`id` WHERE " +
                    "`color`.`color` = '%%s%' OR " +
                    "`size`.`size` = '%%s%' OR " +
                    "`item`.`name` = '%%s%' OR " +
                    "`stock`.`date` = '%%s%' OR " +
                    "`stock`.`name` = '%%s%' OR " +
                    "`item_has_size`.`cost` LIKE %f OR " +
                    "`item_has_size`.`price` LIKE %f".formatted(color, size, name, stock.getDate(), stock.getName(), price, sellingPrice
                    ));

            while (resultSet.next()){
                System.out.println(resultSet.getString("item.name"));
            }
        }catch(SQLException exception){
            exception.printStackTrace();
        }
    }

    /**
     * Insert a new size value to the size table
     * @param size - Size of the item
     * @return boolean - true if the item is added successfully to the table. false otherwise
     */
    public boolean addNewSize(String size){
        try {
            statement = connection.createStatement();
            boolean isAdded = statement.execute("INSERT INTO `size` (`size`) VALUES ('%s')".formatted(size));
            Data.getInstance().refreshSize();
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

    public Size getSize(int sizeID){
        Size size = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `size` WHERE `id` = %d".formatted(sizeID));
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String sizeText = resultSet.getString(2);
                size = new Size(id, sizeText);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return size;
    }

    public List<Size> filterSizes(String sizeText){
        List<Size> sizes = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `size` WHERE `size` LIKE '%" + sizeText + "%'");
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String sizeValue = resultSet.getString(2);
                Size size = new Size(id, sizeValue);
                sizes.add(size);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return sizes;
    }

    public boolean updateSize(int id, String newSize){
        try{
            statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE `size` SET `size` = '%s' WHERE `id` = %d".formatted(newSize, id));
            System.out.println(result);
            Data.getInstance().refreshSize();
            return result > 0;
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public boolean deleteSize(int id){
        try{
            statement = connection.createStatement();
            boolean isDeleted = statement.execute("DELETE FROM `size` WHERE `id` = %d".formatted(id));
            Data.getInstance().refreshSize();
            return isDeleted;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
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
            boolean isUserAdded = statement.execute("INSERT INTO `user` " +
                    "(`firstName`, `lastName`, `email`, `role_id`) " +
                    "VALUES('%s', '%s', '%s')".formatted(firstName, lastName, email, roleID));
//            Data.getInstance().refreshUsers();
            return isUserAdded;
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
            } catch(SQLException e){
                e.printStackTrace();
                return null;
            }
//            System.out.println(resultSet.getFetchSize());
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return null;
    }

    /*
    * Get the details of all the users and show the details
    * */
    public ObservableList<User> getUserDetail() {
        ObservableList<User> userList = FXCollections.observableArrayList();

        String query = "SELECT `id`, `firstName`, `lastName`, `username`, `email`, `password` FROM user";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
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

    /*
    * Gets the customers who has bought stuffs
    * */
    public int getTotalCustomers() {
        int totalCustomers = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery("SELECT COUNT(*) AS totalCustomers FROM customer");

            if (query.next()) {
                totalCustomers = query.getInt("totalCustomers");
            }
            query.close();
            statement.close();

        } catch (SQLException e){
            e.printStackTrace();
        }
        return totalCustomers;
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
                    "SELECT i.name AS item_name, s.size AS item_size, c.color AS item_color, " +
                            "chs.amount, chs.price AS checkout_price, chs.date, ihs.price AS selling_price " +  // <-- FIXED
                            "FROM customer_has_item_has_size chs " +
                            "JOIN item_has_size ihs ON chs.item_has_size_id = ihs.id " +
                            "JOIN item i ON ihs.item_id = i.id " +
                            "JOIN size s ON ihs.size_id = s.id " +
                            "JOIN color c ON i.color_id = c.id"
            );

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("item_name");
                String size = rs.getString("item_size");
                String color = rs.getString("item_color");
                int amount = rs.getInt("amount");
                int price = rs.getInt("price");
                double sellingPrice = rs.getDouble("selling_price");
                String date = rs.getString("date");

                CheckoutItem item = new CheckoutItem(name, size, color, amount, price, sellingPrice, date);
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    public ObservableList<CheckoutItem> getCheckoutItemWithoutColor() {
        ObservableList<CheckoutItem> itemList = FXCollections.observableArrayList();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT i.name AS item_name, s.size AS item_size, " +
                            "chs.amount, chs.price AS checkout_price, chs.date, ihs.price AS selling_price " +
                            "FROM customer_has_item_has_size chs " +
                            "JOIN item_has_size ihs ON chs.item_has_size_id = ihs.id " +
                            "JOIN item i ON ihs.item_id = i.id " +
                            "JOIN size s ON ihs.size_id = s.id"
            );
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("item_name");
                String size = rs.getString("item_size");
                String color = "N/A";
                int amount = rs.getInt("amount");
                int price = rs.getInt("checkout_price");
                double sellingPrice = rs.getDouble("selling_price");
                String date = rs.getString("date");

                CheckoutItem item = new CheckoutItem(name, size, color, amount, price, sellingPrice, date);
                itemList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();;
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
     * Method for showing the sales that we have done
     */

    public XYChart.Series<String, Number> getSalesChartSeries() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        String query = "SELECT chs.date, chs.price " +
                "FROM customer_has_item_has_size chs";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("date");
                int price = rs.getInt("price");

                // Add data point to the chart series
                series.getData().add(new XYChart.Data<>(date, price));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return series;
    }

    /**
     * Add new stock
     */
    public boolean addNewStock(String date, String name){
        try{
            statement = connection.createStatement();
            boolean isStockAdded = statement.execute("INSERT INTO `stock` (`date`, `name`) VALUES('%s', '%s')".formatted(date, name));
            Data.getInstance().refreshStock();
            return isStockAdded;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStock(int id, String date, String newName){
        try{
            statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE `stock` SET `name` = '%s', `date` = '%s' WHERE `id` = %d".formatted(newName, date, id));
            System.out.println(result);
            Data.getInstance().refreshStock();
            return result > 0;
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public List<Stock> filterStocks(String value){
        ArrayList<Stock> rows = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `stock` WHERE `date` LIKE ? OR `name` LIKE ?");
            preparedStatement.setString(1, '%' + value + '%');
            preparedStatement.setString(2, '%' + value + '%');

            ResultSet resultSet = preparedStatement.executeQuery();
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

    public Stock getStock(int stockId){
        Stock stock = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `stock` WHERE `id` = %d".formatted(stockId));
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String name = resultSet.getString(3);
                System.out.println(id + " " + date + " " + name);
                stock = new Stock(id, date, name);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return stock;
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

    public boolean deleteStock(int id){
        try{
            statement = connection.createStatement();
            boolean isDeleted = statement.execute("DELETE FROM `stock` WHERE `id` = %d".formatted(id));
            Data.getInstance().refreshStock();
            return isDeleted;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public UserAnalytics getUserAnalyticsResult(){
        UserAnalytics userAnalytics = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `user` INNER JOIN `role` ON `role`.`id` = `user`.`id`");
            int totalUsers = 0;
            int totalAdminUsers = 0;
            int totalStaffMembers = 0;

            while (resultSet.next()){
                String role = resultSet.getString("role.role");
                System.out.println(role);
                System.out.println(resultSet.getString("role.role"));
                if (role.equals("Admin")){
                    totalAdminUsers += 1;
                }else if(role.equals("Cashier")){
                    totalStaffMembers += 1;
                }
                totalUsers += 1;
            }

            resultSet = statement.executeQuery("SELECT * FROM `customer` " +
                    "INNER JOIN `customer_has_item_has_size` " +
                    "ON `customer_has_item_has_size`.`customer_id` = `customer`.`id`");

            int totalCustomers = 0;
            while (resultSet.next()){
                totalCustomers += 1;
            }

            userAnalytics = new UserAnalytics(totalUsers, totalAdminUsers, totalStaffMembers, totalCustomers, 0, new String[]{});

        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return userAnalytics;
    }
}
