package notificacoes;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;
import infraestrutura.Cpu;
import infraestrutura.MemoriaRam;

public class AutomacaoDeAlertasSlack {
    private static String webHooksUrl = "https://hooks.slack.com/services/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";
    private static String slackChannel = "hospital-beneficencia-portuguesa";

    public static void main(String[] args) {
        Cpu cpu = new Cpu();
        MemoriaRam memoriaRam = new MemoriaRam();

        Double usoCpu = cpu.buscarUsoCpu();
        Double totalRam = memoriaRam.buscarTotalDeRam();
        Double usoRam = memoriaRam.buscarUsoDeRam();
        Integer totalProcessos = memoriaRam.buscarQtdProcessos();

        if (usoCpu > 80.0) {
            enviarAlertaSlack(String.format("Nível crítico de uso da CPU detectado: %.2f%%", usoCpu));
        }

        if (usoRam > 80.0) {
            enviarAlertaSlack(String.format(
                    "Memória RAM em estado crítico!\nTotal de memória RAM da máquina: %.2f GB\nMemória utilizada: %.2f GB\nQuantidade de processos em aberto: %d",
                    totalRam, usoRam, totalProcessos));
        }
    }

    public static void enviarAlertaSlack(String message) {
        try {
            Payload payload = Payload.builder()
                    .channel(slackChannel)
                    .text(message)
                    .build();
            WebhookResponse wbResp = Slack.getInstance().send(webHooksUrl, payload);
            System.out.println("Resposta do webhook: " + wbResp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
