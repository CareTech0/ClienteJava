package ui;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import dao.Registros;
import dao.SitesBloqueados;
import infraestrutura.Cpu;
import infraestrutura.DiscoRigido;
import infraestrutura.MemoriaRam;
import model.*;
import notificacoes.AutomacaoDeAlertasSlack;

import java.util.List;
import java.util.Scanner;

public class InterfaceCliente {
    public static void main(String[] args) {
        String statusDaVerificacao = "";
        SitesBloqueados sitesBloqueados = new SitesBloqueados();

        //Models
        Computador computador = new Computador();
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

        Scanner input = new Scanner(System.in);

        do {
            System.out.println("--------------------------------------------------------");
            System.out.println("||||||||||||||||     Login no Client     |||||||||||||||");
            System.out.println("--------------------------------------------------------");

            System.out.println("User: ");
            String user = input.nextLine();
            System.out.println("Senha: ");
            String senha = input.nextLine();

            List<Computador> computadores = computador.autenticadorComputador(user, senha);

            if (computadores.size() == 1) {
                statusDaVerificacao = "Login Realizado com Sucesso!!!";

                sitesBloqueados.setFkEmpresa(computadores.get(0).getFk_empresa());
                computador.setId_Computador(computadores.get(0).getId_Computador());

                List<RamModel> ramdb = ramModel.autenticarHardware(computador.getId_Computador());
                List<DiscoModel> discosdb = discoModel.autenticarHardware(computador.getId_Computador());
                List<CpuModel> cpudb = cpuModel.autenticarHardware(computador.getId_Computador());

                if (ramdb.isEmpty()){
                    ramModel.inserirHardware(computador.getId_Computador(), ram.buscarTotalDeRam());
                    ramdb = ramModel.autenticarHardware(computador.getId_Computador());
                }

                ramModel.setId_hardware(ramdb.get(0).getId_hardware());
                ramModel.setNome_hardware(ramdb.get(0).getNome_hardware());
                ramModel.setCapacidade_total(ramdb.get(0).getCapacidade_total());
                ramModel.setFk_computador(ramdb.get(0).getFk_computador());

                if (discosdb.isEmpty()){
                    for (Double ssdFor: ssd.buscarTotalDeEspaco()){
                        discoModel.inserirHardware(computador.getId_Computador(), ssdFor);
                    }

                    discosdb = discoModel.autenticarHardware(computador.getId_Computador());
                }

                for (Hardware discoFor: discosdb){
                    discoModel.setId_hardware(discoFor.getId_hardware());
                    discoModel.setCapacidade_total(discoFor.getCapacidade_total());
                    discoModel.setNome_hardware(discoFor.getNome_hardware());
                    discoModel.setFk_computador(discoFor.getFk_computador());
                    computador.adicionarDisco(discoModel);
                }


                if (cpudb.isEmpty()){
                    cpuModel.inserirHardware(computador.getId_Computador(), cpu.buscarUsoCpu());
                    cpudb = cpuModel.autenticarHardware(computador.getId_Computador());
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
                Double usoCpu = cpu.buscarUsoCpu();
                List<Double> usoSsd = ssd.buscarEspacoOcupado();

                registros.inserirCpu(usoCpu, cpuModel.getId_hardware(), computador.getId_Computador());
                registros.inserirRam(usoRam, ram.buscarQtdProcessos(), ramModel.getId_hardware(), computador.getId_Computador());

                Integer i = 0;
                for (DiscoModel discosModelLista: computador.getListaDiscos()){
                    registros.inserirDisco(usoSsd.get(i), discosModelLista.getId_hardware(), computador.getId_Computador());
                    i++;
                }

                System.out.println("Inserido com sucesso");

                //Verifica se deve enviar alerta ao Slack
                String alertaMessage = verificarUso(usoCpu, usoRam, ssd.buscarEspacoOcupado());
                if (alertaMessage != null) {
                    alertasSlack.enviarAlertaSlack(alertaMessage);
                }

                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String verificarUso(Double usoCpu, Double usoRam, List<Double> usoDisco) {
        if (usoCpu > 80.0 || usoRam > 80.0 || usoDisco.get(0) > 80.0 || usoDisco.get(1) > 80.0) {
            return "Nova ocorrência, acesse o painel de monitoramento para mais informações";
        }
        return null;
    }
}
