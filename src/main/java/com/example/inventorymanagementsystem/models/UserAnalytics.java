package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class UserAnalytics implements DataModel {
    IntegerProperty totalNumberOfUsers;
    IntegerProperty numberOfAdmins;
    IntegerProperty numberOfStaffMembers;

    IntegerProperty totalNumberOfCustomers;
    IntegerProperty numberOfCustomersInSpecificDateRange;

    ListProperty<String> topTenCustomers;

    public UserAnalytics(
            int totalNumberOfUsers,
            int numberOfAdmins,
            int numberOfStaffMembers,
            int totalNumberOfCustomers,
            int numberOfCustomersInSpecificDateRange,
            String[] topTenCustomers){

        totalNumberOfUsersProperty().setValue(totalNumberOfUsers);
        numberOfAdminsProperty().setValue(numberOfAdmins);
        numberOfStaffMembersProperty().setValue(numberOfStaffMembers);
        totalNumberOfCustomersProperty().setValue(totalNumberOfCustomers);
        numberOfCustomersInSpecificDateRangeProperty().setValue(numberOfCustomersInSpecificDateRange);
        topTenCustomersProperty().setValue(FXCollections.observableArrayList(topTenCustomers));

    }

    public IntegerProperty totalNumberOfUsersProperty() {
        if (totalNumberOfUsers == null){
            totalNumberOfUsers = new SimpleIntegerProperty();
        }
        return totalNumberOfUsers;
    }

    public IntegerProperty numberOfAdminsProperty(){
        if (numberOfAdmins == null){
            numberOfAdmins = new SimpleIntegerProperty();
        }
        return numberOfAdmins;
    }

    public IntegerProperty numberOfStaffMembersProperty(){
        if (numberOfStaffMembers == null){
            numberOfStaffMembers = new SimpleIntegerProperty();
        }
        return numberOfStaffMembers;
    }

    public IntegerProperty totalNumberOfCustomersProperty() {
        if (totalNumberOfCustomers == null){
            totalNumberOfCustomers = new SimpleIntegerProperty();
        }
        return totalNumberOfCustomers;
    }

    public IntegerProperty numberOfCustomersInSpecificDateRangeProperty(){
        if (numberOfCustomersInSpecificDateRange == null){
            numberOfCustomersInSpecificDateRange = new SimpleIntegerProperty();
        }
        return numberOfCustomersInSpecificDateRange;
    }

    public ListProperty<String> topTenCustomersProperty(){
        if (topTenCustomers == null){
            topTenCustomers = new SimpleListProperty<>();
        }
        return topTenCustomers;
    }

    @Override
    public String getValue() {
        return null;
    }
}
