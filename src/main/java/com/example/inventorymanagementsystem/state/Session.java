package com.example.inventorymanagementsystem.state;

public class Session {
    private static Session session;
    private boolean loggedIn;
    private Session(){
        loggedIn = true;
    }

    public static Session getInstance(){
        if(session == null){
            session = new Session();
        }
        return session;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public void destroy(){
        loggedIn = false;
        session = null;
    }
}
