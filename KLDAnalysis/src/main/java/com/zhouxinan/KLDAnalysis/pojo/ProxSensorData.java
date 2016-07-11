package com.zhouxinan.KLDAnalysis.pojo;

import java.util.Date;

public class ProxSensorData {
    private Integer proxSensorDataId;

    private String proxcard;

    private Date datetime;

    private Integer floor;

    private String zone;

    private Integer type;

    private Double offset;

    public Integer getProxSensorDataId() {
        return proxSensorDataId;
    }

    public void setProxSensorDataId(Integer proxSensorDataId) {
        this.proxSensorDataId = proxSensorDataId;
    }

    public String getProxcard() {
        return proxcard;
    }

    public void setProxcard(String proxcard) {
        this.proxcard = proxcard == null ? null : proxcard.trim();
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? null : zone.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getOffset() {
        return offset;
    }

    public void setOffset(Double offset) {
        this.offset = offset;
    }
}