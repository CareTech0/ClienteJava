package infraestrutura;

import com.github.britooo.looca.api.core.Looca;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class RedeLocal {
    private Looca looca = new Looca();
    private Double  valocidadeRede;
    private Long download = looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getBytesRecebidos();


    public RedeLocal(){

    }

    public static double buscarVelocidadeRede(){
        // Crie um objeto SpeedTestSocket
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        // Variável para armazenar a velocidade de download em Mbps
        double downloadSpeedMbps = 0;

        // Adicione um listener para capturar eventos de teste de velocidade
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                // Quando o teste for concluído, obtenha a velocidade de download em bits/s
                double downloadSpeed = report.getTransferRateBit().doubleValue();

                // Converta a velocidade de bits por segundo para megabits por segundo (Mbps)
                double downloadSpeedMbps = downloadSpeed / 1_000_000.0;
                //System.out.println(downloadSpeedMbps);
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                System.err.println("Erro: " + errorMessage);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // Progresso do teste de velocidade (opcional)
               System.out.println("Progresso: " + percent + "%");
            }
        });

        // Inicie o teste de download com um arquivo de teste
        String fileUrl = "https://link.testfile.org/PDF10MB"; // URL do arquivo de teste
        int timeout = 10000; // Tempo limite de conexão em milissegundos
        System.out.println("1");
        speedTestSocket.startDownload(fileUrl, timeout);
        System.out.println("2");
        // Espere até que o teste seja concluído

        while (downloadSpeedMbps == 0) {
            try {
                Thread.sleep(100); // Espera 100 milissegundos antes de verificar novamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        // Retorne a velocidade de download em Mbps
        return downloadSpeedMbps;
    }

    public Long getDownload() {
        return download;
    }
}
