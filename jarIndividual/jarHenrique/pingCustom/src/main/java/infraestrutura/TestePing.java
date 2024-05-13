package infraestrutura;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestePing {
    public static void main(String[] args) {
        String[] hosts = {"localhost", "www.google.com", "www.facebook.com"};

        for (String host : hosts) {
            testarPing(host, 5);
        }
    }

    public static void testarPing(String host, int numPacotes) {
        try {
            ProcessBuilder builder = new ProcessBuilder("ping", "-c", Integer.toString(numPacotes), host);
            Process processo = builder.start();

            BufferedReader leitor = new BufferedReader(new InputStreamReader(processo.getInputStream()));
            String linha;
            while ((linha = leitor.readLine()) != null) {
                System.out.println(linha);
            }

            int codigoSaida = processo.waitFor();
            System.out.println("Comando ping para " + host + " finalizado com código de saída: " + codigoSaida + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}