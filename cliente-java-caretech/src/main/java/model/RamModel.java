package model;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;

import java.util.List;

public class RamModel extends Hardware {
    public RamModel(Integer id_hardware, String nome_hardware, Double capacidade_total, Double min, Double max, Integer fk_computador) {
        super(id_hardware, nome_hardware, capacidade_total, min, max, fk_computador);
    }

    public RamModel(){ super(); }

    @Override
    public <T> T autenticarHardware(Integer fk_computador) {
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();

        List<RamModel> ram = con.query(
                "SELECT * FROM hardware WHERE nome_hardware = 'ram' AND fk_computador = %s".formatted(fk_computador),
                new BeanPropertyRowMapper<>(RamModel.class)
        );

        return (T) ram;
    }

    @Override
    public void inserirHardware(Integer fkComputador, Double capacidadeTotal) {
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();

        capacidadeTotal.toString().replace(',', '.');

        con.execute("INSERT INTO hardware (nome_hardware, capacidade_total, fk_computador) VALUES ('ram', %s, %d)".formatted(capacidadeTotal, fkComputador));
    }
}
