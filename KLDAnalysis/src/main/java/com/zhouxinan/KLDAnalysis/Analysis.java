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

	public void calculateKLDPerPerson(boolean isInnerJoin, boolean isMatrixSymmetrical)
			throws SQLException, FileNotFoundException {
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
						matrixRow.add(dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date, date2));
					} else {
						matrixRow.add(dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2));
					}
				}
				matrixRowList.add(matrixRow);
			}
			if (isMatrixSymmetrical) {
				makeMatrixSymmetric(matrixRowList);
			}
			printWriter.println(gson.toJson(roundMatrixRowList(matrixRowList)) + "}");
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}

	public void calculateKLDOfSortedHistogramPerPerson(boolean isMatrixSymmetrical)
			throws SQLException, FileNotFoundException {
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
					matrixRow.add(dao.selectSortedHistogramKLDOfTwoDatesOfProxCard(proxCard, date, date2, 5));
				}
				matrixRowList.add(matrixRow);
			}
			if (isMatrixSymmetrical) {
				makeMatrixSymmetric(matrixRowList);
			}
			printWriter.println(gson.toJson(roundMatrixRowList(matrixRowList)) + "}");
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

	public void calculateKLDPerDepartment(boolean isInnerJoin, boolean isMatrixSymmetrical)
			throws SQLException, IOException {
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
							matrixRow.add(dao.selectKLDOfTwoEmployeesOfDateInnerJoin(employee1, employee2, date));
						} else {
							matrixRow.add(dao.selectKLDOfTwoEmployeesOfDate(employee1, employee2, date));
						}

					}
					matrixRowList.add(matrixRow);
				}
				if (isMatrixSymmetrical) {
					makeMatrixSymmetric(matrixRowList);
				}
				printWriter.print(gson.toJson(roundMatrixRowList(matrixRowList)));
				if (isInnerJoin) {
					calculateAveragePerRow(matrixRowList, "sorted_average_5", employeeList1, date);
				} else {
					calculateAveragePerRow(matrixRowList, "sorted_average_4", employeeList1, date);
				}
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

	public void calculateKLDOfSortedHistogramPerDepartment(boolean isMatrixSymmetrical)
			throws SQLException, IOException {
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
						matrixRow.add(dao.selectSortedHistogramKLDOfTwoEmployeesOfDate(employee1, employee2, date, 5));
					}
					matrixRowList.add(matrixRow);
				}
				if (isMatrixSymmetrical) {
					makeMatrixSymmetric(matrixRowList);
				}
				printWriter.print(gson.toJson(roundMatrixRowList(matrixRowList)));
				calculateAveragePerRow(matrixRowList, "sorted_average_6", employeeList1, date);
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

	public void calculateAverageKLDPerDepartment(boolean isInnerJoin) throws SQLException, IOException {
		List<String> departmentList = dao.selectAllDepartments();
		for (Iterator<String> iterator = departmentList.iterator(); iterator.hasNext();) {
			String department = (String) iterator.next();
			List<String> employeeList = dao.selectAllEmployeesOfDepartment(department);
			List<String> dateList = dao.selectDistinctDateForDepartment(department);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<String> employeeList1 = new LinkedList<String>(employeeList);
				for (Iterator<String> iterator3 = employeeList1.iterator(); iterator3.hasNext();) {
					String employee1 = (String) iterator3.next();
					Double average = 0.0;
					List<String> employeeList2 = new LinkedList<String>(employeeList);
					for (Iterator<String> iterator4 = employeeList2.iterator(); iterator4.hasNext();) {
						String employee2 = (String) iterator4.next();
						if (isInnerJoin) {
							average += dao.selectKLDOfTwoEmployeesOfDateInnerJoin(employee1, employee2, date);
						} else {
							average += dao.selectKLDOfTwoEmployeesOfDate(employee1, employee2, date);
						}
					}
					average /= employeeList.size() - 1;
					if (isInnerJoin) {
						dao.insertToSortedAverage("sorted_average_5", employee1, date, average);
					} else {
						dao.insertToSortedAverage("sorted_average_4", employee1, date, average);
					}
				}
			}
		}
	}

	public void calculateAverageKLDOfSortedHistogramPerDepartment() throws SQLException, IOException {
		List<String> departmentList = dao.selectAllDepartments();
		for (Iterator<String> iterator = departmentList.iterator(); iterator.hasNext();) {
			String department = (String) iterator.next();
			List<String> employeeList = dao.selectAllEmployeesOfDepartment(department);
			List<String> dateList = dao.selectDistinctDateForDepartment(department);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				List<String> employeeList1 = new LinkedList<String>(employeeList);
				for (Iterator<String> iterator3 = employeeList1.iterator(); iterator3.hasNext();) {
					String employee1 = (String) iterator3.next();
					Double average = 0.0;
					List<String> employeeList2 = new LinkedList<String>(employeeList);
					for (Iterator<String> iterator4 = employeeList2.iterator(); iterator4.hasNext();) {
						String employee2 = (String) iterator4.next();
						average += dao.selectSortedHistogramKLDOfTwoEmployeesOfDate(employee1, employee2, date, 5);
					}
					average /= employeeList.size() - 1;
					dao.insertToSortedAverage("sorted_average_6", employee1, date, average);
				}
			}
		}
	}

	public void makeMatrixSymmetric(List<List<Double>> matrixRowList) {
		for (int i = 0; i < matrixRowList.size(); i++) {
			for (int j = i + 1; j < matrixRowList.get(i).size(); j++) {
				Double average = (matrixRowList.get(i).get(j) + matrixRowList.get(j).get(i)) / 2;
				matrixRowList.get(i).set(j, average);
				matrixRowList.get(j).set(i, average);
			}
		}
	}

	public void calculateAveragePerRow(List<List<Double>> matrixRowList, String tableName, List<String> list,
			String date) throws SQLException {
		for (int i = 0; i < matrixRowList.size(); i++) {
			Double average = 0.0;
			for (int j = 0; j < matrixRowList.get(i).size(); j++) {
				average += matrixRowList.get(i).get(j);
			}
			average /= matrixRowList.get(i).size() - 1;
			dao.insertToSortedAverage(tableName, list.get(i), date, average);
		}
	}

	public void calculateAveragePerRow2(List<List<Double>> matrixRowList, String tableName, String proxCard,
			List<String> list) throws SQLException {
		for (int i = 0; i < matrixRowList.size(); i++) {
			Double average = 0.0;
			for (int j = 0; j < matrixRowList.get(i).size(); j++) {
				average += matrixRowList.get(i).get(j);
			}
			average /= matrixRowList.get(i).size() - 1;
			dao.insertToSortedAverage(tableName, proxCard, list.get(i), average);
		}
	}

	public List<List<Double>> roundMatrixRowList(List<List<Double>> matrixRowList) {
		List<List<Double>> matrixRowListCopy = new ArrayList<List<Double>>(matrixRowList);
		for (int i = 0; i < matrixRowListCopy.size(); i++) {
			for (int j = 0; j < matrixRowListCopy.get(i).size(); j++) {
				matrixRowListCopy.get(i).set(j, Math.round(matrixRowListCopy.get(i).get(j) * 100.0) / 100.0);
			}
		}
		return matrixRowListCopy;
	}

	public void printMatrixRowList(List<List<Double>> matrixRowList) {
		for (int i = 0; i < matrixRowList.size(); i++) {
			for (int j = 0; j < matrixRowList.get(i).size(); j++) {
				System.out.print(matrixRowList.get(i).get(j));
			}
			System.out.println();
		}
	}
}
