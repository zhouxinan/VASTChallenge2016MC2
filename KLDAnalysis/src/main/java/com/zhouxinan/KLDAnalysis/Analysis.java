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

	public void calculateKLD() throws SQLException {
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			System.out.println("current proxCard: " + proxCard);
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
				for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
					String date2 = (String) iterator3.next();
					System.out
							.print(Math.round(dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2) * 100.0) / 100.0
									+ "\t");
				}
				System.out.println();
			}
			System.out.println("===================");
		}
	}

	public void calculateKLDInnerJoin() throws SQLException {
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			System.out.println("current proxCard: " + proxCard);
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
				for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
					String date2 = (String) iterator3.next();
					System.out
							.print(Math.round(dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date, date2) * 100.0)
									/ 100.0 + "\t");
				}
				System.out.println();
			}
			System.out.println("===================");
		}
	}

	public void calculateKLDPerDepartment() throws SQLException {
		List<String> departmentList = dao.selectAllDepartments();
		for (Iterator<String> iterator = departmentList.iterator(); iterator.hasNext();) {
			String department = (String) iterator.next();
			System.out.println("current department: " + department);
			List<String> dateList = dao.selectDistinctDateForDepartment(department);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				System.out.println("current date: " + date);
				List<String> employeeList = dao.selectAllEmployeesOfDepartment(department);
				for (Iterator<String> iterator3 = employeeList.iterator(); iterator3.hasNext();) {
					String employee1 = (String) iterator3.next();
					List<String> employeeList2 = dao.selectAllEmployeesOfDepartment(department);
					for (Iterator<String> iterator4 = employeeList2.iterator(); iterator4.hasNext();) {
						String employee2 = (String) iterator4.next();
						System.out.print(Math.round(
								dao.selectKLDOfTwoEmployeesOfDateInnerJoin(employee1, employee2, date) * 100.0) / 100.0
								+ "\t");
					}
					System.out.println();
				}
				System.out.println("===================");
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~");
		}
	}

}
