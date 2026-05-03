package com.jsvps.recharge_monitor.scheduler;

import com.jsvps.recharge_monitor.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class PollingSchedulerDev {

    private final ExternalApiService externalApiService;

    @Scheduled(fixedRate = 60*1000, initialDelay = 0)
    void pollNbpdclApiTest() {
        log.info("Test Polling NBPDCL api at {}", System.currentTimeMillis());
        externalApiService.checkMeterBalance();
    }
}
