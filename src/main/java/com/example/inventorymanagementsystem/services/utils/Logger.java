package com.example.inventorymanagementsystem.services.utils;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.state.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final static  String LOG_FILE_PATH;
    static {
        File logDir = new File(Constants.loggerDirectory);
        if (!logDir.exists()) {
            boolean directoriesCreated = logDir.mkdirs();
            System.out.println("Log directory created: " + directoriesCreated);
        }else{
            System.out.println("Log directory already exists");
        }

        LOG_FILE_PATH = Constants.loggerDirectory + "\\error.log";
    }

    public static void logError(String message, Throwable throwable){
        try(FileWriter fw = new FileWriter(LOG_FILE_PATH, true)){
            PrintWriter printWriter = new PrintWriter(fw);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            printWriter.println("[" + timestamp + "] ERROR: " + message);
            throwable.printStackTrace(printWriter);
            printWriter.println("--------------------------------------------------");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void logMessage(String message){
        try(FileWriter fw = new FileWriter(LOG_FILE_PATH, true)){
            PrintWriter printWriter = new PrintWriter(fw);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            printWriter.println("[" + timestamp + "] MSG: " + message);
            printWriter.println("--------------------------------------------------");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
