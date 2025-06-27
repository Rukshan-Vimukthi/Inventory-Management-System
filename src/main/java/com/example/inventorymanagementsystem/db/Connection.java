package com.example.inventorymanagementsystem.db;

import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.services.utils.Logger;
import com.example.inventorymanagementsystem.services.utils.Settings;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.state.SettingsData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This is a singleton class. Use to do insert, update, delete, retrieve items from the database. to use methods to do those tasks with the
 * database use Connection.getInstance() to get the instance of the database connection. then call the methods available
 * to perform the operations with the database.
 * <br/><br/>
 * Lis of methods available<br/>
 * {@link #getInstance()} - returns the Connection object instance
 * {@link #getColors()} - get all the colors stored in the database
 * {@link #getItemHasSize(ItemDetail)} - get only the item information stored in the item_has_size table
 */
public class Connection {
    private java.sql.Connection connection;
    private static Connection connectionObject;
    private Statement statement;
    private double price;

    private Connection() throws SQLException{
        String dbLink = "jdbc:mysql://localhost:3306/sandyafashioncorner?useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
//        String password = "root@techlix2002";
        String password = "Sandun@2008.sd";
//        String password = "root@2025sfc";
        connection = DriverManager.getConnection(dbLink, username, password);
    }

    /**
     * create the instance of the Connection object if there is not any instance created.
     * @return Connection instance to perform CRUD operations with the database
     */
    public static Connection getInstance() throws SQLException{
        if (connectionObject == null){
            connectionObject = new Connection();
        }

        System.out.println();
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
                    "SELECT *  FROM `item_has_size` INNER JOIN `item_has_size_has_stock` ON `item_has_size`.`id` = `item_has_size_has_stock`.`item_has_size_id` " +
                            "WHERE `item_id` = %d AND `size_id` = %d".formatted(itemDetail.getId(), itemDetail.getSizeID()));

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                int itemID = resultSet.getInt("item_id");
                int stockID = resultSet.getInt("item_has_size_has_stock.stock_id");
                int sizeID = resultSet.getInt("size_id");
                int orderedQty = resultSet.getInt("ordered_qty");
                int remainingQty = resultSet.getInt("remaining_qty");
                double cost = resultSet.getDouble("cost");
                double sellingPrice = resultSet.getDouble("price");
                itemHasSize = new ItemHasSize(id, itemID, stockID, sizeID, orderedQty, cost, sellingPrice,  remainingQty);
            }

        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
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
            Logger.logError(e.getMessage(), e);
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
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return totalProducts;
    }

    public int getTotalProductValue() {
        int totalProductValue = 0;
        try {
            statement = connection.createStatement();
            ResultSet query = statement.executeQuery("SELECT SUM(price * remaining_qty) AS totalPrice FROM item_has_size");

            if (query.next()) {
                totalProductValue = query.getInt("totalPrice");
            }
            query.close();
            statement.close();

        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
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
            Logger.logError(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return totalRemainingProducts;
    }

    public static int getLowStockItemCount(Connection connection) {
        Settings settings = Settings.getInstance();
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        int count = 0;

        for (ItemHasSize item : allItems) {
            int itemId = item.getItemID();
            Integer target = settings.getItemTarget(itemId);

            if (target == null) {
                target = settings.getLowStockLimit(); // fallback
            }

            if (item.getRemainingQuantity() < target) {
                count++;
            }
        }
        return count;
    }

    public static List<String> getLowStockitemNames(Connection connection) {
        Settings settings = Settings.getInstance();
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        List<String> lowStockNames = new ArrayList<>();

        for (ItemHasSize item : allItems) {
            int itemId = item.getItemID();
            Integer target = settings.getItemTarget(itemId);

            if (target == null) {
                target = settings.getLowStockLimit();
            }

            if (item.getRemainingQuantity() < target) {
                String itemName = connection.getItemNameById(itemId);
                lowStockNames.add(itemName);
            }
        }

        if (lowStockNames.isEmpty()) {
            lowStockNames.add("No low stocks");
        }

        return lowStockNames;
    }

    public static int getOverStockItems(Connection connection) {
        Settings settings = Settings.getInstance();
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        int count = 0;

        for (ItemHasSize item : allItems) {
            int threshold = settings.getOverStockLimit();
            if (item.getRemainingQuantity() > threshold) {
                count++;
            }
        }
        return count;
    }

    public static List<String> getOverStockitemNames(Connection connection) {
        Settings settings = Settings.getInstance();
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        List<String> overStockNames = new ArrayList<>();

        for (ItemHasSize item : allItems) {
            int itemId = item.getItemID();
            int threshold = settings.getOverStockLimit();

            if (item.getRemainingQuantity() > threshold) {
                String itemName = connection.getItemNameById(itemId);
                String itemSize = connection.getItemSizeById(itemId);
                overStockNames.add(itemName + " - " + itemSize);
            }
        }

        if (overStockNames.isEmpty()) {
            overStockNames.add("No over stocks");
        }

        return overStockNames;
    }


    public static int getOutofStokeItems(Connection connection) {
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        int outOfStokesItems = 0;

        for (ItemHasSize item : allItems) {
            if (item.getRemainingQuantity() == 0) {
                outOfStokesItems++;
            }
        }
        return outOfStokesItems;
    }

    public static List<String> getOutOfStockItemNames(Connection connection) {
        List<ItemHasSize> allItems = connection.getAllItemHasSizes();
        List<String> outOfrStockNames = new ArrayList<>();

        for (ItemHasSize item : allItems) {
            if (item.getRemainingQuantity() <= 0) {
                int itemId = item.getItemID();
                String itemName = connection.getItemNameById(itemId);
                String itemSize = connectionObject.getItemSizeById(itemId);
                String nameSizeLabel = itemName + itemSize;
                outOfrStockNames.add(nameSizeLabel);
            }
        }
        if (outOfrStockNames.isEmpty()) {
            outOfrStockNames.add("No out of stocks");
        }
        return outOfrStockNames;
    }

    public Map<String, List<SoldProducts>> getTopAndBottomSellingProducts() {
        Map<String, List<SoldProducts>> soldResult = new HashMap<>();
        List<SoldProducts> topSelling = new ArrayList<>();
        List<SoldProducts> topFiveSelling = new ArrayList<>();
        List<SoldProducts> bottomSelling = new ArrayList<>();
        List<SoldProducts> bottomFiveSelling = new ArrayList<>();

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

            ResultSet rsBottom = stmt.executeQuery(bottomQuery);
            while (rsBottom.next()) {
                bottomSelling.add(new SoldProducts(rsBottom.getInt("item_id"), rsBottom.getInt("total_sold")));
            }

            ResultSet rsTopFive = stmt.executeQuery(topQuery);
            while (rsTopFive.next()) {
                topFiveSelling.add(new SoldProducts(rsTopFive.getInt("item_id"), rsTopFive.getInt("total_sold")));
            }

            ResultSet rsBottomFive = stmt.executeQuery(bottomQuery);
            while (rsBottomFive.next()) {
                bottomFiveSelling.add(new SoldProducts(rsBottomFive.getInt("item_id"), rsBottomFive.getInt("total_sold")));
            }

            soldResult.put("top", topSelling);
            soldResult.put("bottom", bottomSelling);
            soldResult.put("topFive", topFiveSelling);
            soldResult.put("bottomFive", bottomFiveSelling);
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return soldResult;
    };

    // Get the top 10 selling products
    public List<SoldProducts> getTop10SellingProducts() {
        List<SoldProducts> top10 = new ArrayList<>();

        String topTenQuery = "SELECT item_id, SUM(ordered_qty - remaining_qty) AS total_sold " +
                "FROM item_has_size " +
                "GROUP BY item_id " +
                "ORDER BY total_sold DESC " +
                "LIMIT 10";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(topTenQuery)) {

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int totalSold = rs.getInt("total_sold");
                top10.add(new SoldProducts(itemId, totalSold));
            }

        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }

        return top10;
    }


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
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return name;
    }

    public String getItemSizeById(int itemId) {
        String size = "-";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT size FROM size WHERE id = " + itemId);
            if (rs.next()) {
                size = rs.getString("size");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return size;
    }

    /**
     * Get the items in the item_has_size table
     * @return ItemHasSize object that contains all the information for the specified item
     */
    public ArrayList<ItemHasSize> getAllItemHasSizes() {
        ArrayList<ItemHasSize> items = new ArrayList<>();
        String query = "SELECT * FROM item_has_size INNER JOIN `item_has_size_has_stock` ON `item_has_size`.`id` = " +
                "`item_has_size_has_stock`.`item_has_size_id`";
        Set<Integer> seenItemIds = new HashSet<>();

        try  (PreparedStatement stmt = connection.prepareStatement(query);
               ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                items.add(new ItemHasSize(
                        resultSet.getInt("id"),
                        resultSet.getInt("item_id"),
                        resultSet.getInt("item_has_size_has_stock.stock_id"),
                        resultSet.getInt("size_id"),
                        resultSet.getInt("ordered_qty"),
                        resultSet.getDouble("cost"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("remaining_qty")
                ));
            }
        } catch (SQLException exception) {
            Logger.logError(exception.getMessage(), exception);
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
            Data.getInstance().refreshColors();
            return id;
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Data.getInstance().refreshColors();
            return result > 0;
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
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
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(e.getMessage(), e);
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
                        "`item_id`, `size_id`, `ordered_qty`, `cost`, `price`, `remaining_qty`) " +
                                "VALUES(%d, %d, %d, %f, %f, %d)".formatted(
                                        id, sizeID, qty, price, sellingPrice, qty), Statement.RETURN_GENERATED_KEYS);
            generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            id = generatedKeys.getInt(1);

            statement.execute("INSERT INTO `item_has_size_has_stock` (" +
                    "`item_has_size_id`, `stock_id`) VALUES(%d, %d)".formatted(
                            id, stockID), Statement.RETURN_GENERATED_KEYS);

            statement.execute("INSERT INTO `color_has_item_has_size` (" +
                            "`color_id`, `item_has_size_id`) VALUES('%d', '%d')".formatted(
                                    colorID, id
                            ), Statement.RETURN_GENERATED_KEYS);
            Data.getInstance().refreshItemDetails();
            return true;
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }

    public int addSingleItem(String itemName){
        try{
            statement = connection.createStatement();
            statement.execute(
                    "INSERT INTO `item` (`name`) VALUES('%s')".formatted(itemName), Statement.RETURN_GENERATED_KEYS
            );

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            return generatedKeys.getInt(1);
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addNewVariant(int itemID, int stockID, int sizeID, int colorID, int orderedQty, double price, double sellingPrice, String imagePath){
        try{
            statement = connection.createStatement();
            String size = "NULL";
            if (sizeID != 0){
                size = String.valueOf(sizeID);
            }

            String color = "NULL";
            if (colorID != 0){
                color = String.valueOf(colorID);
            }
            statement.execute("INSERT INTO `item_has_size` (" +
                    "`item_id`, `item_stock_id`, `size_id`, `ordered_qty`, `cost`, `price`, `remaining_qty`) " +
                    "VALUES(%d, %d, %s, %d, %f, %f, %d)".formatted(
                            itemID, stockID, size, orderedQty, price, sellingPrice, orderedQty), Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            statement.execute("INSERT INTO `item_has_size_has_stock` (" +
                    "`item_has_size_id`, `stock_id`) VALUES(%d, %d)".formatted(
                            id, stockID), Statement.RETURN_GENERATED_KEYS);
            statement.execute("INSERT INTO `color_has_item_has_size` (" +
                    "`color_id`, `item_has_size_id`, `image_path`) VALUES(%s, '%d', '%s')".formatted(
                            color, id, imagePath
                    ), Statement.RETURN_GENERATED_KEYS);
            Data.getInstance().refreshItemDetails();
            return true;
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update the information of the item selected
     * @return
     */
    public boolean updateItem(int itemID, int itemHasSizeID, int itemHasSizeHasStockID, int colorHasItemHasSizeId, String name, Integer qty, Integer remainingQty, Double price, Double sellingPrice, int stockID, int sizeID, int colorID, String newImagePath){
        try{
            statement = connection.createStatement();
            statement.execute(
                    "UPDATE `item` SET `name` = '%s' WHERE `id` = %d".formatted(
                            name, itemID
                    ));


            statement.execute(
                    "UPDATE `item_has_size` SET `item_id` = %d, `size_id` = %d, `ordered_qty` = %d, `cost` = %f, `price` = %f, `remaining_qty` = %d WHERE `id` = %d ".formatted(
                            itemID, sizeID, qty, price, sellingPrice, remainingQty, itemHasSizeID
                    ));

            if (itemHasSizeHasStockID == 0){
                statement.execute("INSERT INTO `item_has_size_has_stock` (" +
                        "`item_has_size_id`, `stock_id`) VALUES(%d, %d)".formatted(
                                itemHasSizeID, stockID));
            }else {
                statement.execute(("UPDATE `item_has_size_has_stock` " +
                        "SET `item_has_size_id` = %d, `stock_id` = %d WHERE `id` = %d ").formatted(
                        itemHasSizeID, stockID, itemHasSizeHasStockID));
            }

            if (colorHasItemHasSizeId == 0) {
                statement.execute("INSERT INTO `color_has_item_has_size` (`color_id`, `item_has_size_id`) " +
                        "VALUES('%d', '%d')");
            }else{
                statement.execute("UPDATE `color_has_item_has_size` SET " +
                        "`color_id` = %d, `image_path` = '%s' WHERE `id` = %d".formatted(
                                colorID, newImagePath, colorHasItemHasSizeId
                        ));
            }
            Data.getInstance().refreshItemDetails();
            return true;
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
            sqlException.printStackTrace();
        }
        return false;
    }

    public boolean deleteItem(int itemHasSizeHasStockID, int colorHasItemHasSizeID, int itemHasSizeID, int itemID){
        try{
            statement = connection.createStatement();
            statement.execute("DELETE FROM `item_has_size_has_stock` WHERE `id` = %d".formatted(itemHasSizeHasStockID));
            statement.execute("DELETE FROM `color_has_item_has_size` WHERE `id` = %d".formatted(colorHasItemHasSizeID));
            statement.execute("DELETE FROM `item_has_size` WHERE `id` = %d".formatted(itemHasSizeID));
            statement.execute("DELETE FROM `item` WHERE `id` = %d".formatted(itemID));
            return true;
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public Sale getSale(String date, int customerID){
        Sale sale = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `sale` WHERE `date` = '%s' AND `customer_id` = %d".formatted(date, customerID));
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String saleDate = resultSet.getString("date");
                double receivedAmount = resultSet.getDouble("received_amount");
                double totalCost = resultSet.getDouble("total_cost");
                int remainsStatusID = resultSet.getInt("remains_statuss_id");
                sale = new Sale(id, saleDate, receivedAmount, totalCost, remainsStatusID);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return sale;
    }

    public List<Sale> filterSales(String date, int customerId){
        List<Sale> sales = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `sale` WHERE `date` = '%s' AND `customer_id` = %d".formatted(date, customerId));
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String saleDate = resultSet.getString("date");
                double receivedAmount = resultSet.getDouble("received_amount");
                double totalCost = resultSet.getDouble("total_cost");
                int remainsStatusID = resultSet.getInt("remains_statuss_id");
                Sale sale = new Sale(id, saleDate, receivedAmount, totalCost, remainsStatusID);
                sales.add(sale);
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return sales;
    }

    public void payFromPoints(double amount, Customer customer){
        try{
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `customer` SET `points` = `points` - %f WHERE `id` = %d".formatted(amount, customer.getId()));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void payFromRefundAmount(double amount, Customer customer){
        try{
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `customer` SET `refund_amount` = `refund_amount` - %f WHERE `id` = %d".formatted(amount, customer.getId()));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean insertCustomerItem(
            int customer_id,
            int itemHasSizeId,
            int amount,
            double price,
            String date,
            int itemStatusId,
            double discount,
            double priceWithDiscount,
            double receivedAmount,
            int remainsStatusID) {
        try {
            boolean doesSaleExist = false;
            Sale sale = null;
            for (Sale sale_ : filterSales(date, customer_id)){
                if (sale_.getRemainsStatusID() == remainsStatusID && sale_.getDate().equals(date)){
                    doesSaleExist = true;
                    sale = sale_;
                    break;
                }
            }

            int saleID = 0;
            if (!doesSaleExist){
                statement = connection.createStatement();
                statement.execute("INSERT INTO `sale` (`date`,`received_amount`, `total_cost`, `remains_statuss_id`, `customer_id`) " +
                        "VALUES('%s', %f, %f, %d, %d)".formatted(date, receivedAmount, priceWithDiscount, remainsStatusID, customer_id), Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                saleID = resultSet.getInt(1);
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO customer_has_item_has_size (" +
                                "customer_id, " +
                                "item_has_size_id, " +
                                "amount, " +
                                "price, " +
                                "item_status_id, " +
                                "discount, " +
                                "price_with_discount, sale_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                );
                ps.setInt(1, customer_id);
                ps.setInt(2, itemHasSizeId);
                ps.setInt(3, amount);
                ps.setDouble(4, price);
                ps.setInt(5, itemStatusId);
                ps.setDouble(6, discount);
                ps.setDouble(7, priceWithDiscount);
                ps.setDouble(8, saleID);
                int rowsInserted = ps.executeUpdate();
                return rowsInserted > 0;
            }else{
                saleID = sale.getId();
                statement = connection.createStatement();

                double newReceivedAmount = sale.getReceivedMoney() + receivedAmount;
                double newTotalCost = sale.getTotalCost() + priceWithDiscount;

                int rowsAffected = 0;
                rowsAffected = statement.executeUpdate("UPDATE `sale` SET `received_amount` = %f, `total_cost` = %f WHERE `customer_id` = %d AND `date` = '%s' AND id = %d".formatted(newReceivedAmount, newTotalCost, customer_id, date, saleID));
                if (rowsAffected > 0) {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO customer_has_item_has_size (" +
                                    "customer_id, " +
                                    "item_has_size_id, " +
                                    "amount, " +
                                    "price, " +
                                    "item_status_id, " +
                                    "discount, " +
                                    "price_with_discount, sale_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    );
                    ps.setInt(1, customer_id);
                    ps.setInt(2, itemHasSizeId);
                    ps.setInt(3, amount);
                    ps.setDouble(4, price);
                    ps.setInt(5, itemStatusId);
                    ps.setDouble(6, discount);
                    ps.setDouble(7, priceWithDiscount);
                    ps.setDouble(8, saleID);
                    int rowsInserted = ps.executeUpdate();
                    return rowsInserted > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertCheckoutItem(
            Customer customer,
            List<CheckoutItem> checkoutItems,
            double totalCost,
            double receivedAmount,
            int remainsStatusID,
            double discountForAll,
            boolean payFromPoints,
            boolean payFromRefunds,
            boolean saveRemaindersForPoints) {
        try {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            boolean doesSaleExist = false;
            Sale sale = null;
            for (Sale sale_ : filterSales(date, customer.getId())){
                if (sale_.getRemainsStatusID() == remainsStatusID && sale_.getDate().equals(date)){
                    doesSaleExist = true;
                    sale = sale_;
                    break;
                }
            }

            double pointAvailable = customer.getPoints();
            double refundsAvailable = customer.getRefundAmount();

            double updatedPoints = 0.0D;
            double updatedRefunds = 0.0D;
            double remaining = 0.0D;

            statement = connection.createStatement();

            boolean fullPaymentMade = false;
            double totalAmountPaid = 0.0D;
            if (payFromPoints){
                if (pointAvailable >= totalCost){
                    updatedPoints = pointAvailable - totalCost;
                    fullPaymentMade = true;
                }else{
                    totalAmountPaid += pointAvailable;
                    remaining = totalCost - pointAvailable;
                }
            }

            if (!fullPaymentMade) {
                if (payFromRefunds) {
                    if (remaining != 0) {
                        if (remaining <= refundsAvailable) {
                            updatedRefunds = refundsAvailable - remaining;
                            fullPaymentMade = true;
                        } else {
                            totalAmountPaid += refundsAvailable;
                            remaining -= refundsAvailable;
                        }
                    }else{
                        if (refundsAvailable >= totalCost){
                            updatedRefunds = refundsAvailable - totalCost;
                            fullPaymentMade = true;
                        }else{
                            totalAmountPaid += refundsAvailable;
                            remaining = totalCost - refundsAvailable;
                        }
                    }
                }
            }

            System.out.println("Updated Points: " + updatedPoints);
            System.out.println("Total amount paid: " + totalAmountPaid);
            System.out.println("Updated refunds:" + updatedRefunds);

            if (!payFromPoints && !payFromRefunds){
                if (saveRemaindersForPoints){
                    totalAmountPaid += totalCost;
                    fullPaymentMade = true;
                    updatedPoints = receivedAmount - totalCost;
                    statement.execute("UPDATE customer SET `points` = `points` + %f WHERE id = %d".formatted(updatedPoints, customer.getId()));
                }
            }

            if(payFromPoints){
                if (saveRemaindersForPoints){
                    if (receivedAmount > totalCost){
                        statement.execute("UPDATE customer SET `points` = %f WHERE id = %d".formatted(receivedAmount - totalCost, customer.getId()));
                    }
                }else {
                    statement.execute("UPDATE customer SET `points` = %f WHERE id = %d".formatted(updatedPoints, customer.getId()));
                }
            }

            if(payFromRefunds) {
                if (saveRemaindersForPoints && !payFromPoints){
                    if (receivedAmount > totalCost){
                        statement.execute("UPDATE customer SET `points` = `points` + %f WHERE id = %d".formatted(receivedAmount - totalCost, customer.getId()));
                    }
                    statement.execute("UPDATE customer SET `refund_amount` = %f WHERE id = %d".formatted(updatedRefunds, customer.getId()));
                }else {
                    statement.execute("UPDATE customer SET `refund_amount` = %f WHERE id = %d".formatted(updatedRefunds, customer.getId()));
                }
            }

            if (payFromPoints && payFromRefunds){
                if ((pointAvailable + refundsAvailable) >= totalCost || receivedAmount == totalCost || (receivedAmount > totalCost && saveRemaindersForPoints)){
                    fullPaymentMade = true;
                }
            }


            int saleID = 0;
            if (!doesSaleExist){
                if (fullPaymentMade){
                    statement.execute("INSERT INTO `sale` (`date`,`received_amount`, `total_cost`, `remains_statuss_id`, `customer_id`) " +
                            "VALUES('%s', %f, %f, %d, %d)".formatted(date, totalCost, totalCost, remainsStatusID, customer.getId()), Statement.RETURN_GENERATED_KEYS);
                }else{
                    statement.execute("INSERT INTO `sale` (`date`,`received_amount`, `total_cost`, `remains_statuss_id`, `customer_id`) " +
                            "VALUES('%s', %f, %f, %d, %d)".formatted(date, receivedAmount, totalCost, remainsStatusID, customer.getId()), Statement.RETURN_GENERATED_KEYS);
                }
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                saleID = resultSet.getInt(1);
                System.out.println("Sale created!");
            }else{
                saleID = sale.getId();
                if(fullPaymentMade) {
                    int rowsAffected = statement.executeUpdate("UPDATE `sale` SET `received_amount` = %f, `total_cost` = %f WHERE `customer_id` = %d AND `date` = '%s' AND id = %d".formatted(sale.getReceivedMoney() + totalCost, sale.getTotalCost() + totalCost, customer.getId(), date, saleID));
                }else {
                    int rowsAffected = statement.executeUpdate("UPDATE `sale` SET `received_amount` = %f, `total_cost` = %f WHERE `customer_id` = %d AND `date` = '%s' AND id = %d".formatted(sale.getReceivedMoney() + receivedAmount, sale.getTotalCost() + totalCost, customer.getId(), date, saleID));
                }
            }

            for (CheckoutItem checkoutItem : checkoutItems){
                double priceWithDiscount = checkoutItem.getCostWithDiscount();
                double unitPrice = checkoutItem.getPrice();
                int amount = checkoutItem.getAmount();
                int itemHasSizeID = checkoutItem.getitemHasSizeId();
                int customerID = customer.getId();
                int itemStatusID = 1;
                double discount = checkoutItem.getDiscount();

                if (discount == 0.0D && discountForAll != 0.0D){
                    discount = discountForAll;
                }

                statement = connection.createStatement();
                statement.execute("INSERT INTO `customer_has_item_has_size` (" +
                        "customer_id, " +
                        "item_has_size_id, " +
                        "amount, " +
                        "price, " +
                        "item_status_id, " +
                        "discount, " +
                        "price_with_discount, sale_id) VALUES" +
                        "(%d, %d, %d, %f, %d, %f, %f, %d)".formatted(
                                customerID,
                                itemHasSizeID,
                                amount,
                                unitPrice,
                                itemStatusID,
                                discount,
                                priceWithDiscount,
                                saleID)
                );

            }
            return true;
//            int saleID = 0;
//            if (!doesSaleExist){
//                statement = connection.createStatement();
//                statement.execute("INSERT INTO `sale` (`date`,`received_amount`, `total_cost`, `remains_statuss_id`, `customer_id`) " +
//                        "VALUES('%s', %f, %f, %d, %d)".formatted(date, receivedAmount, priceWithDiscount, remainsStatusID, customer_id), Statement.RETURN_GENERATED_KEYS);
//                ResultSet resultSet = statement.getGeneratedKeys();
//                resultSet.next();
//                saleID = resultSet.getInt(1);
//                PreparedStatement ps = connection.prepareStatement(
//                        "INSERT INTO customer_has_item_has_size (" +
//                                "customer_id, " +
//                                "item_has_size_id, " +
//                                "amount, " +
//                                "price, " +
//                                "item_status_id, " +
//                                "discount, " +
//                                "price_with_discount, sale_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
//                );
//                ps.setInt(1, customer_id);
//                ps.setInt(2, itemHasSizeId);
//                ps.setInt(3, amount);
//                ps.setDouble(4, price);
//                ps.setInt(5, itemStatusId);
//                ps.setDouble(6, discount);
//                ps.setDouble(7, priceWithDiscount);
//                ps.setDouble(8, saleID);
//                int rowsInserted = ps.executeUpdate();
//                return rowsInserted > 0;
//            }else{
//                saleID = sale.getId();
//                statement = connection.createStatement();
//
//                double newReceivedAmount = sale.getReceivedMoney() + receivedAmount;
//                double newTotalCost = sale.getTotalCost() + priceWithDiscount;
//
//                int rowsAffected = 0;
//                rowsAffected = statement.executeUpdate("UPDATE `sale` SET `received_amount` = %f, `total_cost` = %f WHERE `customer_id` = %d AND `date` = '%s' AND id = %d".formatted(newReceivedAmount, newTotalCost, customer_id, date, saleID));
//                if (rowsAffected > 0) {
//                    PreparedStatement ps = connection.prepareStatement(
//                            "INSERT INTO customer_has_item_has_size (" +
//                                    "customer_id, " +
//                                    "item_has_size_id, " +
//                                    "amount, " +
//                                    "price, " +
//                                    "item_status_id, " +
//                                    "discount, " +
//                                    "price_with_discount, sale_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
//                    );
//                    ps.setInt(1, customer_id);
//                    ps.setInt(2, itemHasSizeId);
//                    ps.setInt(3, amount);
//                    ps.setDouble(4, price);
//                    ps.setInt(5, itemStatusId);
//                    ps.setDouble(6, discount);
//                    ps.setDouble(7, priceWithDiscount);
//                    ps.setDouble(8, saleID);
//                    int rowsInserted = ps.executeUpdate();
//                    return rowsInserted > 0;
//                }
//                return false;
//            }
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }


    public ItemDetail getItemDetail(int itemId){
        ItemDetail itemDetail = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `item` " +
                    "LEFT JOIN `item_has_size` " +
                    "ON `item`.`id` = `item_has_size`.`item_id`" +
                    "INNER JOIN `item_has_size_has_stock` ON `item_has_size`.`id` = `item_has_size_has_stock`.`item_has_size_id` " +
                    "LEFT JOIN `stock` ON `item_has_size_has_stock`.`stock_id` = `stock`.`id` " +
                    "LEFT JOIN `size` ON `size`.`id` = `item_has_size`.`size_id` " +
                    "LEFT JOIN `color_has_item_has_size` " +
                    "ON `item_has_size`.`id` = `color_has_item_has_size`.`item_has_size_id` " +
                    "LEFT JOIN `color` ON `color_has_item_has_size`.`color_id` = `color`.`id` WHERE `color_has_item_has_size`.`id` = %d".formatted(itemId));

            while (resultSet.next()){
                int itemID = resultSet.getInt("item.id");
                String name = resultSet.getString("item.name");
                Double price = resultSet.getDouble("item_has_size.cost");
                Double sellingPrice = resultSet.getDouble("item_has_size.price");
                int orderedQty = resultSet.getInt("item_has_size.ordered_qty");
                int remainingQty = resultSet.getInt("item_has_size.remaining_qty");
                int itemHasSizeHasStockID = resultSet.getInt("item_has_size_has_stock.id");
                int stockID = resultSet.getInt("stock.id");
                String stockDate = resultSet.getString("stock.date");
                String stockName = resultSet.getString("stock.name");
                int itemHasSizeID = resultSet.getInt("item_has_size.id");
                int itemSizeID = resultSet.getInt("size.id");
                String itemSize = resultSet.getString("size.size");
                int itemColorID = resultSet.getInt("color.id");
                String itemColor = resultSet.getString("color.color");
                int colorHasItemHasSizeID = resultSet.getInt("color_has_item_has_size.id");
                String pathToImage = resultSet.getString("color_has_item_has_size.image_path");

                itemDetail = new ItemDetail(
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
                        itemHasSizeHasStockID,
                        orderedQty,
                        remainingQty,
                        pathToImage
                );
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return itemDetail;
    }

    public boolean handleItemReturn (int itemHasSizeId, int customerId, Label messageLabel) {
        try {
            String updateQuery = "UPDATE customer_has_item_has_size SET  item_status_id = 2 WHERE item_has_size_id = ? AND customer_id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, itemHasSizeId);
            updateStmt.setInt(2, customerId);
            updateStmt.executeUpdate();

            String updateStockQuery = "UPDATE item_has_size SET remaining_qty = remaining_qty + 1 WHERE id = ?";
            PreparedStatement stockStmt = connection.prepareStatement(updateStockQuery);
            stockStmt.setInt(1, itemHasSizeId);
            stockStmt.executeUpdate();

            return true;

        } catch (SQLException  e) {
            messageLabel.setText("❌ DB error: " + e.getMessage());
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    public ItemHasSize getItemHasSizeByID(int itemHasSizeID){
        ItemHasSize itemHasSize = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `item_has_size`  WHERE id = %d".formatted(itemHasSizeID));
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                int itemID = resultSet.getInt("item_id");
                int stockID = resultSet.getInt("item_has_size_has_stock.stock_id");
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

    public List<CustomerSale> getCustomerSales(String date, int customerID){
        List<CustomerSale> customerSales = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet;

            if (date == null && customerID == 0) {
                resultSet = statement.executeQuery("SELECT * FROM sale INNER JOIN customer_has_item_has_size ON customer_has_item_has_size.sale_id = sale.id INNER JOIN `color_has_item_has_size` ON `color_has_item_has_size`.`item_has_size_id` = `customer_has_item_has_size`.`item_has_size_id`");
            }else if(date != null && customerID == 0){
                resultSet = statement.executeQuery("SELECT * FROM sale INNER JOIN customer_has_item_has_size ON customer_has_item_has_size.sale_id = sale.id INNER JOIN `color_has_item_has_size` ON `color_has_item_has_size`.`item_has_size_id` = `customer_has_item_has_size`.`item_has_size_id` WHERE `date` = '%s'".formatted(date));
            }else if(date == null && customerID != 0){
                resultSet = statement.executeQuery("SELECT * FROM sale INNER JOIN customer_has_item_has_size ON customer_has_item_has_size.sale_id = sale.id INNER JOIN `color_has_item_has_size` ON `color_has_item_has_size`.`item_has_size_id` = `customer_has_item_has_size`.`item_has_size_id` WHERE `sale`.`customer_id` = %d".formatted(customerID));
            }else {
                resultSet = statement.executeQuery("SELECT * FROM sale INNER JOIN customer_has_item_has_size ON customer_has_item_has_size.sale_id = sale.id INNER JOIN `color_has_item_has_size` ON `color_has_item_has_size`.`item_has_size_id` = `customer_has_item_has_size`.`item_has_size_id` WHERE `date` = '%s' AND `sale`.`customer_id` = %d".formatted(date, customerID));
            }

            HashMap<String, HashMap<Integer, CustomerSale>> itemsOverview = new HashMap<>();

            while (resultSet.next()){
                int saleID = resultSet.getInt("sale.id");
                String saleDate = resultSet.getString("sale.date");
                double receivedMoney = resultSet.getDouble("sale.received_amount");
                double cost = resultSet.getDouble("sale.total_cost");
                int colorHasItemHasSizeID = resultSet.getInt("color_has_item_has_size.id");

                int customerHasItemHasSizeID = resultSet.getInt("customer_has_item_has_size.id");
                int boughtAmount = resultSet.getInt("customer_has_item_has_size.amount");
                double discount = resultSet.getDouble("customer_has_item_has_size.discount");
                double unitPrice = resultSet.getDouble("customer_has_item_has_size.price");
                double costWithDiscount = resultSet.getDouble("customer_has_item_has_size.price_with_discount");
                double refundAmount = resultSet.getDouble("customer_has_item_has_size.refund_amount");
                ItemDetail itemDetail = getItemDetail(colorHasItemHasSizeID);

                System.out.println("Refund amountX: " + refundAmount);

                double totalCost = boughtAmount * unitPrice;

                CheckoutItem checkoutItem = new CheckoutItem(
                        customerHasItemHasSizeID,
                        itemDetail.getName(),
                        itemDetail.getSize(),
                        itemDetail.getItemColor(),
                        boughtAmount,
                        itemDetail.getPrice(),
                        itemDetail.getSellingPrice(),
                        discount,
                        String.valueOf(totalCost),
                        costWithDiscount,
                        refundAmount
                );

                int customerId = resultSet.getInt("customer_has_item_has_size.customer_id");
                int amount = resultSet.getInt("customer_has_item_has_size.amount");
                Customer customer = getCustomer(customerId);
                CustomerSale customerSale = new CustomerSale(saleID, receivedMoney, cost, amount, saleDate, customer, itemDetail, checkoutItem);
                customerSales.add(customerSale);
            }

        }catch(Exception e){
            e.printStackTrace();
            Logger.logError(e.getMessage(), e);
        }
        System.out.println("Customer sale list length: " + customerSales.size());
        return customerSales;
    }

    public boolean handleItemReturn (int itemHasSizeId, int customerId, String date) {
        try {
            String updateQuery = "UPDATE customer_has_item_has_size SET  item_status_id = 2 WHERE item_has_size_id = ? AND customer_id = ? AND `date`";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, itemHasSizeId);
            updateStmt.setInt(2, customerId);
            updateStmt.executeUpdate();

            String updateStockQuery = "UPDATE item_has_size SET remaining_qty = remaining_qty + 1 WHERE id = ?";
            PreparedStatement stockStmt = connection.prepareStatement(updateStockQuery);
            stockStmt.setInt(1, itemHasSizeId);
            stockStmt.executeUpdate();
            return true;

        } catch (SQLException  e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    public ItemDetail getItemDetailByID(int itemId) {
        ItemDetail itemDetail = null;

        String query = """
        SELECT i.id AS item_id, i.name, ihs.cost, ihs.price, s.size, s.id AS size_id,
               chis.id AS chis_id, c.id AS color_id, c.color, c.hex_color
        FROM item i
        JOIN item_has_size ihs ON i.id = ihs.item_id
        JOIN size s ON ihs.size_id = s.id
        JOIN color_has_item_has_size chis ON ihs.id = chis.item_has_size_id
        JOIN color c ON chis.color_id = c.id
        WHERE i.id = ? LIMIT 1
    """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int itemID = rs.getInt("item_id");
                String name = rs.getString("name");
                double price = rs.getDouble("cost");
                double sellingPrice = rs.getDouble("price");
                int stockID = 0;
                String stockDate = "";
                String stockName = "";
                int itemSizeID = rs.getInt("size_id");
                String itemSize = rs.getString("size");
                int itemColorID = rs.getInt("color_id");
                String itemColor = rs.getString("color");
                int itemHasSizeID = 0;
                int colorHasItemHasSizeID = rs.getInt("chis_id");
                int itemHasSizeHasStockID = 0;
                int orderedQty = 0;
                int remainingQty = 0;
                String pathToImage = rs.getString("hex_color"); // using hex color here for convenience

                itemDetail = new ItemDetail(
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
                        itemHasSizeHasStockID,
                        orderedQty,
                        remainingQty,
                        pathToImage
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemDetail;
    }


    public void returnItem(int itemID, String date, CustomerSale customersale, int returnedQty){
        try{
            statement = connection.createStatement();
            if (customersale != null){
                System.out.println("Updating database");
                System.out.println("Amount bought: " + customersale.getCheckoutItem().getAmount());
                System.out.println("Returned Qty: " + returnedQty);
                System.out.println("Checkout Item ID: " + customersale.getCheckoutItem().getId());

                int checkoutItemId = customersale.getCheckoutItem().getId();
                int saleId = customersale.getSaleID();
                int itemHasSizeId = customersale.getItemDetails().getItemHasSizeID();

                System.out.println("Price: " + customersale.getCheckoutItem().getSellingPrice());
                System.out.println("Discount: " + customersale.getCheckoutItem().getDiscount());

                double amountToReduce = 0.0D;
                double unitPrice = customersale.getCheckoutItem().getSellingPrice();
                double discount = customersale.getCheckoutItem().getDiscount();
                int newBoughtQty = customersale.getCheckoutItem().getAmount() - returnedQty;

                System.out.println("Unit Price: " + unitPrice);
                System.out.println("Discount: " + discount);

                double newCostWithDiscount = (newBoughtQty * unitPrice);
                if (discount != 0.0D){
                    newCostWithDiscount *= (discount / 100);
                }
                double refundAmount = customersale.getCheckoutItem().getCostWithDiscount() - newCostWithDiscount;
                System.out.println("Refund Amount: " + refundAmount);
                System.out.println(customersale.getCheckoutItem().getRefundAmount());
                refundAmount += customersale.getCheckoutItem().getRefundAmount();

                System.out.println("New bought qty: " + newBoughtQty);
                System.out.println("New Cost With Discount: " + newCostWithDiscount);
                System.out.println("Refund Amount: " + refundAmount);

                int rowsAffected = statement.executeUpdate("UPDATE `customer_has_item_has_size` SET `item_status_id` = %d, `amount` = %d, `price_with_discount` = %f, `refund_amount` = %f WHERE id = %d".formatted(2, newBoughtQty, newCostWithDiscount, refundAmount, checkoutItemId));
                System.out.println("Rows affected for changing customer_has_item_has_size table: " + rowsAffected);

                rowsAffected = statement.executeUpdate("UPDATE customer SET refund_amount = %f WHERE id = %d".formatted(refundAmount, customersale.getCustomer().getId()));
                System.out.println("Rows affected for changing customer table: " + rowsAffected);

                rowsAffected = statement.executeUpdate("UPDATE `item_has_size` SET `remaining_qty` = `remaining_qty` + %d WHERE id = %d".formatted(returnedQty, itemHasSizeId));
                System.out.println("Rows affected for changing item_has_size table: " + rowsAffected);

                double newReceivedAmount = 0.0D;
                if (refundAmount > customersale.getReceivedAmount()){
                    newReceivedAmount = 0;
                }else{
                    newReceivedAmount = customersale.getReceivedAmount() - refundAmount;
                }

                System.out.println("New received amount: " + newReceivedAmount);

                rowsAffected = statement.executeUpdate("UPDATE `sale` SET `total_cost` = %f, `received_amount` = %f WHERE `id` = %d".formatted(newCostWithDiscount, newReceivedAmount, saleId));
                System.out.println("Rows affected for changing sale table: " + rowsAffected);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets all the details related to each item in the database.
     * @return List of type ItemDetail which holds all the information related to each item
     */
    public List<ItemDetail> getItemDetails(){
        ArrayList<ItemDetail> itemDetails = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `item` " +
                    "INNER JOIN `item_has_size` " +
                    "ON `item`.`id` = `item_has_size`.`item_id`" +
                    "INNER JOIN `item_has_size_has_stock` ON `item_has_size`.`id` = `item_has_size_has_stock`.`item_has_size_id` " +
                    "LEFT JOIN `stock` ON `item_has_size_has_stock`.`stock_id` = `stock`.`id` " +
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
                int itemHasSizeHasStockID = resultSet.getInt("item_has_size_has_stock.id");
                int stockID = resultSet.getInt("stock.id");
                String stockDate = resultSet.getString("stock.date");
                String stockName = resultSet.getString("stock.name");
                int itemHasSizeID = resultSet.getInt("item_has_size.id");
                int itemSizeID = resultSet.getInt("size.id");
                String itemSize = resultSet.getString("size.size");
                int itemColorID = resultSet.getInt("color.id");
                String itemColor = resultSet.getString("color.color");
                int colorHasItemHasSizeID = resultSet.getInt("color_has_item_has_size.id");
                String pathToImage = resultSet.getString("color_has_item_has_size.image_path");

                System.out.println("Item Has Size ID: " + itemHasSizeID);

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
                        itemHasSizeHasStockID,
                        orderedQty,
                        remainingQty,
                        pathToImage
                );


                itemDetails.add(itemDetail);
            }

        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return itemDetails;
    }

    // For setting the dates as filters for showing the sales
    public ResultSet getFilteredSalesData (String filter) throws SQLException {
        String query =  "SELECT sale.date, chs.item_has_size_id, chs.price, chs.amount FROM customer_has_item_has_size chs INNER JOIN sale ON chs.sale_id = sale.id WHERE ";

        if (filter == null) {
            filter = "";
        }

        switch (filter) {
            case "Today":
                query += "DATE(sale.date) = CURDATE()";
                break;
            case "Yesterday":
                query += "DATE(sale.date) = CURDATE() - INTERVAL 1 DAY";
                break;
            case "Last 7 Days":
                query += "sale.date >= CURDATE() - INTERVAL 7 DAY";
                break;
            case "This Month":
                query += "MONTH(sale.date) = MONTH(CURDATE()) AND YEAR(sale.date) = YEAR(CURDATE())";
                break;
            case "Last Month":
                query += "MONTH(sale.date) = MONTH(CURDATE() - INTERVAL 1 MONTH) AND YEAR(sale.date) = YEAR(CURDATE() - INTERVAL 1 MONTH)";
                break;
            case "This Year":
                query += "YEAR(sale.date) = YEAR(CURDATE())";
                break;
            case "Last Year":
                query += "YEAR(sale.date) = YEAR(CURDATE() - INTERVAL 1 YEAR)";
                break;
            default:
                query = "SELECT sale.date, chs.item_has_size_id, chs.price, chs.amount FROM customer_has_item_has_size chs INNER JOIN sale ON chs.sale_id = sale.id";
                break;
        }
        PreparedStatement stmt = connection.prepareStatement(query);
        return stmt.executeQuery();
    }

    public void filterItems(String color, String size, Stock stock, double price, String name, double sellingPrice) throws SQLException {
        List<ItemDetail> itemDetails = new ArrayList<>();
        try{
            statement = connection.createStatement();
            String stockDate = null;
            String stockName = null;

            if(stock != null){
                stockDate = stock.getDate();
                stockName = stock.getName();
            }

            String query = "SELECT * FROM `item` " +
                    "INNER JOIN `item_has_size` " +
                    "ON `item`.`id` = `item_has_size`.`item_id`" +
                    "INNER JOIN `item_has_size_has_stock` ON `item_has_size`.`id` = `item_has_size_has_stock`.`item_has_size_id` " +
                    "INNER JOIN `stock` ON `item_has_size_has_stock`.`stock_id` = `stock`.`id` " +
                    "INNER JOIN `size` ON `size`.`id` = `item_has_size`.`size_id` " +
                    "INNER JOIN `color_has_item_has_size` " +
                    "ON `item_has_size`.`id` = `color_has_item_has_size`.`item_has_size_id` " +
                    "INNER JOIN `color` ON `color_has_item_has_size`.`color_id` = `color`.`id` WHERE ";

            boolean addedFirstCondition = false;
            if (color != null){
                System.out.println("Color: " + color.split("0x")[1]);
                if(!addedFirstCondition){
                    query += "`color`.`color` = '#" + color.split("0x")[1].substring(0, 6).toUpperCase() + "' ";
                    addedFirstCondition = true;
                }else{
                    query += " OR `color`.`color` = '#" + color.split("0x")[1].substring(0, 6).toUpperCase() + "'";
                }
            }

            if(size != null){
                if(!addedFirstCondition){
                    addedFirstCondition = true;
                    query += " `size`.`size` = '" + size + "' ";
                }else {
                    query += " OR `size`.`size` = '" + size + "' ";
                }
            }

            if(name != null){
                if(!addedFirstCondition){
                    addedFirstCondition = true;
                    query += "`item`.`name` = '" + name + "' ";
                }else {
                    query += " OR `item`.`name` = '" + name + "' ";
                }
            }

            if(stockDate != null){
                if(!addedFirstCondition){
                    addedFirstCondition = true;
                    query += "`stock`.`date` = '" + stockDate + "' ";
                }else {
                    query += " OR `stock`.`date` = '" + stockDate + "' ";
                }
            }

            if(stockName != null){
                if(!addedFirstCondition){
                    addedFirstCondition = true;
                    query += "`stock`.`name` = '" + stockName + "' ";
                }else {
                    query += " OR `stock`.`name` = '" + stockName + "' ";
                }
            }

            if(price != 0.0D){
                if(!addedFirstCondition){
                    addedFirstCondition = true;
                    query += "`item_has_size`.`cost` LIKE " + price;
                }else {
                    query += " OR `item_has_size`.`cost` LIKE " + price;
                }
            }

            if(sellingPrice != 0.0D){
                if(!addedFirstCondition){
                    addedFirstCondition = true;
                    query += "`item_has_size`.`price` LIKE " + sellingPrice;
                }else {
                    query += " OR `item_has_size`.`price` LIKE " + sellingPrice;
                }
            }

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                System.out.println(resultSet.getString("item.name"));
                int itemID = resultSet.getInt("item.id");
                String itemName = resultSet.getString("item.name");
                Double itemPrice = resultSet.getDouble("item_has_size.cost");
                Double itemSellingPrice = resultSet.getDouble("item_has_size.price");
                int orderedQty = resultSet.getInt("item_has_size.ordered_qty");
                int remainingQty = resultSet.getInt("item_has_size.remaining_qty");
                int itemHasSizeHasStockID = resultSet.getInt("item_has_size_has_stock.id");
                int stockID = resultSet.getInt("stock.id");
                String itemStockDate = resultSet.getString("stock.date");
                String itemStockName = resultSet.getString("stock.name");
                int itemHasSizeID = resultSet.getInt("item_has_size.id");
                int itemSizeID = resultSet.getInt("size.id");
                String itemSize = resultSet.getString("size.size");
                int itemColorID = resultSet.getInt("color.id");
                String itemColor = resultSet.getString("color.color");
                int colorHasItemHasSizeID = resultSet.getInt("color_has_item_has_size.id");
                String pathToImage = resultSet.getString("color_has_item_has_size.image_path");

                ItemDetail itemDetail = new ItemDetail(
                        itemID,
                        itemName,
                        itemPrice,
                        itemSellingPrice,
                        stockID,
                        itemStockDate,
                        itemStockName,
                        itemSizeID,
                        itemSize,
                        itemColorID,
                        itemColor,
                        itemHasSizeID,
                        colorHasItemHasSizeID,
                        itemHasSizeHasStockID,
                        orderedQty,

                        remainingQty,
                        pathToImage
                );

                System.out.println(itemName);

                itemDetails.add(itemDetail);
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        Data.getInstance().setItemDetails(itemDetails);
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
            Logger.logError(e.getMessage(), e);
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
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(sqlException.getMessage(), sqlException);
            sqlException.printStackTrace();
        }
        return sizes;
    }

    public boolean updateSize(int id, String newSize){
        try{
            statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE `size` SET `size` = '%s' WHERE `id` = %d".formatted(newSize, id));
            Data.getInstance().refreshSize();
            return result > 0;
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
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
            Logger.logError(e.getMessage(), e);
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
    public int addNewUser(String firstName, String lastName, String userName, String email, String password, String registeredDate, int roleID, String pathToImage, String phoneNumber){
        String imagePath = pathToImage;
        if (pathToImage == null){
            imagePath = " ";
        }
        try{
            statement = connection.createStatement();
            boolean isUserAdded = statement.execute("INSERT INTO `user` " +
                    "(`firstName`, `lastName`, `username`, `email`, `password`, `registered_date`, `role_id`, `image_path`, `phone`) " +
                    "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%d', '%s', '%s')".formatted(firstName, lastName, userName, email, password, registeredDate, roleID, imagePath, phoneNumber));
            Data.getInstance().refreshUsers();
            return 1;
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * Delete user
     * @param userID - ID of the user in the database
     * @return int - 1 if the process completes without an error. -1 if there is any error occurred
     */
    public int deleteUser(int userID){
        try{
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM `user` WHERE `id` = %d".formatted(userID));
            Data.getInstance().refreshUsers();
            return 1;
        }catch (SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * Get and returns all the users from the database
     */
    public List<User> getUsers(){
        List<User> users = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `user` INNER JOIN `role` ON `user`.`role_id` = `role`.`id`");
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String userName = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String registeredDate = resultSet.getString("registered_date");
                String role = resultSet.getString("role.role");
                String imagePath = resultSet.getString("image_path");
                String phone = resultSet.getString("phone");

                User user = new User(id, firstName, lastName, userName, email, password, registeredDate, role, imagePath, phone);
                users.add(user);
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return users;
    }

    public void filterUsers(String filterValue) throws SQLException {
        List<User> users = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `user` INNER JOIN `role` ON `user`.`role_id` = `role`.`id` WHERE " +
                    "firstname LIKE '%" + filterValue + "%' OR " +
                    "lastname LIKE '%" + filterValue + "%' OR " +
                    "username LIKE '%" + filterValue + "%' OR " +
                    "email LIKE '%" + filterValue + "%' OR " +
                    "registered_date LIKE '" + filterValue + "' OR " +
                    "role.role LIKE '%" + filterValue + "%'"
                    );
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String userName = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String registeredDate = resultSet.getString("registered_date");
                String role = resultSet.getString("role.role");
                String imagePath = resultSet.getString("image_path");
                String phone = resultSet.getString("phone");


                User user = new User(id, firstName, lastName, userName, email, password, registeredDate, role, imagePath, phone);
                users.add(user);
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        Data.getInstance().setUsers(users);
    }

    public int updateUser(int userID, String firstName, String lastName, String userName, String email, String password, String registeredDate, int roleID){
        try{
            statement = connection.createStatement();
            boolean isUserAdded = statement.execute(("UPDATE `user` " +
                    "SET `firstName` = '%s', `lastName` = '%s', `username` = '%s', `email` = '%s', `password` = '%s', `registered_date` = '%s', `role_id` = %d WHERE `id` = %d ").formatted(
                            firstName, lastName, userName, email, password, registeredDate, roleID, userID
            ));
            Data.getInstance().refreshUsers();
            return 1;
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return -1;
    }

    public ResultSet getUser(String userName, String password){
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * " +
                            "FROM `user` INNER JOIN `role` ON `user`.`role_id` = `role`.`id` " +
                            "WHERE `username` = '%s' AND `password` = '%s'".formatted(userName, password));
            resultSet.next();
            try {
                System.out.println(resultSet.getString("username"));
                return resultSet;
            } catch(SQLException e){
                Logger.logError(e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
//            System.out.println(resultSet.getFetchSize());
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
            sqlException.printStackTrace();
        }
        return null;
    }

    public void clearDebtUsingPoints(int userID){

    }

    public List<Customer> getCustomersRegisteredOn(String timeFrame){
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = null;

        int hoursElapsed = endTime.getHour();
        int minutesElapsed = endTime.getMinute();
        int secondsElapsed = endTime.getSecond();
        int dayOfTheWeek = endTime.getDayOfWeek().getValue();

        LocalDateTime todayStart = endTime.minusSeconds(secondsElapsed)
                .minusMinutes(minutesElapsed)
                .minusHours(hoursElapsed);

        int dateElapsed = endTime.getDayOfMonth();
        int dayInTheYear = endTime.getDayOfYear();
        int monthsElapsed = 0;

        switch (timeFrame) {
            case "Today" -> startTime = todayStart;
            case "This Week" -> startTime = endTime.minusDays(dayOfTheWeek);
            case "Last Week" -> {
                startTime = endTime.minusDays(dayOfTheWeek).minusWeeks(1);
                endTime = endTime.minusDays(dayOfTheWeek);
            }
            case "This month" -> startTime = endTime.minusDays(dateElapsed);
            case "Last Month" -> {
                startTime = endTime.minusDays(dateElapsed).minusMonths(1);
                endTime = endTime.minusDays(dateElapsed);
            }
            case "This Year" -> startTime = endTime.minusDays(dayInTheYear);
            case "Last Year" -> startTime = endTime.minusDays(dayInTheYear).minusYears(1);
        }


        List<Customer> customers = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet;

            if (startTime != null) {
                resultSet = statement.executeQuery("SELECT * FROM `customer` WHERE `registered_date` >= '%s' AND `registered_date` <= '%s'".formatted(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            }else{
                resultSet = statement.executeQuery("SELECT * FROM `customer`");
            }

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String registeredDate = resultSet.getString("registered_date");
                String pathToImage = resultSet.getString("image_path");
                double points = resultSet.getDouble("points");
                double refundAmount = resultSet.getDouble("refund_amount");

                Customer customer = new Customer(id, firstName, lastName, phone, email, registeredDate, pathToImage, points, refundAmount);
                customers.add(customer);
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return customers;
    }

    public List<Customer> getCustomers(){
        List<Customer> customers = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `customer` ");
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String registeredDate = resultSet.getString("registered_date");
                String pathToImage = resultSet.getString("image_path");
                double points = resultSet.getDouble("points");
                double refundAmount = resultSet.getDouble("refund_amount");

                Customer customer = new Customer(id, firstName, lastName, phone, email, registeredDate,pathToImage, points, refundAmount);
                customers.add(customer);
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return customers;
    }

    /*
    * Get the details of all the users and show the details
    * */
    public ObservableList<User> getUserDetail() {
        ObservableList<User> userList = FXCollections.observableArrayList();

//        String query = "SELECT `id`, `firstName`, `lastName`, `username`, `email`, `password` FROM user";
//
//        try (PreparedStatement stmt = connection.prepareStatement(query);
//             ResultSet resultSet = stmt.executeQuery()) {
//
//            while (resultSet.next()) {
//                User user = new User(
//                        resultSet.getInt("id"),
//                        resultSet.getString("firstName"),
//                        resultSet.getString("lastName"),
//                        resultSet.getString("username"),
//                        resultSet.getString("email"),
//                        resultSet.getString("password")
//                        resultSet.getString("")
//                        resultSet.getString("password")
//                );
//                userList.add(user);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return userList;
    }

    /**
     * Add new customer
     */
    public void addNewCustomer(){

    }

    /**
     * Delete user
     * @param customerID - ID of the user in the database
     * @return int - 1 if the process completes without an error. -1 if there is any error occurred
     */
    public int deleteCustomer(int customerID){
        try{
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM `customer` WHERE `id` = %d".formatted(customerID));
            Data.getInstance().refreshCustomers();
            return 1;
        }catch (SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return -1;
    }

    public void filterCustomers(String filterValue) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `customer` WHERE " +
                    "`first_name` LIKE '%" + filterValue + "%' OR " +
                    "`last_name` LIKE '%" + filterValue + "%' OR " +
                    "`email` LIKE '%" + filterValue + "%' OR " +
                    "`phone` LIKE '%" + filterValue + "%' OR " +
                    "`registered_date` LIKE '" + filterValue + "'"
            );
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String registeredDate = resultSet.getString("registered_date");
                String pathToImage = resultSet.getString("image_path");
                double points = resultSet.getDouble("points");
                double refundAmount = resultSet.getDouble("refund_amount");

                Customer customer = new Customer(id, firstName, lastName, phone, email, registeredDate,pathToImage, points, refundAmount);
                customers.add(customer);
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        Data.getInstance().setCustomers(customers);
    }

    public String addCustomers(String first_name, String last_name, String phone, String email, String registeredDate, String imagePath) {
        try {
            if (email == null || email.trim().isEmpty()) {
                email = "Not included";
            }

            // Check if customer already exists
            PreparedStatement checkPs = connection.prepareStatement(
                    "SELECT COUNT(*) FROM customer WHERE first_name = ? AND last_name = ? AND phone = ? AND email = ?"
            );
            checkPs.setString(1, first_name);
            checkPs.setString(2, last_name);
            checkPs.setString(3, phone);
            checkPs.setString(4, email);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return "Customer already exists!";
            }

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO customer (first_name, last_name, phone, email, registered_date, image_path) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(6, imagePath);
            ps.setDate(5, java.sql.Date.valueOf(registeredDate));
            ps.executeUpdate();

            Data.getInstance().refreshCustomers();
            return "Customer added successfully!";
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
            return "Error: Could not add customer.";
        }
    }

    /**
    * Gets the customers who have bought stuffs
    */
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
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return totalCustomers;
    }

    public void storeSales (int customerId, int itemHasSizeId, int amount, double price, int item_status_id) {
        String theCurrentDate = LocalDate.now().toString();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO customer_has_item_has_size (customer_id, item_has_size_id, amount, price, date, item_Status_id) VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, customerId);
            stmt.setInt(2, itemHasSizeId);
            stmt.setInt(3, amount);
            stmt.setDouble(4, price);
            stmt.setString(5, theCurrentDate);
            stmt.setInt(6, item_status_id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRemainingQuantity(int itemHasSizeId, int purchasedAmount) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE item_has_size SET remaining_qty = remaining_qty - ? WHERE id = ?");

            stmt.setInt(1, purchasedAmount);
            stmt.setInt(2, itemHasSizeId);;

            int rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public ObservableList<CheckoutItem> getCheckoutItems() {
        ObservableList<CheckoutItem> itemList = FXCollections.observableArrayList();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT chs.id AS id, chs.refund_amount AS refund_amount, i.name AS item_name, s.size AS item_size, c.color AS item_color, " +
                            "chs.amount, chs.price AS checkout_price, sale.date, ihs.price AS selling_price, chs.discount AS discount " +
                            "FROM customer_has_item_has_size chs " +
                            "INNER JOIN sale ON chs.sale_id = sale.id " +
                            "JOIN item_has_size ihs ON chs.item_has_size_id = ihs.id " +
                            "JOIN item i ON ihs.item_id = i.id " +
                            "JOIN size s ON ihs.size_id = s.id " +
                            "JOIN color_has_item_has_size chis ON chis.item_has_size_id = ihs.id " +
                            "JOIN color c ON chis.color_id = c.id"
            );

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("item_name");
                String size = rs.getString("item_size");
                String color = rs.getString("item_color");
                int amount = rs.getInt("amount");
                int price = rs.getInt("checkout_price");
                double sellingPrice = rs.getDouble("selling_price");
                double discount = rs.getDouble("discount");
                double refundAmount = rs.getDouble("refund_amount");
                double totalCost = amount * sellingPrice;

                String date = rs.getString("date");

                double costWithDiscount = (sellingPrice * discount / 100) * amount;

                CheckoutItem item = new CheckoutItem(
                        id,
                        name,
                        size,
                        color,
                        amount,
                        price,
                        sellingPrice,
                        discount,
                        String.valueOf(totalCost),
                        costWithDiscount,
                        refundAmount);
                itemList.add(item);
            }

        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }

        return itemList;
    }

    public ObservableList<CheckoutItem> getCheckoutItemWithoutColor() {
        ObservableList<CheckoutItem> itemList = FXCollections.observableArrayList();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT chs.id AS id, chs.refund_amount AS refund_amount, i.name AS item_name, s.size AS item_size, " +
                            "chs.amount, chs.discount as discount, chs.price AS checkout_price, sale.date, ihs.price AS selling_price " +
                            "FROM customer_has_item_has_size chs INNER JOIN sale ON chs.sale_id = sale.id " +
                            "JOIN item_has_size ihs ON chs.item_has_size_id = ihs.id " +
                            "JOIN item i ON ihs.item_id = i.id " +
                            "JOIN size s ON ihs.size_id = s.id"
            );
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("item_name");
                String size = rs.getString("item_size");
                String color = "N/A";
                int amount = rs.getInt("amount");
                int price = rs.getInt("checkout_price");
                double sellingPrice = rs.getDouble("selling_price");
                double discount = rs.getDouble("discount");
                double refundAmount = rs.getDouble("refund_amount");

                double totalCost = amount * sellingPrice;

//                String date = rs.getString("sale.date");

                double costWithDiscount = (sellingPrice * discount / 100) * amount;

                CheckoutItem item = new CheckoutItem(id, name, size, color, amount, price, sellingPrice, String.valueOf(totalCost), costWithDiscount, refundAmount);
                itemList.add(item);
            }
        } catch (SQLException e) {
            Logger.logError(e.getMessage(), e);
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
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(e.getMessage(), e);
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
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStock(int id, String date, String newName){
        try{
            statement = connection.createStatement();
            int result = statement.executeUpdate("UPDATE `stock` SET `name` = '%s', `date` = '%s' WHERE `id` = %d".formatted(newName, date, id));
            Data.getInstance().refreshStock();
            return result > 0;
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
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
                Stock stock = new Stock(id, date, name);
                rows.add(stock);
            }
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
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
                stock = new Stock(id, date, name);
            }
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
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
                Stock stock = new Stock(id, date, name);
                rows.add(stock);
            }
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
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
            Logger.logError(e.getMessage(), e);
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
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return userAnalytics;
    }

    public List<Customer> getTopTenCustomers(){
        List<Customer> customerList = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT `customer_id`, `customer`.*, COUNT(*) AS count FROM `customer_has_item_has_size` INNER JOIN `customer` ON `customer_has_item_has_size`.`customer_id` = `customer`.`id` GROUP BY `customer_id` ORDER BY count DESC LIMIT 10");
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String registeredDate = resultSet.getString("registered_date");
                String pathToImage = resultSet.getString("image_path");
                double points = resultSet.getDouble("points");
                double refundAmount = resultSet.getDouble("refund_amount");

                Customer customer = new Customer(id, firstName, lastName, phone, email, registeredDate, pathToImage, points, refundAmount);
                customerList.add(customer);
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return customerList;
    }

    public Customer getCustomer(int customerID){
        Customer customer = null;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `customer`WHERE id = %d".formatted(customerID));
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String registeredDate = resultSet.getString("registered_date");
                String pathToImage = resultSet.getString("image_path");
                double points = resultSet.getDouble("points");
                double refundAmount = resultSet.getDouble("refund_amount");

                customer = new Customer(id, firstName, lastName, phone, email, registeredDate, pathToImage, points, refundAmount);
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return customer;
    }

    public int[] getUserCounts(){
        int numberOfCustomers = 0;
        int numberOfAdmins = 0;
        int numberOfUsers = 0;

        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `user` INNER JOIN `role` ON `user`.`role_id` = `role`.`id`");
            while (resultSet.next()){
                String role = resultSet.getString("role.role");
                if (role.equals("Admin")){
                    numberOfAdmins += 1;
                }else{
                    numberOfUsers += 1;
                }
            }

            resultSet = statement.executeQuery("SELECT * FROM `customer`");
            while (resultSet.next()){
                numberOfCustomers += 1;
            }
        }catch(SQLException exception){
            Logger.logError(exception.getMessage(), exception);
            exception.printStackTrace();
        }
        return new int[]{numberOfAdmins, numberOfUsers, numberOfCustomers};
    }

    public double getAccountsReceivable(int customerID){
        double totalDueBalance = 0.0D;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `sale` INNER JOIN `customer` ON `customer`.`id` = `sale`.`customer_id` INNER JOIN `remains_status` ON `sale`.`remains_statuss_id` = `remains_status`.`id` WHERE `customer_id` = %d AND `remains_statuss_id` = %d".formatted(customerID, 4));
            while (resultSet.next()){
                double receivedMoney = resultSet.getDouble("sale.received_amount");
                double totalCost = resultSet.getDouble("sale.total_cost");
                int remainsStatus = resultSet.getInt("sale.remains_statuss_id");

                double dueBalance = receivedMoney - totalCost;
                if (dueBalance < 0 && remainsStatus == 4){
                    totalDueBalance += dueBalance;
                }
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return totalDueBalance;
    }

    public String clearCustomerDebt(Customer customer, double amount, int saleID, boolean fromPoints){
        double remains = amount;
        try {
            statement = connection.createStatement();
            HashMap<Integer, List<Double>> saleIDs = new HashMap<>();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM `sale` INNER JOIN `customer` ON `customer`.`id` = `sale`.`customer_id` INNER JOIN `remains_status` ON `sale`.`remains_statuss_id` = `remains_status`.`id` WHERE `sale`.`customer_id` = %d".formatted(customer.getId()));
            while (resultSet.next()){
                int saleId = resultSet.getInt("sale.id");
                double receivedMoney = resultSet.getDouble("sale.received_amount");
                double totalCost = resultSet.getDouble("sale.total_cost");

                if (receivedMoney < totalCost) {
                    double receivableAmount = ((receivedMoney - totalCost) * -1);
                    ArrayList<Double> dataList = new ArrayList<>();
                    dataList.add(receivedMoney);
                    dataList.add(receivableAmount);
                    dataList.add(totalCost);
                    saleIDs.put(saleId, dataList);
                }
            }
            if (fromPoints) {
                double points = customer.getPoints();
                if (points >= remains){
                    points -= remains;
                    statement.execute("UPDATE `customer` SET `points` = %f WHERE `id` = %d".formatted(points, customer.getId()));

                    for (Map.Entry<Integer, List<Double>> dataEntry : saleIDs.entrySet()){
                        int saleId = dataEntry.getKey();
                        double receivedMoney = dataEntry.getValue().get(0);
                        double liableAmount = dataEntry.getValue().get(1);
                        double totalCost = dataEntry.getValue().get(2);

                        double newReceivedAmount = 0.0;
                        if (liableAmount > remains) {
                            newReceivedAmount = receivedMoney + remains;
                            remains = 0.0D;
                        } else if (liableAmount <= remains) {
                            remains -= liableAmount;
                            newReceivedAmount = totalCost;
                        }

                        statement.execute(("" +
                                "UPDATE `sale` SET `received_amount` = %f WHERE `id` = %d").formatted(newReceivedAmount, saleId));
                    }
                    return "Debt cleared successfully!";
                }else{
                    return "There is no enough points to clear debt!";
                }
            }else{
                for (Map.Entry<Integer, List<Double>> dataEntry : saleIDs.entrySet()){
                    int saleId = dataEntry.getKey();
                    double receivedMoney = dataEntry.getValue().get(0);
                    double liableAmount = dataEntry.getValue().get(1);
                    double totalCost = dataEntry.getValue().get(2);

                    double newReceivedAmount = 0.0;

                    if (liableAmount > remains){
                        newReceivedAmount = receivedMoney + remains;
                        remains = 0.0D;
                    }else if(liableAmount <= remains){
                        remains -= liableAmount;
                        newReceivedAmount = totalCost;
                    }

                    statement.execute(("" +
                            "UPDATE `sale` SET `received_amount` = %f WHERE `id` = %d").formatted(newReceivedAmount, saleId));
                }
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
            return "An error occurred";
        }

        return "Debt settled successfully!";
    }

    public HashMap<Customer, Map<Integer, Sale>> getLiabilities(){
        HashMap<Customer, Map<Integer, Sale>> customerSalesMap = new HashMap<>();
        List<Integer> customerIDs = new ArrayList<>();
        Map<Integer, Customer> customers = new HashMap<>();

        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `sale` INNER JOIN `customer` ON `customer`.`id` = `sale`.`customer_id` INNER JOIN `remains_status` ON `sale`.`remains_statuss_id` = `remains_status`.`id`");
            while (resultSet.next()){
                int customerId = resultSet.getInt("customer_id");

                Customer customer = getCustomer(customerId);

                int saleID = resultSet.getInt("sale.id");
                String saleDate = resultSet.getString("sale.date");
                double receivedMoney = resultSet.getDouble("sale.received_amount");
                double totalCost = resultSet.getDouble("sale.total_cost");
                int remainsStatus = resultSet.getInt("sale.remains_statuss_id");
                Sale sale = new Sale(saleID, saleDate, receivedMoney, totalCost, remainsStatus);

                if (!customers.containsKey(customerId)){
                    customers.put(customerId, customer);
                    Map<Integer, Sale> salesMap = new HashMap<>();
                    salesMap.put(saleID, sale);
                    customerSalesMap.put(customers.get(customerId), salesMap);
                }else{
                    if (!customerSalesMap.get(customers.get(customerId)).containsKey(saleID)){
                        customerSalesMap.get(customers.get(customerId)).put(saleID, sale);
                    }
                }
            }

        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return customerSalesMap;
    }

    public List<ItemDetail> getCustomerLiableItems(Customer customer){
        List<ItemDetail> customerLiableItems = new ArrayList<>();
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `customer_has_item_has_size` " +
                            "INNER JOIN `sale` ON `sale`.`id` = `customer_has_item_has_size`.`sale_id` " +
                            "INNER JOIN `item_has_size` ON `customer_has_item_has_size`.`item_has_size_id` = `item_has_size`.`id` " +
                            "INNER JOIN `customer` ON `customer`.`id` = `sale`.`customer_id` " +
                            "INNER JOIN `color_has_item_has_size` ON `color_has_item_has_size`.`item_has_size_id` = `item_has_size`.`id` " +
                            "INNER JOIN `remains_status` ON `sale`.`remains_statuss_id` = `remains_status`.`id` " +
                            "WHERE `customer_has_item_has_size`.`customer_id` = %d".formatted(customer.getId()));
            while (resultSet.next()){
                int customerId = resultSet.getInt("customer_id");
                int colorHasItemHasSizeID = resultSet.getInt("color_has_item_has_size.id");
                ItemDetail itemDetail = getItemDetail(colorHasItemHasSizeID);

                int saleID = resultSet.getInt("sale.id");
                String saleDate = resultSet.getString("sale.date");
                double receivedMoney = resultSet.getDouble("sale.received_amount");
                double totalCost = resultSet.getDouble("sale.total_cost");
                int remainsStatus = resultSet.getInt("sale.remains_statuss_id");
                Sale sale = new Sale(saleID, saleDate, receivedMoney, totalCost, remainsStatus);

                if (receivedMoney < totalCost && remainsStatus == 4){
                    customerLiableItems.add(itemDetail);
                }
            }
        }catch(SQLException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }
        return customerLiableItems;
    }


    public double filterLiabilities(String dateRange){
        double accountsReceivable = 0.0D;
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        int currentDayOfYear = now.getDayOfYear();
        int currentDayOfMonth = now.getDayOfMonth();
        int currentDayOfWeek = now.getDayOfWeek().getValue();

        LocalDate from = LocalDate.parse(today);
        if (dateRange.equals("This week")){
            from = from.minusDays(currentDayOfWeek);
        }else if(dateRange.equals("This month")){
            from = from.minusDays(currentDayOfMonth);
        }else if(dateRange.equals("This year")){
            from = from.minusDays(currentDayOfYear);
        }else if(dateRange.equals("Yesterday")){
            from = LocalDate.parse(today).minusDays(1);
        }else if(dateRange.equals("Last week")){
            from  = LocalDate.parse(today).minusDays(currentDayOfWeek).minusWeeks(1);
        }else if(dateRange.equals("Last month")){
            from  = LocalDate.parse(today).minusDays(currentDayOfMonth).minusMonths(1);
        }else if(dateRange.equals("Last year")){
            from  = LocalDate.parse(today).minusDays(currentDayOfYear).minusYears(1);
        }

        String fromDate = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `sale` WHERE `date` >= '%s' AND `date` <= '%s'".formatted(fromDate, today));
            while (resultSet.next()){
                String date = resultSet.getString("date");
                Double receivedAmount = resultSet.getDouble("received_amount");
                Double totalCost = resultSet.getDouble("total_cost");
                if (receivedAmount < totalCost){
                    accountsReceivable += ((receivedAmount - totalCost) * -1);
                }
            }
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
            sqlException.printStackTrace();
        }
        return accountsReceivable;
    }

    public double filterPoints(String dateRange){
        double points = 0.0D;
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        int currentDayOfYear = now.getDayOfYear();
        int currentDayOfMonth = now.getDayOfMonth();
        int currentDayOfWeek = now.getDayOfWeek().getValue();

        LocalDate from = LocalDate.parse(today);
        if (dateRange.equals("This week")){
            from = from.minusDays(currentDayOfWeek);
        }else if(dateRange.equals("This month")){
            from = from.minusDays(currentDayOfMonth);
        }else if(dateRange.equals("This year")){
            from = from.minusDays(currentDayOfYear);
        }else if(dateRange.equals("Yesterday")){
            from = LocalDate.parse(today).minusDays(1);
        }else if(dateRange.equals("Last week")){
            from  = LocalDate.parse(today).minusDays(currentDayOfWeek).minusWeeks(1);
        }else if(dateRange.equals("Last month")){
            from  = LocalDate.parse(today).minusDays(currentDayOfMonth).minusMonths(1);
        }else if(dateRange.equals("Last year")){
            from  = LocalDate.parse(today).minusDays(currentDayOfYear).minusYears(1);
        }

        String fromDate = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try{
            statement = connection.createStatement();
            String query = "SELECT * FROM `sale` WHERE `date` = '%s'".formatted(today);
            if (!dateRange.equals("Today")){
                query = "SELECT * FROM `sale` WHERE `date` >= '%s' AND `date` <= '%s'".formatted(fromDate, today);
            }

            query = "SELECT * FROM `sale` WHERE `date` >= '%s' AND `date` <= '%s'".formatted(fromDate, today);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                String date = resultSet.getString("date");
                Double receivedAmount = resultSet.getDouble("received_amount");
                Double totalCost = resultSet.getDouble("total_cost");
                int remainsStatusId = resultSet.getInt("remains_statuss_id");

                if (receivedAmount > totalCost && remainsStatusId == 2){
                    points += (receivedAmount - totalCost);
                }
            }
        }catch(SQLException sqlException){
            Logger.logError(sqlException.getMessage(), sqlException);
            sqlException.printStackTrace();
        }
        return points;
    }

}
