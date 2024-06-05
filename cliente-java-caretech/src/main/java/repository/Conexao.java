package repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {
    private String host = System.getenv("DB_HOST");
    private String bd = System.getenv("DB_NAME");
    private String user = System.getenv("DB_USER");
    private String pass = System.getenv("DB_PASSWORD");
    private String port = System.getenv("DB_PORT");
    private String ambiente = System.getenv("AMBIENTE");
    private JdbcTemplate conexaoDoBanco;

    public Conexao() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        if(ambiente == null){
            dataSource.setUrl("jdbc:mysql://localhost:3306/caretech");
            dataSource.setUsername("root");
            dataSource.setPassword("urubu100");
        }else {
            // configurar variável ambiente no compose para identificar o ambiente de produção
            dataSource.setUrl("jdbc:mysql://%s:%s/%s".formatted(host, port, bd));
            dataSource.setUsername(this.user);
            dataSource.setPassword(this.pass);
        }

        conexaoDoBanco = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConexaoDoBanco() {
        return conexaoDoBanco;
    }
}