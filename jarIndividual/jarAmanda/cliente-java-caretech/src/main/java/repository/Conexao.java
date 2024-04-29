package repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {
    private String bd = "caretech";
    private String user = "root";
    private String pass = "sptech2023";
    private JdbcTemplate conexaoDoBanco;

    public Conexao() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + this.bd);
        dataSource.setUsername(this.user);
        dataSource.setPassword(this.pass);

        conexaoDoBanco = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConexaoDoBanco() {
        return conexaoDoBanco;
    }
}