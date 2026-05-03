package com.jsvps.recharge_monitor.scheduler;

import com.jsvps.recharge_monitor.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class PollingScheduler {

    private final ExternalApiService externalApiService;

    @Scheduled(cron = "${cronExpression}", zone = "Asia/Kolkata")
    void pollNbpdclApiDaily() {
        log.info("Polling NBPDCL api at {}", System.currentTimeMillis());
        externalApiService.checkMeterBalance();
    }
}
