package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    private Connection conn;
    Statement statement;

    public QueryExecutor(Connection conn)
    {
        this.conn = conn;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {

        }
    }

    public void executeQuery(String query) throws SQLException {
        statement.execute(query);
    }

    public void commit() {
        try {
            conn.close();
        } catch (SQLException e) {

        }
    }
}
