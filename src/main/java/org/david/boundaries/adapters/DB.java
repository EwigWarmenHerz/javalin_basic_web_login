package org.david.boundaries.adapters;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalConnectionFactoryConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalConnectionPoolConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.SimplePassword;
import org.david.miscellaneous.custom_exceptions.CustomExceptions;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import java.sql.SQLException;
import java.time.Duration;
import java.util.function.Function;
public class DB {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/umbrella";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "user";
    private  final AgroalDataSource dataSource;
    public static final DB instance = new DB();

    private DB(){
        try{
            var config = new AgroalConnectionFactoryConfigurationSupplier()
                .jdbcUrl(DB_URL)
                .principal(() -> DB_USER)
                .credential(new SimplePassword(DB_PASSWORD))
                .connectionProviderClassName("org.postgresql.Driver");
            var poolConfig = new AgroalConnectionPoolConfigurationSupplier()
                .connectionFactoryConfiguration(config)
                .maxSize(10)
                .minSize(2)
                .initialSize(2)
                .acquisitionTimeout(Duration.ofSeconds(5))
                .leakTimeout(Duration.ofMinutes(1))
                .validationTimeout(Duration.ofSeconds(30))
                .maxLifetime(Duration.ofHours(1));
            var dataSourceConfig = new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration(poolConfig)
                .metricsEnabled(false);
            dataSource = AgroalDataSource.from(dataSourceConfig);
        }catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database");
        }
    }

    public <T> T execute(Function<DSLContext,T> f) throws SQLException {
        try(var connection = dataSource.getConnection()){
            var dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            return f.apply(dslContext);
        }catch (Exception e){
            throw new CustomExceptions.GenericSQLException("something went wrong");
        }
    }

}
