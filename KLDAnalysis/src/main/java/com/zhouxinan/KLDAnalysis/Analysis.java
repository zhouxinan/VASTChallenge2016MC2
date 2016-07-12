package com.zhouxinan.KLDAnalysis;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class Analysis {
	Dao dao = Dao.getInstance();

	public List<String> getAllProxCard() throws SQLException {
		Dao dao = Dao.getInstance();
		return dao.selectAllProxCard();
	}

	public List<ProxSensorData> getProxSensorDataListOfProxCard(String proxCard) throws SQLException {
		Dao dao = Dao.getInstance();
		return dao.selectAllProxSensorDataOfProxCard(proxCard);
	}

	public List<String> getDistinctDateOfProxCard(String proxCard) throws SQLException {
		Dao dao = Dao.getInstance();
		return dao.selectDistinctDateOfProxCard(proxCard);
	}

	public List<ProxSensorData> getProxSensorDataListByDateAndProxCard(String proxCard, String date)
			throws SQLException {
		Dao dao = Dao.getInstance();
		return dao.selectByDateAndProxCard(proxCard, date);
	}

	public void insertToAnalysisTable(String proxCard, String zone, String date, Integer floor, Double duration)
			throws SQLException {
		Dao dao = Dao.getInstance();
		dao.insertToAnalysisTable(proxCard, zone, date, floor, duration);
	}

	public void analyze() throws SQLException {
		List<String> proxCardList = getAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			// String proxCard = "vawelon";
			List<String> dateList = getDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<ProxSensorData> psdList = getProxSensorDataListByDateAndProxCard(proxCard, date);
				System.out.println("length: " + psdList.size());
				Iterator<ProxSensorData> iterator3 = psdList.iterator();
				ProxSensorData proxSensorData = (ProxSensorData) iterator3.next();
				Double offset = proxSensorData.getOffset();
				Double firstOffset = offset;
				Integer floor = proxSensorData.getFloor();
				String zone = proxSensorData.getZone();
				while (iterator3.hasNext()) {
					ProxSensorData currentPsd = (ProxSensorData) iterator3.next();
					Double duration = currentPsd.getOffset() - offset;
					insertToAnalysisTable(proxCard, zone, date, floor, duration);
					offset = currentPsd.getOffset();
					floor = currentPsd.getFloor();
					zone = currentPsd.getZone();
				}
				Double durationOfDay = offset - firstOffset;
				dao.calculateProbabilityForDay(date, durationOfDay);

			}
		}
	}

}
