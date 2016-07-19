package com.zhouxinan.KLDAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

public class Analysis {
	Dao dao = Dao.getInstance();

	public void buildDailyDataTable() throws SQLException {
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<ProxSensorData> psdList = dao.selectByProxCardAndDate(proxCard, date);
				/*
				 * The following 3 lines of code run with no result, which
				 * proves the validity of the following algorithm.
				 */
				// if (psdList.size() == 1) {
				// System.out.println(date + proxCard);
				// }
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

	public void calculateKLDPerPerson(boolean isInnerJoin) throws SQLException, FileNotFoundException {
		Gson gson = new Gson();
		String fileName;
		if (isInnerJoin) {
			fileName = "EmployeeByDayComparisons.json";
		} else {
			fileName = "EmployeeByDayComparisonsApprox.json";
		}
		File file = new File(fileName);
		PrintWriter printWriter = new PrintWriter(file);
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			printWriter.print("\"" + proxCard + "\" : {\n");
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			String dateListJson = gson.toJson(dateList);
			printWriter.print("\"dates\" : ");
			printWriter.print(dateListJson + ",\n");
			printWriter.print("\"matrix\" : \n");
			List<List<Double>> matrixRowList = new ArrayList<List<Double>>();
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<Double> matrixRow = new ArrayList<Double>();
				List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
				for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
					String date2 = (String) iterator3.next();
					if (isInnerJoin) {
						matrixRow.add(
								Math.round(dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date, date2) * 100.0)
										/ 100.0);
					} else {
						matrixRow.add(
								Math.round(dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2) * 100.0) / 100.0);
					}
				}
				matrixRowList.add(matrixRow);
			}
			printWriter.println(gson.toJson(matrixRowList) + "}");
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}

	public void calculateKLDOfSortedHistogramPerPerson() throws SQLException, FileNotFoundException {
		Gson gson = new Gson();
		File file = new File("EmployeeByDayComparisonsSortedHistogram.json");
		PrintWriter printWriter = new PrintWriter(file);
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			printWriter.print("\"" + proxCard + "\" : {\n");
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			String dateListJson = gson.toJson(dateList);
			printWriter.print("\"dates\" : ");
			printWriter.print(dateListJson + ",\n");
			printWriter.print("\"matrix\" : \n");
			List<List<Double>> matrixRowList = new ArrayList<List<Double>>();
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<Double> matrixRow = new ArrayList<Double>();
				List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
				for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
					String date2 = (String) iterator3.next();
					matrixRow.add(Math
							.round(dao.selectSortedHistogramKLDOfTwoDatesOfProxCard(proxCard, date, date2, 5) * 100.0)
							/ 100.0);
				}
				matrixRowList.add(matrixRow);
			}
			printWriter.println(gson.toJson(matrixRowList) + "}");
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}

	public void calculateAverageKLDPerDayPerPerson(boolean isInnerJoin) throws SQLException, FileNotFoundException {
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				Double average = 0.0;
				List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
				for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
					String date2 = (String) iterator3.next();
					if (isInnerJoin) {
						average += dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date, date2);
					} else {
						average += dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2);
					}
				}
				average /= dateList2.size() - 1;
				if (isInnerJoin) {
					dao.insertToSortedAverage("sorted_average_2", proxCard, date, average);
				} else {
					dao.insertToSortedAverage("sorted_average", proxCard, date, average);
				}
			}
		}
	}

	public void calculateAverageKLDOfSortedHistogramPerDayPerPerson() throws SQLException, FileNotFoundException {
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				Double average = 0.0;
				List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
				for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
					String date2 = (String) iterator3.next();
					average += dao.selectSortedHistogramKLDOfTwoDatesOfProxCard(proxCard, date, date2, 5);
				}
				average /= dateList2.size() - 1;
				dao.insertToSortedAverage("sorted_average_3", proxCard, date, average);
			}
		}
	}

	public void calculateKLDPerDepartment(boolean isInnerJoin) throws SQLException, IOException {
		Gson gson = new Gson();
		String fileName;
		if (isInnerJoin) {
			fileName = "DepartmentComparisons.json";
		} else {
			fileName = "DepartmentComparisonsApprox.json";
		}
		File file = new File(fileName);
		PrintWriter printWriter = new PrintWriter(file);
		List<String> departmentList = dao.selectAllDepartments();
		for (Iterator<String> iterator = departmentList.iterator(); iterator.hasNext();) {
			String department = (String) iterator.next();
			printWriter.print("\"" + department + "\" : {\n");
			List<String> employeeList = dao.selectAllEmployeesOfDepartment(department);
			String employeeListJson = gson.toJson(employeeList);
			printWriter.print("\"employees\" : " + employeeListJson + ",\n");
			printWriter.print("\"dates\" : {\n");
			List<String> dateList = dao.selectDistinctDateForDepartment(department);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				printWriter.println("\"" + date + "\" : ");
				List<List<Double>> matrixRowList = new ArrayList<List<Double>>();
				List<String> employeeList1 = new LinkedList<String>(employeeList);
				for (Iterator<String> iterator3 = employeeList1.iterator(); iterator3.hasNext();) {
					String employee1 = (String) iterator3.next();
					List<Double> matrixRow = new ArrayList<Double>();
					List<String> employeeList2 = new LinkedList<String>(employeeList);
					for (Iterator<String> iterator4 = employeeList2.iterator(); iterator4.hasNext();) {
						String employee2 = (String) iterator4.next();
						if (isInnerJoin) {
							matrixRow.add(Math.round(
									dao.selectKLDOfTwoEmployeesOfDateInnerJoin(employee1, employee2, date) * 100.0)
									/ 100.0);
						} else {
							matrixRow.add(
									Math.round(dao.selectKLDOfTwoEmployeesOfDate(employee1, employee2, date) * 100.0)
											/ 100.0);
						}

					}
					matrixRowList.add(matrixRow);
				}
				printWriter.print(gson.toJson(matrixRowList));
				if (iterator2.hasNext()) {
					printWriter.println(",");
				}
			}
			printWriter.println("}}");
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}

	public void calculateKLDOfSortedHistogramPerDepartment() throws SQLException, IOException {
		Gson gson = new Gson();
		File file = new File("DepartmentComparisonsSortedHistogram.json");
		PrintWriter printWriter = new PrintWriter(file);
		List<String> departmentList = dao.selectAllDepartments();
		for (Iterator<String> iterator = departmentList.iterator(); iterator.hasNext();) {
			String department = (String) iterator.next();
			printWriter.print("\"" + department + "\" : {\n");
			List<String> employeeList = dao.selectAllEmployeesOfDepartment(department);
			String employeeListJson = gson.toJson(employeeList);
			printWriter.print("\"employees\" : " + employeeListJson + ",\n");
			printWriter.print("\"dates\" : {\n");
			List<String> dateList = dao.selectDistinctDateForDepartment(department);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				printWriter.println("\"" + date + "\" : ");
				List<List<Double>> matrixRowList = new ArrayList<List<Double>>();
				List<String> employeeList1 = new LinkedList<String>(employeeList);
				for (Iterator<String> iterator3 = employeeList1.iterator(); iterator3.hasNext();) {
					String employee1 = (String) iterator3.next();
					List<Double> matrixRow = new ArrayList<Double>();
					List<String> employeeList2 = new LinkedList<String>(employeeList);
					for (Iterator<String> iterator4 = employeeList2.iterator(); iterator4.hasNext();) {
						String employee2 = (String) iterator4.next();
						matrixRow.add(Math
								.round(dao.selectSortedHistogramKLDOfTwoEmployeesOfDate(employee1, employee2, date, 5) * 100.0)
								/ 100.0);
					}
					matrixRowList.add(matrixRow);
				}
				printWriter.print(gson.toJson(matrixRowList));
				if (iterator2.hasNext()) {
					printWriter.println(",");
				}
			}
			printWriter.println("}}");
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}
}
