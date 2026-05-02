package com.jsvps.recharge_monitor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsvps.recharge_monitor.dto.MeterDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {

    @Value("${nbpdcl.uri}")
    private String uri;

    @Value("${lowBalanceThreshold}")
    private Double lowBalanceThreshold;

    @Value("#{'${payload_list}'.split(',')}")
    private List<String> payloadList;

    @Value("#{${meterIdMap}}")
    private Map<String, String> meterIdCustAliasMap;

    @Value("#{${rechargeUrlMap}}")
    private Map<String, String> meterIdRchrgUrlMap;

    String emojiLowInd = "🔴";
    String emojiOkInd = "✅";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationService notificationService;

    public void checkMeterBalance() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        List<MeterDetails> allMeterDetails = new ArrayList<>();
        getAllMeterDetails().forEach(meterDetails -> {
            if (meterDetails.getAvailBalance() < lowBalanceThreshold) {
                meterDetails.setLowBalanceFlag(true);
                atomicBoolean.set(true);
            }
            allMeterDetails.add(meterDetails);
        });

        if(atomicBoolean.get()){
            String preparedMessage = prepareLowBalanceMessage(allMeterDetails);
            invokeLowBalanceAlert(preparedMessage);
        }else {
            log.info("All balances are above threshold.");
        }
    }

    private String prepareLowBalanceMessage(List<MeterDetails> allMeterDetails) {
        StringBuilder sb = new StringBuilder("⚡ ALERT: Low Balance ⚡\n\n");
        for (MeterDetails meterDetails : allMeterDetails) {
            if(!meterDetails.isLowBalanceFlag()){
                continue;
            }else {
                sb.append("*");
                sb.append(emojiLowInd);
            }
            sb.append(" ")
                    .append(meterIdCustAliasMap.get(meterDetails.getMeterNumber()))
                    .append(" | ")
                    .append("Bal: ")
                    .append(meterDetails.getAvailBalance())
                    .append("*\n")
                    .append("ID: ")
                    .append(meterDetails.getUnitId())
                    .append("\n")
                    .append("Last Rchrg: ")
                    .append(meterDetails.getMaxDtOfRecharge())
                    .append("\n")
                    .append("Pay: ")
                    .append(meterIdRchrgUrlMap.get(meterDetails.getMeterNumber()))
                    .append("\n\n");
        }
        return sb.toString();
    }

    public void invokeLowBalanceAlert(String message) {
        notificationService.invokeNotification(message);
    }

    private List<MeterDetails> getAllMeterDetails() {
        List<MeterDetails> allMeterDetails = new ArrayList<>();
        for(String s : payloadList){
            allMeterDetails.add(fetchMeterDetailsFromApi(s));
        }
        return allMeterDetails;
    }

    public MeterDetails fetchMeterDetailsFromApi(String payload) {
        log.info("Start fetching meter balance for payload {}", payload);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, payload, String.class);
        log.info("Successfully fetched meter balance for payload {}", payload);
        MeterDetails meter = new MeterDetails();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String statusMsg = root.get(0).path("data").path("statusMsg").asText();
            if("SUCCESS".equalsIgnoreCase(statusMsg)){
                JsonNode meterDetails = root.get(0).path("data").path("data").get(0);
                meter = objectMapper.readValue(meterDetails.toString(), MeterDetails.class);
            }
        } catch (JsonProcessingException e) {
            log.error("Errored while parsing NBPDCL api response", e);
            notificationService.invokeErrorNotification();
            throw new RuntimeException(e);
        }
        return meter;
    }
}
