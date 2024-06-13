package ui;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.rede.Rede;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import dao.Registros;
import dao.SitesBloqueados;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import infraestrutura.Cpu;
import infraestrutura.DiscoRigido;
import infraestrutura.MemoriaRam;
import infraestrutura.RedeLocal;
import logs.Logger;
import model.*;
import notificacoes.AutomacaoDeAlertasSlack;
import repository.ConexaoSqlServer;
import threads.BloquearSites;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class InterfaceCliente {
    private static long ultimoEnvioSlack = 0;

    public static void main(String[] args) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        String statusDaVerificacao = "";
        SitesBloqueados sitesBloqueados = new SitesBloqueados();
        Logger loggerAvisos = new Logger("logs/Avisos da Aplicação");
        Logger loggerAlertas = new Logger("logs/Alertas de Captura");
        Logger loggerMonitoramento = new Logger("logs/Monitoramento do Dispositivo");

        //Models
        Computador computadorSqlServer = new Computador();
        Computador computadorMySql = new Computador();
        CpuModel cpuModel = new CpuModel();
        CpuModel cpuModelMysql = new CpuModel();
        RamModel ramModelMysql = new RamModel();
        DiscoModel discoModel = new DiscoModel();
        DiscoModel discoModelMysql = new DiscoModel();
        RamModel ramModel = new RamModel();
        RedeModel redeModel = new RedeModel();
        RedeModel redeModelMysql = new RedeModel();

        //Infraestrutura
        Cpu cpu = new Cpu();
        MemoriaRam ram = new MemoriaRam();
        DiscoRigido ssd = new DiscoRigido();
        Looca looca = new Looca();
        Sistema sistema = looca.getSistema();
        AutomacaoDeAlertasSlack alertasSlack = new AutomacaoDeAlertasSlack();
        RedeLocal rede = new RedeLocal();

        //Dao
        Registros registros = new Registros();
        ConexaoSqlServer conSqlServer = new ConexaoSqlServer();

        Scanner input = new Scanner(System.in);

        do {
            System.out.println("--------------------------------------------------------");
            System.out.println("||||||||||||||||     Login no Client     |||||||||||||||");
            System.out.println("--------------------------------------------------------");

            System.out.println("User: ");
            String user = input.nextLine();
            System.out.println("Senha: ");
            String senha = input.nextLine();
            List<Computador> computadores = computadorSqlServer.autenticadorComputador(user, senha, "SqlServer");
            if (computadores.size() == 1) {
                statusDaVerificacao = "Login Realizado com Sucesso!!!";

                loggerAvisos.gerarLog("✅ Aplicação Iniciada ✅");
                loggerAlertas.gerarLog("✅ Aplicação Iniciada ✅");
                loggerMonitoramento.gerarLog("✅ Aplicação Iniciada ✅");
                loggerAvisos.gerarLog(String.format("✅ Usuário %s fez login no sistema com a senha %s.", user, senha));
                loggerAlertas.gerarLog(String.format("✅ Usuário %s fez login no sistema com a senha %s.", user, senha));
                loggerMonitoramento.gerarLog(String.format("✅ Usuário %s fez login no sistema com a senha %s.", user, senha));

                List<Computador> computadoresMySql = computadorMySql.autenticadorComputador(user, senha, "mysql");
                if (computadoresMySql.size() < 1) {
                    registros.inserirComputador(user, senha, "mysql");
                    computadoresMySql = computadorMySql.autenticadorComputador(user, senha, "mysql");
                }
                sitesBloqueados.setFkEmpresa(computadores.get(0).getFk_empresa());
                computadorSqlServer.setId_Computador(computadores.get(0).getId_Computador());
                computadorSqlServer.setEstacao_de_trabalho(computadores.get(0).getEstacao_de_trabalho());
                computadorSqlServer.setFk_empresa(computadores.get(0).getFk_empresa());
                computadorMySql.setId_Computador(computadoresMySql.get(0).getId_Computador());

                //Models SQL server
                List<RamModel> ramdb = ramModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<DiscoModel> discosdb = discoModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<CpuModel> cpudb = cpuModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<RedeModel> rededb = redeModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                //Models mysql
                List<RamModel> ramMysql = ramModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                List<CpuModel> cpuMysql = cpuModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                List<DiscoModel> discoMysql = discoModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                List<RedeModel> redeMysql = redeModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");

                if (rededb.isEmpty()) {
                    redeModel.inserirHardware(computadorSqlServer.getId_Computador(), 0.0, "sqlserver");
                    rededb = redeModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                    System.out.println("Rede inserida com sucesso");
                }

                if(redeMysql.isEmpty()){
                    redeModel.inserirHardware(computadorMySql.getId_Computador(), 0.0, "mysql");
                    redeMysql = redeModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");

                }

                redeModelMysql.setId_hardware(redeMysql.get(0).getId_hardware());
                redeModelMysql.setNome_hardware(redeMysql.get(0).getNome_hardware());
                redeModelMysql.setCapacidade_total(redeMysql.get(0).getCapacidade_total());
                redeModelMysql.setFk_computador(redeMysql.get(0).getFk_computador());

                redeModel.setId_hardware(rededb.get(0).getId_hardware());
                redeModel.setNome_hardware(rededb.get(0).getNome_hardware());
                redeModel.setCapacidade_total(rededb.get(0).getCapacidade_total());
                redeModel.setFk_computador(rededb.get(0).getFk_computador());


                if (ramdb.isEmpty()) {
                    ramModel.inserirHardware(computadorSqlServer.getId_Computador(), ram.buscarTotalDeRam(), "sqlserver");
                    ramdb = ramModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                }

                if(ramMysql.isEmpty()){
                    ramModel.inserirHardware(computadorMySql.getId_Computador(), ram.buscarTotalDeRam(), "mysql");
                    ramMysql = ramModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                }

                //Adicionando os atibutos ao objeto atrelado ao banco MySQL
                ramModelMysql.setId_hardware(ramMysql.get(0).getId_hardware());
                ramModelMysql.setNome_hardware(ramMysql.get(0).getNome_hardware());
                ramModelMysql.setCapacidade_total(ramMysql.get(0).getCapacidade_total());
                ramModelMysql.setFk_computador(ramMysql.get(0).getFk_computador());

                //Adicionando os atributos ao objeto atrelado ao banco SQL-Server
                ramModel.setId_hardware(ramdb.get(0).getId_hardware());
                ramModel.setNome_hardware(ramdb.get(0).getNome_hardware());
                ramModel.setCapacidade_total(ramdb.get(0).getCapacidade_total());
                ramModel.setFk_computador(ramdb.get(0).getFk_computador());

                if (discosdb.isEmpty()) {
                    discoModel.inserirHardware(computadorSqlServer.getId_Computador(), ssd.buscarTotalDeEspaco().get(0), "sqlserver");
                    discosdb = discoModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                }

                if(discoMysql.isEmpty()){
                    discoModel.inserirHardware(computadorMySql.getId_Computador(), ssd.buscarTotalDeEspaco().get(0), "mysql");
                    discoMysql = discoModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");

                }

                for (Hardware discoFor : discoMysql) {
                    discoModelMysql.setId_hardware(discoFor.getId_hardware());
                    discoModelMysql.setCapacidade_total(discoFor.getCapacidade_total());
                    discoModelMysql.setNome_hardware(discoFor.getNome_hardware());
                    discoModelMysql.setFk_computador(discoFor.getFk_computador());
                    computadorMySql.adicionarDisco(discoModelMysql);
                }

                for (Hardware discoFor : discosdb) {
                    discoModel.setId_hardware(discoFor.getId_hardware());
                    discoModel.setCapacidade_total(discoFor.getCapacidade_total());
                    discoModel.setNome_hardware(discoFor.getNome_hardware());
                    discoModel.setFk_computador(discoFor.getFk_computador());
                    computadorSqlServer.adicionarDisco(discoModel);
                }


                if (cpudb.isEmpty()) {
                    cpuModel.inserirHardware(computadorSqlServer.getId_Computador(), cpu.buscarUsoCpu(), "sqlserver");
                    cpudb = cpuModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                }

                if(cpuMysql.isEmpty()){
                    cpuModel.inserirHardware(computadorMySql.getId_Computador(), cpu.buscarUsoCpu(), "mysql");
                    cpuMysql = cpuModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                }

                cpuModelMysql.setId_hardware(cpuMysql.get(0).getId_hardware());
                cpuModelMysql.setFk_computador(cpuMysql.get(0).getFk_computador());
                cpuModelMysql.setCapacidade_total(cpuMysql.get(0).getCapacidade_total());
                cpuModelMysql.setNome_hardware(cpuMysql.get(0).getNome_hardware());

                cpuModel.setId_hardware(cpudb.get(0).getId_hardware());
                cpuModel.setFk_computador(cpudb.get(0).getFk_computador());
                cpuModel.setCapacidade_total(cpudb.get(0).getCapacidade_total());
                cpuModel.setNome_hardware(cpudb.get(0).getNome_hardware());

            } else {
                statusDaVerificacao = "Login ou senha inválidos!!!";
                loggerAvisos.gerarLog("❌ Erro ao acessar: login ou senha inválidos! ❌");
                loggerAlertas.gerarLog("❌ Erro ao acessar: login ou senha inválidos! ❌");
                loggerMonitoramento.gerarLog("❌ Erro ao acessar: login ou senha inválidos! ❌");
            }

            System.out.println(statusDaVerificacao);
        } while (!statusDaVerificacao.equals("Login Realizado com Sucesso!!!"));

        try {

            System.out.println("Sistema operacional: " + sistema.getSistemaOperacional());

            loggerAvisos.gerarLog(String.format("🖥️ Sistema operacional utilizado para captura: %s", sistema.getSistemaOperacional()));
            loggerAlertas.gerarLog(String.format("🖥️ Sistema operacional utilizado para captura: %s", sistema.getSistemaOperacional()));
            loggerMonitoramento.gerarLog(String.format("🖥️ Sistema operacional utilizado para captura: %s", sistema.getSistemaOperacional()));

            BloquearSites bloqueioDeSites = new BloquearSites();
            bloqueioDeSites.setEstacaoDeTrabalho(computadorSqlServer.getEstacao_de_trabalho());
            bloqueioDeSites.setFkEmpresa(computadorSqlServer.getFk_empresa());
            bloqueioDeSites.start();
            while (true) {
                ssd.buscarTotalDeEspaco();
                ssd.buscarEspacoLivre();

                Double usoRam = ram.buscarUsoDeRam();
                Double totalRam = ram.buscarTotalDeRam();
                Double totalDisco1 = ssd.buscarTotalDeEspaco().get(0);
                Double usoCpu = cpu.buscarUsoCpu();
                List<Double> usoSsd = ssd.buscarEspacoOcupado();



                registros.inserirDisco(usoSsd.get(0), computadorMySql.getListaDiscos().get(0).getId_hardware(),
                            "mysql");

                registros.inserirCpu(usoCpu, cpuModelMysql.getId_hardware(), "mysql");
                registros.inserirRam(usoRam, ramModelMysql.getId_hardware(),
                        "mysql");

                // Crie um objeto SpeedTestSocket
                SpeedTestSocket speedTestSocket = new SpeedTestSocket();

                // Variável para armazenar a velocidade de download em Mbps
                double downloadSpeedMbps = 0;

                // Adicione um listener para capturar eventos de teste de velocidade
                speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                    @Override
                    public void onCompletion(SpeedTestReport report) {
                        // Quando o teste for concluído, obtenha a velocidade de download em bits/s
                        double downloadSpeed = report.getTransferRateBit().doubleValue();

                        // Converta a velocidade de bits por segundo para megabits por segundo (Mbps)
                        double downloadSpeedMbps = downloadSpeed / 1_000_000.0;
                        registros.inserirRede(downloadSpeedMbps, redeModelMysql.getId_hardware(), "mysql");
                        registros.inserirRede(downloadSpeedMbps, redeModel.getId_hardware(), "sqlserver");
                        //System.out.println(downloadSpeedMbps);
                    }

                    @Override
                    public void onError(SpeedTestError speedTestError, String errorMessage) {
                        System.err.println("Erro: " + errorMessage);
                    }

                    @Override
                    public void onProgress(float percent, SpeedTestReport report) {
                        // Progresso do teste de velocidade (opcional)
                    }
                });

                // Inicie o teste de download com um arquivo de teste
                String fileUrl = "https://link.testfile.org/PDF10MB"; // URL do arquivo de teste
                int timeout = 10000; // Tempo limite de conexão em milissegundos
                speedTestSocket.startDownload(fileUrl, timeout);

                System.out.println("Registros inseridos localmente com sucesso.");

                registros.inserirCpu(usoCpu, cpuModel.getId_hardware(), "sqlserver");
                registros.inserirRam(usoRam, ramModel.getId_hardware(),
                        "sqlserver");

                for (int i = 0; i < computadorSqlServer.getListaDiscos().size(); i++) {
                    registros.inserirDisco(usoSsd.get(i), computadorSqlServer.getListaDiscos().get(i).getId_hardware(),
                            "sqlserver");
                }

                System.out.println("Registros inseridos na nuvem com sucesso");

                logarUsoRecursos(usoCpu, usoRam, totalRam, totalDisco1, usoSsd, loggerMonitoramento);

                long currentTime = System.currentTimeMillis();
                long diffMinutes = (currentTime - ultimoEnvioSlack) / (60 * 1000);

                if (diffMinutes >= 10) {


                    String alertaMessage = verificarUso(computadorSqlServer.getEstacao_de_trabalho(), usoCpu, totalRam, usoRam, totalDisco1, usoSsd, loggerAvisos, loggerAlertas);
                    if (alertaMessage != null) {
                        alertasSlack.enviarAlertaSlack(alertaMessage);
                    }

                    ultimoEnvioSlack = currentTime;
                }
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void logarUsoRecursos(Double usoCpu, Double usoRam, Double totalRam, Double totalDisco1, List<Double> usoDisco, Logger loggerMonitoramento) {
        loggerMonitoramento.gerarLog(String.format("🔔 Uso da CPU: %.2f%%", usoCpu));
        loggerMonitoramento.gerarLog(String.format("🔔 Uso da RAM: %.2f%% de %.2f GB", usoRam, totalRam));
        loggerMonitoramento.gerarLog(String.format("🔔 Uso do Disco 1: %.2f%% de %.2f GB", usoDisco.get(0), totalDisco1));
        loggerMonitoramento.gerarLog("");
    }

    private static String verificarUso(String estacao_de_trabalho, Double usoCpu, Double totalRam, Double usoRam, Double totalDisco1, List<Double> usoDisco, Logger loggerAvisos, Logger loggerAlertas) {

        Double porcentualUsoRam = (usoRam / totalRam) * 100;
        Double porcentualUsoDisco1 = (usoDisco.get(0) / totalDisco1) * 100;

        StringBuilder notificacao = new StringBuilder();

        if (usoCpu > 90.0 || porcentualUsoRam > 90.0 || porcentualUsoDisco1 > 90.0 || usoCpu > 80.0 || porcentualUsoRam > 80.0 || porcentualUsoDisco1 > 80.0) {
            notificacao.append("Novo ocorrência!\n");
            notificacao.append("Estação de trabalho: ").append(estacao_de_trabalho).append("\n");
        }

        if (usoCpu > 90.0 || porcentualUsoRam > 90.0 || porcentualUsoDisco1 > 90.0) {
            notificacao.append("Tipo da ocorrência: Máquina em estado crítico de funcionamento\n");
            notificacao.append("Detalhes técnicos sobre o Hardware:\n");
            loggerAvisos.gerarLog("‼️ Nova ocorrência: Máquina em estado crítico de funcionamento\n");
        } else if (usoCpu > 80.0 || porcentualUsoRam > 80.0 || porcentualUsoDisco1 > 80.0) {
            notificacao.append("Tipo da ocorrência: Máquina em estado de alerta\n");
            notificacao.append("Detalhes técnicos sobre o Hardware:\n");
            loggerAvisos.gerarLog("️❗ Nova ocorrência: Máquina em estado de alerta\n");
        }

        if (usoCpu > 90.0) {
            notificacao.append("CPU: ")
                    .append(String.format("%.2f", usoCpu)).append("%\n");
            loggerAlertas.gerarLog("⚠️ Uso da CPU acima de 90%: %.2f".formatted(usoCpu));
        } else if (usoCpu > 80.0) {
            notificacao.append("CPU: ")
                    .append(String.format("%.2f", usoCpu)).append("%\n");
            loggerAlertas.gerarLog("⚠️ Uso da CPU acima de 80%: %.2f".formatted(usoCpu));
        }

        if (porcentualUsoRam > 90.0) {
            notificacao.append("RAM: ")
                    .append(String.format("%.2f", porcentualUsoRam)).append("%\n");
            loggerAlertas.gerarLog("⚠️ Cuidado! RAM utilizada: %.2f".formatted(porcentualUsoRam));
        } else if (porcentualUsoRam > 80) {
            notificacao.append("RAM: ")
                    .append(String.format("%.2f", porcentualUsoRam)).append("%\n");
            loggerAlertas.gerarLog("⚠️ RAM utilizada: %.2f".formatted(porcentualUsoRam));
        }

        if (porcentualUsoDisco1 > 90.0) {
            notificacao.append("Disco: ")
                    .append(String.format("%.2f", porcentualUsoDisco1)).append("%\n");
            loggerAlertas.gerarLog("⚠️ Alerta! Espaço consumido no Disco: %.2f".formatted(porcentualUsoDisco1));
        } else if (porcentualUsoDisco1 > 80) {
            notificacao.append("Disco: ")
                    .append(String.format("%.2f", porcentualUsoDisco1)).append("%\n");
            loggerAlertas.gerarLog("⚠️ Espaço consumido no Disco: %.2f".formatted(porcentualUsoDisco1));
        }

        if (notificacao.isEmpty()) {
            return null;
        } else {
            return notificacao.toString();
        }
    }
}