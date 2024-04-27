package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;

import java.util.Locale;

public class Registros {
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();
    public void inserirRegistros(
            Double usoRam,
            Double usoCpu,
            Integer qtdProcessos
    ){

        String ram = usoRam.toString().replace(',', '.');
        String cpu = usoCpu.toString().replace(',', '.');

        con.execute("INSERT INTO registros (uso_ram, uso_cpu, qtd_processos, fk_computador) VALUES (%s, %s, %d, %d)"
                .formatted(ram, cpu, qtdProcessos, 1));
    }
}

