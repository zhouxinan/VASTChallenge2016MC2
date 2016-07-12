package com.zhouxinan.KLDAnalysis;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class Analysis {
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

	public void analyze() throws SQLException {
		List<String> proxCardList = getAllProxCard();
//		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
//			String proxCard = (String) iterator.next();
			String proxCard = "vawelon";
			List<String> dateList = getDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				System.out.println((String) iterator2.next());
			}
//		}
	}

}
