package notificacoes;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

public class AutomacaoDeAlertasSlack {
    private static final String webHooksUrl = "https://hooks.slack.com/services/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";
    private static final String slackChannel = "hospital-beneficencia-portuguesa";

    public void enviarAlertaSlack(String message) {
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
