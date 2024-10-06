package com.izzy.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.Arrays;

public class ScooterRequest {
    @Size(min = 1, max = 20)
    private String identifier;
    private String status;
    @Min(0) // Ensures the batteryLevel is at least 0
    @Max(100) // Ensures the batteryLevel does not exceed 100
    private Integer batteryLevel;
    private String zoneName;
    @Min(10) // Ensures the speedLimit is at least 10
    @Max(100) // Ensures the speedLimit does not exceed 100
    private Integer speedLimit;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public enum Status {
        ACTIVE("Active"), INACTIVE("Inactive"), BLOCKED("Blocked"), UNBLOCKED("Unblocked"), BROKEN("Broken"), RENTED("Rented");
        private final String value;

        Status(String value) {
            this.value = value;
        }

        public static Boolean checkByValue(String value) {
            return Arrays.stream(values()).filter(month -> month.getValue().equals(value)).findFirst().map(v -> !v.toString().isBlank()).orElse(Boolean.FALSE);
        }

        public String getValue() {
            return value;
        }
    }
}
