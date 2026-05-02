package com.jsvps.recharge_monitor.scheduler;

import com.jsvps.recharge_monitor.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PollingScheduler {

    private final ExternalApiService externalApiService;

//    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Kolkata")
    @Scheduled(fixedRate = 60*2*1000)
    void pollNbpdclApiDaily() {
        log.info("Polling NBPDCL api at {}", System.currentTimeMillis());
        externalApiService.checkMeterBalance();
    }
}
