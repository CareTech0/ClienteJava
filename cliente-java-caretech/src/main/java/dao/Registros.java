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
            Integer qtdProcessos,
            Double usoDisco,
            Integer fkComputador
    ){

        String ram = usoRam.toString().replace(',', '.');
        String cpu = usoCpu.toString().replace(',', '.');
        String disco = usoDisco.toString().replace(',', '.');

        con.execute("INSERT INTO registros (uso_ram, uso_cpu, qtd_processos, uso_disco, velocidade_rede, fk_computador) VALUES (%s, %s, %d, %s, %d, %d)"
                .formatted(ram, cpu, qtdProcessos, disco, null, fkComputador));
    }
}

