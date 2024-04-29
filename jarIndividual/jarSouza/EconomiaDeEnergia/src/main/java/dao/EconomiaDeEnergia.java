package dao;

import infraestrutura.Cpu;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;

import java.io.IOException;

public class EconomiaDeEnergia {
    public void modoEconomia(){
        Cpu dadosCpu = new Cpu();
        if (dadosCpu.buscarUsoCpu() >= 5){
            ativarModoEconomia();
        } else {
            desativarModoEconomia();
        }
    }

    public void ativarModoEconomia() {
            try {
                Thread.sleep(5000);
                diminuirBrilhoTela();
                reduzirVelocidadeCPU();
                alterarPlanoEnergia();
                System.out.println("Uso de Cpu maior que 70%.\nModo de economia de energia ativado.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void diminuirBrilhoTela() {
        try {
            Runtime.getRuntime().exec("powershell (Get-WmiObject -Namespace root/WMI -Class WmiMonitorBrightnessMethods).WmiSetBrightness(1, 20)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reduzirVelocidadeCPU() {
        try {
            Runtime.getRuntime().exec("powershell powercfg /setacvalueindex SCHEME_CURRENT SUB_PROCESSOR PERFBOOSTMODE 50");
            Runtime.getRuntime().exec("powershell powercfg /setactive SCHEME_CURRENT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alterarPlanoEnergia() {
        try {
            Runtime.getRuntime().exec("powershell powercfg /s a1841308-3541-4fab-bc81-f71556f20b4a");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void desativarModoEconomia() {
        try {
            restaurarBrilho();
            reativarOtimizacaoCPU();
            System.out.println("Modo de economia de energia desativado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restaurarBrilho() throws IOException {
        Runtime.getRuntime().exec("powershell (Get-WmiObject -Namespace root/WMI -Class WmiMonitorBrightnessMethods).WmiSetBrightness(1, 60)");
    }

    private void reativarOtimizacaoCPU() throws IOException {
        Runtime.getRuntime().exec("powershell powercfg /setacvalueindex SCHEME_CURRENT SUB_PROCESSOR PERFBOOSTMODE 100");
        Runtime.getRuntime().exec("powershell powercfg /setactive SCHEME_CURRENT");
    }
}
