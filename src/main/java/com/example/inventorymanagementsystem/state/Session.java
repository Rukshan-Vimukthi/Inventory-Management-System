package com.example.inventorymanagementsystem.state;

import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.AuthenticateStateListener;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private static Session session;
    private static boolean loggedIn;

    private static List<AuthenticateStateListener> authenticateStateListeners = new ArrayList<>();

    private User user;
    private Session(){
        loggedIn = true;
    }

    public static Session getInstance(){
        if(session == null){
            session = new Session();
        }
        return session;
    }

    public static boolean isLoggedIn(){
        return loggedIn;
    }

    public void destroy(){
        loggedIn = false;
        session = null;
        notifyObservers(false);
    }

    public void setSessionUser(User user){
        this.user = user;
        notifyObservers(user != null);
    }

    public User getSessionUser(){
        return this.user;
    }

    public void notifyObservers(boolean isLoggedIn){
        for (AuthenticateStateListener authStateListener : authenticateStateListeners){
            if (isLoggedIn) {
                authStateListener.onLoggedIn();
            }else{
                authStateListener.onLoggedOut();
            }
        }
    }

    public static void addListener(AuthenticateStateListener authenticateStateListener){
        authenticateStateListeners.add(authenticateStateListener);
    }
}
