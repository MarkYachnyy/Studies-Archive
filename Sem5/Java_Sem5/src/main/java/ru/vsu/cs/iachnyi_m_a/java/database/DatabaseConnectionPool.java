package ru.vsu.cs.iachnyi_m_a.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseConnectionPool {

    private final String URL;
    private final String USER;
    private final String PASS;

    private ArrayList<Connection> availableConns = new ArrayList<>();
    private ArrayList<Connection> usedConns = new ArrayList<>();

    public DatabaseConnectionPool(String url, String username, String password, String driver, int initConnCnt) {
        try {
            Class.forName(driver);
        } catch (Exception _) {

        }
        this.URL = url;
        this.USER = username;
        this.PASS = password;
        for (int i = 0; i < initConnCnt; i++) {
            availableConns.add(getConnection());
        }
        System.out.println(availableConns);
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public synchronized Connection retrieve() {
        Connection newConn;
        if (availableConns.isEmpty()) {
            newConn = getConnection();
        } else {
            newConn = availableConns.getLast();
            availableConns.remove(newConn);
        }
        usedConns.add(newConn);
        return newConn;
    }

    public synchronized void release(Connection con) {
        usedConns.remove(con);
        availableConns.add(con);
    }
}
