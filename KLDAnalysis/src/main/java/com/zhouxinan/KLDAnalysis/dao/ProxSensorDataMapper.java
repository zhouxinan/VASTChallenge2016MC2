package com.zhouxinan.KLDAnalysis.dao;

import com.zhouxinan.KLDAnalysis.pojo.ProxSensorData;

public interface ProxSensorDataMapper {
    int deleteByPrimaryKey(Integer proxSensorDataId);

    int insert(ProxSensorData record);

    int insertSelective(ProxSensorData record);

    ProxSensorData selectByPrimaryKey(Integer proxSensorDataId);

    int updateByPrimaryKeySelective(ProxSensorData record);

    int updateByPrimaryKey(ProxSensorData record);
}