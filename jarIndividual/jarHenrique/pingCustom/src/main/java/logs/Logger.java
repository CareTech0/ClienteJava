package logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private String logFileName;
    private FileWriter logFileWriter;
    private DateTimeFormatter logFormatter;

    public Logger(String directoryName) {
        try {
            File directory = new File(directoryName);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = fileNameFormatter.format(LocalDateTime.now());

            this.logFileName = directoryName + "/caretechLog_" + timestamp + ".txt";

            this.logFileWriter = new FileWriter(this.logFileName, true);
            this.logFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gerarLog(String message) {
        try {
            String timestamp = logFormatter.format(LocalDateTime.now());
            this.logFileWriter.write(timestamp + " - " + message + "\n");
            this.logFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fecharLog() {
        try {
            if (this.logFileWriter != null) {
                this.logFileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLogFileName() {
        return logFileName;
    }
}