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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.awt.Color;




public class InterfaceCliente {

    public static void main(String[] args) {
        Color corPersonalizada = new Color(50, 146, 255);
        String statusDaVerificacao = "";
        SitesBloqueados sitesBloqueados = new SitesBloqueados();
        Computador computador = new Computador();
        Cpu cpu = new Cpu();
        MemoriaRam ram = new MemoriaRam();
        DiscoRigido ssd = new DiscoRigido();
        Looca looca = new Looca();
        Sistema sistema = looca.getSistema();

        String mensagemErro = "\033[34m\033[1m-----------------------------------------------------------------\n"
                + " Login ou senha inválidos. Por favor, verifique suas credenciais\n"
                + "-----------------------------------------------------------------\033[0m";

        String mensagemSucesso = "\033[34m\033[1m-----------------------------------------------------------------\n"
                + "                   Login realizado com sucesso!\n"
                + "-----------------------------------------------------------------\033[0m";

        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m-----------------------------------------------------------------\033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m      _____                    _____              _      \033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m     /  __ \\                  |_   _|            | |     \033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m    | /   \\/  __ _  _ __  ___   | |    ___   ___ | |__   \033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m    | |     / _` || '__| / _ \\  | |   / _ \\ / __|| '_ \\  \033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m    | \\__/\\| (_| || |   |  __/  | |  |  __/| (__ | | | | \033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m     \\____/ \\__,_||_|    \\___|  \\_/   \\___| \\___||_| |_|\033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m-----------------------------------------------------------------\033[0m");
        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m      Bem-vindo ao sistema de login do Cliente! \uD83D\uDCBB\033[0m");

        System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m Entre em sua conta inserindo os dados nos campos abaixo:\033[0m");


        do{

            Scanner input = new Scanner(System.in);
            System.out.print("\nInforme o usuário: ");
            String user = input.nextLine();

            System.out.print("Informe a senha: ");
            String senha = input.nextLine();

            List<Computador> computadores = computador.autenticadorComputador(user, senha);

            if (computadores.size() == 1){
                statusDaVerificacao = mensagemSucesso;
                sitesBloqueados.setFkEmpresa(computadores.get(0).getFk_empresa());
                computador.setId_Computador(computadores.get(0).getId_Computador());
            } else {
                statusDaVerificacao = mensagemErro;
            }

            System.out.println(statusDaVerificacao);
        } while(!statusDaVerificacao.equals(mensagemSucesso));


        try {
            System.out.println("\033[34m\033[1;34mSistema operacional:\033[0m " + sistema.getSistemaOperacional());
            while (true) {

               ssd.buscarTotalDeEspaco();
               ssd.buscarEspacoLivre();

                List<Janela> listaProcessos = ram.buscarProcessos();
                List<SitesBloqueados> listaSitesBloqueados = sitesBloqueados.getSitesBloqueados();

                for(int processo = 0; processo < listaProcessos.size(); processo++){
                    for(int site = 0; site < listaSitesBloqueados.size(); site++){
                        if(listaProcessos.get(processo).getTitulo().toLowerCase().contains(listaSitesBloqueados.get(site).getNome().toLowerCase())){
                            String siteBloqueado = listaSitesBloqueados.get(site).getNome();
                            LocalDateTime horaAtual = LocalDateTime.now();
                            String horaFormatada = horaAtual.format(DateTimeFormatter.ofPattern("HH:mm"));

                            System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m-----------------------------------------------------------------\033[0m");
                            System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m      Tentativa de acessar o site '" + siteBloqueado + "' às " + horaFormatada +".\033[0m");
                            System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m          \u26D4 Estamos encerrando o processo. \u26D4              \033[0m");
                            System.out.println("\033[1;38;2;" + corPersonalizada.getRed() + ";" + corPersonalizada.getGreen() + ";" + corPersonalizada.getBlue() + "m-----------------------------------------------------------------\033[0m");
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

                Registros registros = new Registros();
                registros.inserirRegistros(
                    usoRam,
                    usoCpu,
                    ram.buscarQtdProcessos(),
                    usoSsd,
                    computador.getId_Computador()
                );
                LocalDateTime horaAtual = LocalDateTime.now();
                String horaFormatada = horaAtual.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println("Inserido com sucesso às " + horaFormatada);
                Thread.sleep(3000);
        }
    }
    catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    }


}

