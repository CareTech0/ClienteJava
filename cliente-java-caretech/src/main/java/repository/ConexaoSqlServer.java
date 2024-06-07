package repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoSqlServer {

    private final String URL = "jdbc:sqlserver://54.85.6.232:1433;databaseName=caretech";
    private final String user = "sa";
    private final String password = "urubu100";
    public static JdbcTemplate conexaoSqlServer;


    public ConexaoSqlServer(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        conexaoSqlServer = new JdbcTemplate(dataSource);
    }
}
