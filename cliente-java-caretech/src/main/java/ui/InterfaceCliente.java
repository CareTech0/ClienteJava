package ui;

import dao.Registros;
import infraestrutura.Cpu;
import infraestrutura.DiscoRigido;
import infraestrutura.MemoriaRam;
import infraestrutura.RedeLocal;
import model.Computador;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class InterfaceCliente {
    public static void main(String[] args) {
        String statusDaVerificacao = "";

        do{
            System.out.println("--------------------------------------------------------");
            System.out.println("||||||||||||||||     Login no Client     |||||||||||||||");
            System.out.println("--------------------------------------------------------");

            Scanner input = new Scanner(System.in);
            System.out.println("User: ");
            String user = input.nextLine();
            System.out.println("Senha: ");
            String senha = input.nextLine();

            Computador computador = new Computador();
            List<Computador> computadores = computador.autenticadorComputador(user, senha);

            if (computadores.size() == 1){
                statusDaVerificacao = "Login Realizado com Sucesso!!!";
            } else {
                statusDaVerificacao = "Login ou senha inválidos!!!";
            }

            System.out.println(statusDaVerificacao);
        } while(!statusDaVerificacao.equals("Login Realizado com Sucesso!!!"));

        try {
            while (true) {
                MemoriaRam ram = new MemoriaRam();
                System.out.println("Ram em uso no momento: %.1f".formatted(ram.buscarUsoDeRam()));

                DiscoRigido ssd = new DiscoRigido();
                System.out.println("Total de espaço nos discos rigidos: " + ssd.buscarTotalDeEspaco());
                System.out.println("Espeço disponível nos discos rígidos: " + ssd.buscarEspacoLivre());

                RedeLocal rede = new RedeLocal();
                System.out.println("Infos de rede: ");
                rede.buscarVelocidadeRede();

                Cpu cpu = new Cpu();
                System.out.println("Informações da CPU: ");
                System.out.println(cpu.buscarUsoCpu());
                System.out.println("Teste comando no terminal");

                System.out.println("Lista de processos: ");
                System.out.println(ram.buscarProcessos());

                Registros registros = new Registros();
                registros.inserirRegistros(
                    ram.buscarUsoDeRam(),
                    cpu.buscarUsoCpu(),
                    ram.buscarQtdProcessos()
                );

                Thread.sleep(3000);
        }
    }
    catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    }
}
