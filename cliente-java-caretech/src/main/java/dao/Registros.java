package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;

import java.util.Locale;

public class Registros {
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();
//    public void inserirRegistros(
//            Double usoRam,
//            Double usoCpu,
//            Integer qtdProcessos,
//            Double usoDisco,
//            Integer fkComputador
//    ){
//
//        String ram = usoRam.toString().replace(',', '.');
//        String cpu = usoCpu.toString().replace(',', '.');
//        String disco = usoDisco.toString().replace(',', '.');
//
//        con.execute("INSERT INTO registros (uso_ram, uso_cpu, qtd_processos, uso_disco, velocidade_rede, fk_computador) VALUES (%s, %s, %d, %s, %d, %d)"
//                .formatted(ram, cpu, qtdProcessos, disco, null, fkComputador));
//    }



    public void inserirDisco(Double usoDisco, Integer fkHardware){
        String disco = usoDisco.toString().replace(',', '.');

        con.execute("INSERT INTO registros (uso_capacidade, fk_hardware) VALUES (%s, %d)".formatted(disco, fkHardware));
    }

    public void inserirCpu(Double usoCpu, Integer fkHardware){
        String cpu = usoCpu.toString().replace(',', '.');

        con.execute("INSERT INTO registros (uso_capacidade, fk_hardware) VALUES (%s, %d)".formatted(cpu, fkHardware));
    }

    public void inserirRam(Double usoRam, Integer qtdDeProcessos, Integer fkHardware){
        String ram = usoRam.toString().replace(',', '.');

        con.execute("INSERT INTO registros (uso_capacidade, qtd_processos, fk_hardware) VALUES (%s, %d, %d)".formatted(ram, qtdDeProcessos, fkHardware));
    }

}

