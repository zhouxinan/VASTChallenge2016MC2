package com.zhouxinan.KLDAnalysis;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class Analysis {
	Dao dao = Dao.getInstance();

	public void analyzeDailyDataForPerson() throws SQLException {
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<ProxSensorData> psdList = dao.selectByDateAndProxCard(proxCard, date);
				Iterator<ProxSensorData> iterator3 = psdList.iterator();
				ProxSensorData proxSensorData = (ProxSensorData) iterator3.next();
				Double firstOffset = proxSensorData.getOffset();
				Double offset = firstOffset;
				Integer floor = proxSensorData.getFloor();
				String zone = proxSensorData.getZone();
				while (iterator3.hasNext()) {
					ProxSensorData currentPsd = (ProxSensorData) iterator3.next();
					Double duration = currentPsd.getOffset() - offset;
					dao.insertToAnalysisTable(proxCard, zone, date, floor, duration);
					offset = currentPsd.getOffset();
					floor = currentPsd.getFloor();
					zone = currentPsd.getZone();
				}
				Double durationOfDay = offset - firstOffset;
				dao.calculateProbabilityForDayAndPerson(proxCard, date, durationOfDay);
			}
		}
	}

}
