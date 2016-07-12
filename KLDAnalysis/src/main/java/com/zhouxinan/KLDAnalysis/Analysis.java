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

	public void analyze() throws SQLException {
		List<String> proxCardList = getAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			List<ProxSensorData> psdList = getProxSensorDataListOfProxCard(proxCard);
			for (Iterator<ProxSensorData> iterator2 = psdList.iterator(); iterator2.hasNext();) {
				ProxSensorData proxSensorData = (ProxSensorData) iterator2.next();
				System.out.println(proxSensorData.getOffset());
			}
		}
	}

}
