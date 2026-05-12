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
                "/usr/local/bin/wacli-send.sh", groupJid, message);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                reader.lines().forEach(line -> System.out.println("CLI Output: " + line));
//            }
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
            log.info("wacli output: {}", output);
            log.info("wacli exitCode: {}", exitCode);
        } catch (Exception e) {
            log.error("Errored while sending whatsapp messaage" ,e);
        }
    }

    public void invokeErrorNotification() {
    }
}
