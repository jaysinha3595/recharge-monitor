package com.jsvps.recharge_monitor.dto;

import lombok.Data;

@Data
public class MeterDetails {
    private String updateDate;
    private String meterNumber;
    private String meterStatus;
    private Double availBalance;
    private Integer recordStatus;
    private Double rechargeAmount;
    private String createdBy;
    private String updateBy;
    private String mcFlag;
    private String unitId;
    private String serverIp;
    private Integer arrearAmount;
    private String maxDtOfRecharge;
    private String createDate;
    private String id;
    private boolean lowBalanceFlag;
}
