package infraestrutura;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestePing {
    public static void main(String[] args) {
        String[] hosts = {"localhost", "google.com", "facebook.com"};

        for (String host : hosts) {
            testarPing(host);
        }
    }

    public static void testarPing(String host) {
        try {
            Process processo = Runtime.getRuntime().exec("ping " + host);
            BufferedReader leitor = new BufferedReader(new InputStreamReader(processo.getInputStream()));

            String linha;
            while ((linha = leitor.readLine()) != null) {
                System.out.println(linha);
            }

            int codigoSaida = processo.waitFor();
            System.out.println("Comando ping para " + host + " finalizado com código de saída: " + codigoSaida);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
