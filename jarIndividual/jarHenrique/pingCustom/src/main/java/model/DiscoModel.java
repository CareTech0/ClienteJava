package model;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;
import repository.ConexaoSqlServer;

import java.util.List;

public class DiscoModel extends Hardware{


    public DiscoModel() { super(); }

    public DiscoModel(Integer id_hardware, String nome_hardware, Double capacidade_total, Double min, Double max, Integer fk_computador) {
        super(id_hardware, nome_hardware, capacidade_total, min, max, fk_computador);
    }

    @Override
    public <T> T autenticarHardware(Integer fk_computador, String banco) {

        JdbcTemplate con;
        if(banco.equalsIgnoreCase("sqlserver")){
            con = ConexaoSqlServer.conexaoSqlServer;
        }else {
            Conexao conexao = new Conexao();
            con = conexao.getConexaoDoBanco();
        }

        List<DiscoModel> cpu = con.query(
                "SELECT * FROM hardware WHERE nome_hardware = 'disco' AND fk_computador = %s".formatted(fk_computador),
                new BeanPropertyRowMapper<>(DiscoModel.class)
        );

        return (T) cpu;
    }

    @Override
    public void inserirHardware(Integer fkComputador, Double capacidadeTotal, String banco) {
        JdbcTemplate con;
        if(banco.equalsIgnoreCase("sqlserver")){
            con = ConexaoSqlServer.conexaoSqlServer;
        } else {
            Conexao conexao = new Conexao();
            con = conexao.getConexaoDoBanco();
        }

        capacidadeTotal.toString().replace(',', '.');

        con.execute("INSERT INTO hardware (nome_hardware, capacidade_total, fk_computador) VALUES ('disco', %s, %d)".formatted(capacidadeTotal, fkComputador));
    }

}
