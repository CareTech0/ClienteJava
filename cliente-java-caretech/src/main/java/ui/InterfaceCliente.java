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
import infraestrutura.Cpu;
import infraestrutura.DiscoRigido;
import infraestrutura.MemoriaRam;
import infraestrutura.RedeLocal;
import model.*;
import notificacoes.AutomacaoDeAlertasSlack;
import repository.ConexaoSqlServer;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class InterfaceCliente {
    private static long ultimoEnvioSlack = 0;

    public static void main(String[] args) {
        String statusDaVerificacao = "";
        SitesBloqueados sitesBloqueados = new SitesBloqueados();

        //Models
        Computador computadorSqlServer = new Computador();
        Computador computadorMySql = new Computador();
        CpuModel cpuModel = new CpuModel();
        CpuModel cpuModelMysql = new CpuModel();
        RamModel ramModelMysql = new RamModel();
        DiscoModel discoModel = new DiscoModel();
        DiscoModel discoModelMysql = new DiscoModel();
        RamModel ramModel = new RamModel();

        //Infraestrutura
        Cpu cpu = new Cpu();
        MemoriaRam ram = new MemoriaRam();
        DiscoRigido ssd = new DiscoRigido();
        Looca looca = new Looca();
        Sistema sistema = looca.getSistema();
        AutomacaoDeAlertasSlack alertasSlack = new AutomacaoDeAlertasSlack();

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
            List<Computador> computadoresMySql = computadorMySql.autenticadorComputador(user, senha, "mysql");
            if (computadores.size() == 1) {
                statusDaVerificacao = "Login Realizado com Sucesso!!!";

                sitesBloqueados.setFkEmpresa(computadores.get(0).getFk_empresa());
                computadorSqlServer.setId_Computador(computadores.get(0).getId_Computador());
                computadorMySql.setId_Computador(computadoresMySql.get(0).getId_Computador());

                //Models SQL server
                List<RamModel> ramdb = ramModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<DiscoModel> discosdb = discoModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<CpuModel> cpudb = cpuModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                //Models mysql
                List<RamModel> ramMysql = ramModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                List<CpuModel> cpuMysql = cpuModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                List<DiscoModel> discoMysql = discoModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");

                if (ramdb.isEmpty()) {
                    ramModel.inserirHardware(computadorSqlServer.getId_Computador(), ram.buscarTotalDeRam(), "sqlserver");
                    ramModel.inserirHardware(computadorMySql.getId_Computador(), ram.buscarTotalDeRam(), "mysql");
                    ramMysql = ramModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                    ramdb = ramModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
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
                    for (Double ssdFor : ssd.buscarTotalDeEspaco()) {
                        discoModel.inserirHardware(computadorSqlServer.getId_Computador(), ssdFor, "sqlserver");
                        discoModel.inserirHardware(computadorMySql.getId_Computador(), ssdFor, "mysql");
                    }

                    discoMysql = discoModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                    discosdb = discoModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                }

                for (Hardware discoFor : discoMysql) {

                    discoModelMysql.setId_hardware(discoFor.getId_hardware());
                    discoModelMysql.setCapacidade_total(discoFor.getCapacidade_total());
                    discoModelMysql.setNome_hardware(discoFor.getNome_hardware());
                    discoModelMysql.setFk_computador(discoFor.getFk_computador());
                    computadorMySql.adicionarDisco(discoModel);
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
                    cpuModel.inserirHardware(computadorMySql.getId_Computador(), cpu.buscarUsoCpu(), "mysql");
                    cpuMysql = cpuModelMysql.autenticarHardware(computadorMySql.getId_Computador(), "mysql");
                    cpudb = cpuModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
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
            }

            System.out.println(statusDaVerificacao);
        } while (!statusDaVerificacao.equals("Login Realizado com Sucesso!!!"));

        try {
            System.out.println("Sistema operacional: " + sistema.getSistemaOperacional());
            while (true) {
                ssd.buscarTotalDeEspaco();
                ssd.buscarEspacoLivre();

                List<Janela> listaProcessos = ram.buscarProcessos();
                List<SitesBloqueados> listaSitesBloqueados = sitesBloqueados.getSitesBloqueados();

                // Thread de fechar  sites bloqueados
                for (Janela processo : listaProcessos) {
                    for (SitesBloqueados site : listaSitesBloqueados) {
                        if (processo.getTitulo().toLowerCase().contains(site.getNome().toLowerCase())) {
                            System.out.println("Você não tem permissão para acessar o site %s, portanto fechamos ele".formatted(site.getNome()));
                            Long pidProcesso = processo.getPid();
                            PowerShellResponse response;
                            if (sistema.getSistemaOperacional().equalsIgnoreCase("Windows")) {
                                response = PowerShell.executeSingleCommand("taskkill /PID %d".formatted(pidProcesso));
                            } else {
                                response = PowerShell.executeSingleCommand("kill %d".formatted(pidProcesso));
                            }
                            break;
                        }
                    }
                }

                Double usoRam = ram.buscarUsoDeRam();
                Double totalRam = ram.buscarTotalDeRam();
                Double totalDisco1 = ssd.buscarTotalDeEspaco().get(0);
                Double usoCpu = cpu.buscarUsoCpu();
                List<Double> usoSsd = ssd.buscarEspacoOcupado();



                for(int i = 0; i < computadorMySql.getListaDiscos().size(); i++){
                    registros.inserirDisco(usoSsd.get(i), computadorMySql.getListaDiscos().get(i).getId_hardware(),
                            "mysql");
                }
                registros.inserirCpu(usoCpu, cpuModelMysql.getId_hardware(), "mysql");
                registros.inserirRam(usoRam, ramModelMysql.getId_hardware(),
                        "mysql");

                System.out.println("Registros inseridos localmente com sucesso.");

                registros.inserirCpu(usoCpu, cpuModel.getId_hardware(), "sqlserver");
                registros.inserirRam(usoRam, ramModel.getId_hardware(),
                        "sqlserver");

                for (int i = 0; i < computadorSqlServer.getListaDiscos().size(); i++) {
                    registros.inserirDisco(usoSsd.get(i), computadorSqlServer.getListaDiscos().get(i).getId_hardware(),
                            "sqlserver");
                }

                System.out.println("Registros inseridos na nuvem com sucesso");
                RedeLocal rede = new RedeLocal();
                rede.buscarVelocidadeRede();

                long currentTime = System.currentTimeMillis();
                long diffMinutes = (currentTime - ultimoEnvioSlack) / (60 * 1000);

                if (diffMinutes >= 10) {
                    String alertaMessage = verificarUso(usoCpu, usoRam, totalRam, totalDisco1, usoSsd);
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

    private static String verificarUso(Double usoCpu, Double totalRam, Double usoRam, Double totalDisco1, List<Double> usoDisco) {
        // Verificar se os valores de total não são zero para evitar divisão por zero
        if (totalRam == 0 || totalDisco1 == 0) {
            return "Erro: Total de RAM ou Disco não pode ser zero.";
        }

        // Verificar se a lista usoDisco tem pelo menos um elemento
        if (usoDisco == null || usoDisco.size() < 1) {
            return "Erro: Lista usoDisco deve conter pelo menos um elemento.";
        }

        Double porcentualUsoRam = (usoRam / totalRam) * 100;
        Double porcentualUsoDisco1 = (usoDisco.get(0) / totalDisco1) * 100;

        StringBuilder notificacao = new StringBuilder();

        notificacao.append("Novo alerta\n");

        if (usoCpu > 80.0) {
            notificacao.append("Estado Crítico! Uso da CPU acima de 80%: ")
                    .append(String.format("%.2f", usoCpu)).append("%\n");
        } else if (usoCpu > 60) {
            notificacao.append("Cuidado! Uso da CPU acima de 60%: ")
                    .append(String.format("%.2f", usoCpu)).append("%\n");
        }

        if (porcentualUsoRam > 80.0) {
            notificacao.append("Estado Crítico! RAM utilizada: ")
                    .append(String.format("%.2f", porcentualUsoRam)).append("%\n");
        } else if (porcentualUsoRam > 70) {
            notificacao.append("Cuidado! RAM utilizada: ")
                    .append(String.format("%.2f", porcentualUsoRam)).append("%\n");
        }

        if (porcentualUsoDisco1 > 80.0) {
            notificacao.append("Estado Crítico! Espaço consumido no Disco 1: ")
                    .append(String.format("%.2f", porcentualUsoDisco1)).append("%\n");
        } else if (porcentualUsoDisco1 > 70) {
            notificacao.append("Cuidado! Espaço consumido no Disco 1: ")
                    .append(String.format("%.2f", porcentualUsoDisco1)).append("%\n");
        }

        if (notificacao.isEmpty()) {
            return null;
        } else {
            return notificacao.toString();
        }
    }
}