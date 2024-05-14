package ui;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

public class TesteMessage {

    private static String webHooksUrl = "https://hooks.slack.com/services/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";
    private static String oAuthToken = "xoxb-7123829579969-7108341675157-Alj4yke2YGUSTHQJzZJablZv";
    private static String slackChannel = "hospital-beneficencia-portuguesa";

    public static void main(String[] args) {
        testeSlack("Teste");
    }

    public static void testeSlack(String message) {
        try {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append(message);

            Payload payload = Payload.builder().channel(slackChannel).text(msgBuilder.toString()).build();

            WebhookResponse wbResp = Slack.getInstance().send(webHooksUrl, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
