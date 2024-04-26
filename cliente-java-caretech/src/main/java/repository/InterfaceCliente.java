package repository;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import model.Usuario;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
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



            if (true){
                statusDaVerificacao = "Login Realizado com Sucesso!!!";
            } else {
                statusDaVerificacao = "Login ou senha inválidos!!!";
            }

            System.out.println(statusDaVerificacao);
        } while(!statusDaVerificacao.equals("Login Realizado com Sucesso!!!"));

        while (true){
            MemoriaRam ram = new MemoriaRam();
            System.out.println("Ram em uso no momento: %.1f".formatted(ram.buscarUsoDeRam()));
            DiscoRigido ssd = new DiscoRigido();
            System.out.println("Total de espaço nos discos rigidos: " + ssd.buscarTotalDeEspaco());
            System.out.println("Espeço disponível nos discos rígidos: " + ssd.buscarEspacoLivre());
            RedeLocal rede = new RedeLocal();
            System.out.println("Infos de rede: ");
            rede.buscarVelocidadeRede();
            Cpu cpu = new Cpu();
            System.out.println("Infos de temperatura: ");
            System.out.println(cpu.buscarUsoCpu());
            System.out.println("Teste comando no terminal");

            System.out.println("Lista de processos: ");
            System.out.println(ram.buscarProcessos());

            //Execute a command in PowerShell session
            PowerShellResponse response = PowerShell.executeSingleCommand("speedtest-cli");

            //Print results
            System.out.println("List Processes:" + response.getCommandOutput());
            break;
        }

    }

}
