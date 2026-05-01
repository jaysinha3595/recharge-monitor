package com.jsvps.recharge_monitor.service;

import com.jsvps.recharge_monitor.dto.MeterDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@Component
public class NotificationService {

    @Value("${wacli.group.jid}")
    private String groupJid;

    public void invokeNotification(String message){
        runBashCommands(message);
    }

    public void runBashCommands(String message) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "sh", "-c", "wacli send text --to " + groupJid + " --message '"+message+"'");
        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line -> System.out.println("CLI Output: " + line));
            }
            int exitCode = process.waitFor();
            log.info("wacli success: exitCode {}", exitCode);
        } catch (Exception e) {
            log.error("Errored while sending whatsapp messaage" ,e);
        }
    }

    public void invokeErrorNotification() {
    }
}
