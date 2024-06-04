package ui;

import com.github.britooo.looca.api.core.Looca;
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
        DiscoModel discoModel = new DiscoModel();
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

                List<RamModel> ramdb = ramModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<DiscoModel> discosdb = discoModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                List<CpuModel> cpudb = cpuModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");

                if (ramdb.isEmpty()) {
                    ramModel.inserirHardware(computadorSqlServer.getId_Computador(), ram.buscarTotalDeRam(), "sqlserver");
                    ramModel.inserirHardware(computadorMySql.getId_Computador(), ram.buscarTotalDeRam(), "mysql");
                    ramdb = ramModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                }

                ramModel.setId_hardware(ramdb.get(0).getId_hardware());
                ramModel.setNome_hardware(ramdb.get(0).getNome_hardware());
                ramModel.setCapacidade_total(ramdb.get(0).getCapacidade_total());
                ramModel.setFk_computador(ramdb.get(0).getFk_computador());

                if (discosdb.isEmpty()) {
                    for (Double ssdFor : ssd.buscarTotalDeEspaco()) {
                        discoModel.inserirHardware(computadorSqlServer.getId_Computador(), ssdFor, "sqlserver");
                        discoModel.inserirHardware(computadorMySql.getId_Computador(), ssdFor, "mysql");
                    }

                    discosdb = discoModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
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
                    cpudb = cpuModel.autenticarHardware(computadorSqlServer.getId_Computador(), "sqlserver");
                }

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
                            System.out.println("O site está bloqueado, portanto estamos encerrando o processo");
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

                registros.inserirCpu(usoCpu, cpuModel.getId_hardware(), computadorSqlServer.getId_Computador());
                registros.inserirRam(usoRam, ramModel.getId_hardware(),
                        computadorSqlServer.getId_Computador());

                Integer i = 0;
                for (DiscoModel discosModelLista : computadorSqlServer.getListaDiscos()) {
                    registros.inserirDisco(usoSsd.get(i), discosModelLista.getId_hardware(),
                            computadorSqlServer.getId_Computador());
                    i++;
                }

                System.out.println("Inserido com sucesso");
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