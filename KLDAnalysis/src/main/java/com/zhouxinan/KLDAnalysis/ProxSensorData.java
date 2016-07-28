package com.zhouxinan.KLDAnalysis;

import java.util.Date;

public class ProxSensorData {
    private Integer proxSensorDataId;

    private String proxcard;

    private Date datetime;

    private Integer floor;

    private String zone;

    private Integer type;

    private Double offset;
    
    private Double probability;
    
    private Double duration;
    
    private String proxcard2;
    
    private Date datetime2;
    
    private Double largestValue;

    public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}

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

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public String getProxcard2() {
		return proxcard2;
	}

	public void setProxcard2(String proxcard2) {
		this.proxcard2 = proxcard2;
	}

	public Date getDatetime2() {
		return datetime2;
	}

	public void setDatetime2(Date datetime2) {
		this.datetime2 = datetime2;
	}

	public Double getLargestValue() {
		return largestValue;
	}

	public void setLargestValue(Double largestValue) {
		this.largestValue = largestValue;
	}
}
