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
import model.Computador;
import notificacoes.AutomacaoDeAlertasSlack;

import java.util.List;
import java.util.Scanner;

public class InterfaceCliente {
    public static void main(String[] args) {
        String statusDaVerificacao = "";
        SitesBloqueados sitesBloqueados = new SitesBloqueados();
        Computador computador = new Computador();
        Cpu cpu = new Cpu();
        MemoriaRam ram = new MemoriaRam();
        DiscoRigido ssd = new DiscoRigido();
        Looca looca = new Looca();
        Sistema sistema = looca.getSistema();
        AutomacaoDeAlertasSlack alertasSlack = new AutomacaoDeAlertasSlack();

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
                Double usoSsd = ssd.buscarEspacoOcupado().get(0);

                Registros registros = new Registros();
                registros.inserirRegistros(
                        usoRam,
                        usoCpu,
                        ram.buscarQtdProcessos(),
                        usoSsd,
                        computador.getId_Computador()
                );
                System.out.println("Inserido com sucesso");

                // Verifica se deve enviar alerta ao Slack
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
