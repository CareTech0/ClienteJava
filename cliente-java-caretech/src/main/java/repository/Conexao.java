package repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {
    private String host = System.getenv("DB_HOST");
    private String bd = System.getenv("DB_NAME");
    private String user = System.getenv("DB_USER");
    private String pass = System.getenv("DB_PASSWORD");
    private String port = System.getenv("DB_PORT");

    private JdbcTemplate conexaoDoBanco;

    public Conexao() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //dataSource.setUrl("jdbc:mysql://localhost:3306/" + this.bd);
        //dataSource.setUsername("root");
        //dataSource.setPassword("urubu100");

        dataSource.setUrl("jdbc:mysql://%s:%s/%s".formatted(host, port, bd));
        dataSource.setUsername(this.user);
        dataSource.setPassword(this.pass);

        conexaoDoBanco = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConexaoDoBanco() {
        return conexaoDoBanco;
    }
}