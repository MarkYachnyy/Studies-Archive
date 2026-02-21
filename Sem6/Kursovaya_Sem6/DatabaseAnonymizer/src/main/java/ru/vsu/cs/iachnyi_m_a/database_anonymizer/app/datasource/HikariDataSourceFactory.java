package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceFactory {
    public static HikariDataSource create(String db_name, String username, String password, int idleTimeout, int maxPoolSize) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/" + db_name);
        config.setUsername(username);
        config.setPassword(password);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(maxPoolSize);
        return new HikariDataSource(config);
    }
}
