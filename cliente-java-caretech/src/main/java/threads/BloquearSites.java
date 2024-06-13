package threads;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import dao.SitesBloqueados;
import infraestrutura.MemoriaRam;
import logs.Logger;
import model.Computador;
import notificacoes.AutomacaoDeAlertasSlack;

import java.util.List;

public class BloquearSites extends Thread{

    private String user;
    private String senha;
    private String estacaoDeTrabalho;
    private Integer fkEmpresa;

    @Override
    public void run() {
        System.out.println("ESTAÇÃO DE TRABALHO: " + estacaoDeTrabalho);
        System.out.println("INICIANDO MONITORAMENTO DE SITES");
        Computador computadorSqlServer = new Computador();
        List<Computador> computadores = computadorSqlServer.autenticadorComputador(user, senha, "SqlServer");
        computadorSqlServer.setEstacao_de_trabalho(estacaoDeTrabalho);
        Logger loggerAvisos = new Logger("logs/Avisos da Aplicação");
        Logger loggerAlertas = new Logger("logs/Alertas de Captura");
        Logger loggerMonitoramento = new Logger("logs/Monitoramento do Dispositivo");
        Looca looca = new Looca();
        Sistema sistema = looca.getSistema();
        MemoriaRam ram = new MemoriaRam();
        SitesBloqueados sitesBloqueados = new SitesBloqueados();
        sitesBloqueados.setFkEmpresa(fkEmpresa);
        AutomacaoDeAlertasSlack alertasSlack = new AutomacaoDeAlertasSlack();
        while(true){
            List<Janela> listaProcessos = ram.buscarProcessos();
            List<SitesBloqueados> listaSitesBloqueados = sitesBloqueados.getSitesBloqueados();
            // Thread de fechar  sites bloqueados
            for (Janela processo : listaProcessos) {
                for (SitesBloqueados site : listaSitesBloqueados) {
                    if (processo.getTitulo().toLowerCase().contains(site.getNome().toLowerCase())) {
                        System.out.println("Você não tem permissão para acessar o site %s, portanto fechamos seu navegador".formatted(site.getNome()));
                        loggerAvisos.gerarLog(String.format("🚫 Site bloqueado acessado: %s. Processo encerrado.", site.getNome()));
                        Long pidProcesso = processo.getPid();
                        PowerShellResponse response;
                        if (sistema.getSistemaOperacional().equalsIgnoreCase("Windows")) {
                            response = PowerShell.executeSingleCommand("taskkill /PID %d".formatted(pidProcesso));
                        } else {
                            response = PowerShell.executeSingleCommand("kill %d".formatted(pidProcesso));
                        }
                        String mensagem = "Nova ocorrência\nEstação de trabalho: " + computadorSqlServer.getEstacao_de_trabalho() + "\nTipo da ocorrência: Acesso a site bloqueado\n Site acessado: " + site.getUrl();
                        alertasSlack.enviarAlertaSlack(mensagem);
                        break;
                    }
                }
            }
        }
    }

    public String getEstacaoDeTrabalho() {
        return estacaoDeTrabalho;
    }

    public void setEstacaoDeTrabalho(String estacaoDeTrabalho) {
        this.estacaoDeTrabalho = estacaoDeTrabalho;
    }

    public Integer getFkEmpresa() {
        return fkEmpresa;
    }

    public void setFkEmpresa(Integer fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
    }
}
