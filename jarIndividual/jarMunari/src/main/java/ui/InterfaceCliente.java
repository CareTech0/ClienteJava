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
import infraestrutura.RedeLocal;
import model.Computador;

import java.io.BufferedReader;
import java.io.Console;
import java.util.ArrayList;
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

        do{
            System.out.println("--------------------------------------------------------");
            System.out.println("||||||||||||||||     Login no Client     |||||||||||||||");
            System.out.println("--------------------------------------------------------");

            Scanner input = new Scanner(System.in);
            System.out.println("User: ");
            String user = input.nextLine();
            System.out.println("Senha: ");
            String senha = input.nextLine();

            List<Computador> computadores = computador.autenticadorComputador(user, senha);

            if (computadores.size() == 1){
                statusDaVerificacao = "Login Realizado com Sucesso!!!";
                sitesBloqueados.setFkEmpresa(computadores.get(0).getFk_empresa());
                computador.setId_Computador(computadores.get(0).getId_Computador());
            } else {
                statusDaVerificacao = "Login ou senha inválidos!!!";
            }

            System.out.println(statusDaVerificacao);
        } while(!statusDaVerificacao.equals("Login Realizado com Sucesso!!!"));

        try {
            System.out.println("Sistema operacional: " + sistema.getSistemaOperacional());
            while (true) {

                //System.out.println("Ram em uso no momento: %.1f".formatted(ram.buscarUsoDeRam()));

                //System.out.println("Total de espaço nos discos rigidos: " + ssd.buscarTotalDeEspaco());
                //System.out.println("Espeço disponível nos discos rígidos: " + ssd.buscarEspacoLivre());
                ssd.buscarTotalDeEspaco();
                ssd.buscarEspacoLivre();


                //System.out.println("Informações da CPU: ");
                //System.out.println(cpu.buscarUsoCpu());
                //System.out.println("Teste comando no terminal");

                //System.out.println("Lista de processos: ");
                //System.out.println(ram.buscarProcessos());

                List<Janela> listaProcessos = ram.buscarProcessos();
                List<SitesBloqueados> listaSitesBloqueados = sitesBloqueados.getSitesBloqueados();

                for(int processo = 0; processo < listaProcessos.size(); processo++){
                    for(int site = 0; site < listaSitesBloqueados.size(); site++){
                        if(listaProcessos.get(processo).getTitulo().toLowerCase().contains(listaSitesBloqueados.get(site).getNome().toLowerCase())){
                            System.out.println("O site está bloquado, portanto estamos encerrando o processo");
                            Long pidProcesso = listaProcessos.get(processo).getPid();
                            if(sistema.getSistemaOperacional().equalsIgnoreCase("Windows")){
                                PowerShellResponse response = PowerShell.executeSingleCommand("taskkill /PID %d\n".formatted(pidProcesso));
                            }else{
                                PowerShellResponse response = PowerShell.executeSingleCommand("kill %d\n".formatted(pidProcesso));
                            }
                            break;
                        }
                    }
                }

                Double usoRam = ram.buscarUsoDeRam();
                Double usoCpu = cpu.buscarUsoCpu();
                Double usoSsd = ssd.buscarEspacoOcupado().get(0);

                System.out.println(ram.buscarUsoRamPercentual());
                if(ram.buscarUsoRamPercentual() > 70){
                    System.out.println("Entrei no if");
                    computador.notificacao("Você tem programas demais aberto, tente fechar alguns",
                            "Alerta de PC lento");
                }

                Registros registros = new Registros();
                registros.inserirRegistros(
                    usoRam,
                    usoCpu,
                    ram.buscarQtdProcessos(),
                    usoSsd,
                    computador.getId_Computador()
                );
                System.out.println("Inserido com sucesso");
                Thread.sleep(15000);
        }
    }
    catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    }
}
