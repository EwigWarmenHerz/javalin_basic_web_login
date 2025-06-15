package org.david.boundaries.adapters;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import java.sql.SQLException;
import java.util.function.Function;
public class DB {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/umbrella";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "user";
    private static final HikariDataSource dataSource;


    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(64);
        dataSource = new HikariDataSource(config);
    }

    public <T> T execute(Function<DSLContext,T> f) throws SQLException {
        try(var connection = dataSource.getConnection()){
            var dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            return f.apply(dslContext);

        }
    }

}
