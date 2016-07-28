package com.zhouxinan.KLDAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
				dao.calculateProbabilityForDayAndPerson(proxCard, date, durationOfDay, "00:00:00", "23:59:59");
			}
		}
	}

	public void buildDailyDataTable2() throws SQLException {
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
				Double offset = proxSensorData.getOffset();
				Integer floor = proxSensorData.getFloor();
				String zone = proxSensorData.getZone();
				Date datetime = proxSensorData.getDatetime();
				while (iterator3.hasNext()) {
					ProxSensorData currentPsd = (ProxSensorData) iterator3.next();
					Double duration = currentPsd.getOffset() - offset;
					dao.insertToAnalysisTable2(proxCard, zone, datetime, floor, duration);
					offset = currentPsd.getOffset();
					floor = currentPsd.getFloor();
					zone = currentPsd.getZone();
					datetime = currentPsd.getDatetime();
				}
			}
		}
	}

	public void divideSection() throws SQLException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<String> proxCardList = dao.selectAllProxCard();
		for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
			String proxCard = (String) iterator.next();
			List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
			for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
				String date = (String) iterator2.next();
				System.out.println("proxCard: " + proxCard + " date: " + date);
				List<List<ProxSensorData>> psdListArray = new ArrayList<List<ProxSensorData>>();
				List<String> startTimeList = Arrays.asList("00:00:00", "06:00:00", "12:00:00", "18:00:00");
				List<String> endTimeList = Arrays.asList("05:59:59", "11:59:59", "17:59:59", "23:59:59");
				int sectionCount = startTimeList.size();
				for (int i = 0; i < sectionCount; i++) {
					psdListArray.add(dao.selectByProxCardAndDateFromDailyData2(proxCard, date, startTimeList.get(i),
							endTimeList.get(i)));
				}
				try {
					for (int i = 0; i < sectionCount; i++) {
						List<ProxSensorData> psdList = psdListArray.get(i);
						if (i != sectionCount - 1 && !psdList.isEmpty() && !psdListArray.get(i + 1).isEmpty()) {
							Date sectionEndTime = formatter.parse(date + " " + endTimeList.get(i));
							Date nextSectionStartTime = formatter.parse(date + " " + startTimeList.get(i + 1));
							ProxSensorData psd = psdList.get(psdList.size() - 1);
							ProxSensorData psd2 = psdListArray.get(i + 1).get(0);
							psd.setDuration(Double
									.parseDouble((sectionEndTime.getTime() - psd.getDatetime().getTime()) / 1000 + ""));
							ProxSensorData psdNew = new ProxSensorData();
							psdNew.setDatetime(nextSectionStartTime);
							psdNew.setFloor(psd.getFloor());
							psdNew.setZone(psd.getZone());
							psdNew.setDuration(Double.parseDouble(
									(psd2.getDatetime().getTime() - nextSectionStartTime.getTime()) / 1000 + ""));
							psdListArray.get(i + 1).add(0, psdNew);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < sectionCount; i++) {
					List<ProxSensorData> psdList = psdListArray.get(i);
					Double durationOfSection = 0.0;
					for (int j = 0; j < psdList.size(); j++) {
						ProxSensorData proxSensorData = psdList.get(j);
						dao.insertToDailyDataBySection(proxCard, proxSensorData.getZone(),
								date + " " + startTimeList.get(i), proxSensorData.getFloor(),
								proxSensorData.getDuration());
						durationOfSection += proxSensorData.getDuration();
					}
					dao.calculateProbabilityForDayAndPersonAndSection(proxCard, date, durationOfSection,
							startTimeList.get(i));
				}
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
						matrixRow.add(dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date, date2, "00:00:00",
								"daily_data"));
					} else {
						matrixRow.add(
								dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2, "00:00:00", "daily_data"));
					}
				}
				matrixRowList.add(matrixRow);
			}
			if (isMatrixSymmetrical) {
				makeMatrixSymmetric(matrixRowList);
			}
			printWriter.println(gson.toJson(roundMatrixRowList(matrixRowList)) + "}");
			if (isInnerJoin) {
				calculateAveragePerRow2(matrixRowList, "sorted_average_2", proxCard, dateList);
			} else {
				calculateAveragePerRow2(matrixRowList, "sorted_average", proxCard, dateList);
			}
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}

	public void calculateKLDBySectionPerPerson(boolean isInnerJoin, boolean isMatrixSymmetrical)
			throws SQLException, FileNotFoundException {
		int hours[] = { 0, 6, 12, 18 };
		List<String> startTimeList = Arrays.asList("00:00:00", "06:00:00", "12:00:00", "18:00:00");
		for (int i = 0; i < hours.length; i++) {
			Gson gson = new Gson();
			String fileName;
			if (isInnerJoin) {
				fileName = "EmployeeByHour" + hours[i] + "Comparisons.json";
			} else {
				fileName = "EmployeeByHour" + hours[i] + "ComparisonsApprox.json";
			}
			File file = new File(fileName);
			PrintWriter printWriter = new PrintWriter(file);
			List<String> proxCardList = dao.selectAllProxCardByHour(hours[i]);
			for (Iterator<String> iterator = proxCardList.iterator(); iterator.hasNext();) {
				String proxCard = (String) iterator.next();
				printWriter.print("\"" + proxCard + "\" : {\n");
				List<String> dateList = dao.selectDistinctDateOfProxCardAndHour(proxCard, hours[i]);
				String dateListJson = gson.toJson(dateList);
				printWriter.print("\"dates\" : ");
				printWriter.print(dateListJson + ",\n");
				printWriter.print("\"matrix\" : \n");
				List<List<Double>> matrixRowList = new ArrayList<List<Double>>();
				for (Iterator<String> iterator2 = dateList.iterator(); iterator2.hasNext();) {
					String date = (String) iterator2.next();
					List<Double> matrixRow = new ArrayList<Double>();
					List<String> dateList2 = dao.selectDistinctDateOfProxCardAndHour(proxCard, hours[i]);
					for (Iterator<String> iterator3 = dateList2.iterator(); iterator3.hasNext();) {
						String date2 = (String) iterator3.next();
						if (isInnerJoin) {
							matrixRow.add(dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date, date2,
									startTimeList.get(i), "daily_data_by_section"));
						} else {
							matrixRow.add(dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2, startTimeList.get(i),
									"daily_data_by_section"));
						}
					}
					matrixRowList.add(matrixRow);
				}
				if (isMatrixSymmetrical) {
					makeMatrixSymmetric(matrixRowList);
				}
				printWriter.println(gson.toJson(roundMatrixRowList(matrixRowList)) + "}");
				// if (isInnerJoin) {
				// calculateAveragePerRow2(matrixRowList, "sorted_average_2",
				// proxCard, dateList);
				// } else {
				// calculateAveragePerRow2(matrixRowList, "sorted_average",
				// proxCard, dateList);
				// }
				if (iterator.hasNext()) {
					printWriter.println(",");
				}
			}
			printWriter.close();
		}
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
					matrixRow.add(
							dao.selectSortedHistogramKLDOfTwoDatesOfProxCard(proxCard, date, date2, 5, "00:00:00"));
				}
				matrixRowList.add(matrixRow);
			}
			if (isMatrixSymmetrical) {
				makeMatrixSymmetric(matrixRowList);
			}
			printWriter.println(gson.toJson(roundMatrixRowList(matrixRowList)) + "}");
			calculateAveragePerRow2(matrixRowList, "sorted_average_3", proxCard, dateList);
			if (iterator.hasNext()) {
				printWriter.println(",");
			}
		}
		printWriter.close();
	}

	// public void calculateAverageKLDPerDayPerPerson(boolean isInnerJoin)
	// throws SQLException, FileNotFoundException {
	// List<String> proxCardList = dao.selectAllProxCard();
	// for (Iterator<String> iterator = proxCardList.iterator();
	// iterator.hasNext();) {
	// String proxCard = (String) iterator.next();
	// List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
	// for (Iterator<String> iterator2 = dateList.iterator();
	// iterator2.hasNext();) {
	// String date = (String) iterator2.next();
	// Double average = 0.0;
	// List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
	// for (Iterator<String> iterator3 = dateList2.iterator();
	// iterator3.hasNext();) {
	// String date2 = (String) iterator3.next();
	// if (isInnerJoin) {
	// average += dao.selectKLDOfTwoDatesOfProxCardInnerJoin(proxCard, date,
	// date2, "00:00:00",
	// "daily_data");
	// } else {
	// average += dao.selectKLDOfTwoDatesOfProxCard(proxCard, date, date2,
	// "00:00:00", "daily_data");
	// }
	// }
	// average /= dateList2.size() - 1;
	// if (isInnerJoin) {
	// dao.insertToSortedAverage("sorted_average_2", proxCard, date, average);
	// } else {
	// dao.insertToSortedAverage("sorted_average", proxCard, date, average);
	// }
	// }
	// }
	// }

	// public void calculateAverageKLDOfSortedHistogramPerDayPerPerson() throws
	// SQLException, FileNotFoundException {
	// List<String> proxCardList = dao.selectAllProxCard();
	// for (Iterator<String> iterator = proxCardList.iterator();
	// iterator.hasNext();) {
	// String proxCard = (String) iterator.next();
	// List<String> dateList = dao.selectDistinctDateOfProxCard(proxCard);
	// for (Iterator<String> iterator2 = dateList.iterator();
	// iterator2.hasNext();) {
	// String date = (String) iterator2.next();
	// Double average = 0.0;
	// List<String> dateList2 = dao.selectDistinctDateOfProxCard(proxCard);
	// for (Iterator<String> iterator3 = dateList2.iterator();
	// iterator3.hasNext();) {
	// String date2 = (String) iterator3.next();
	// average += dao.selectSortedHistogramKLDOfTwoDatesOfProxCard(proxCard,
	// date, date2, 5, "00:00:00");
	// }
	// average /= dateList2.size() - 1;
	// dao.insertToSortedAverage("sorted_average_3", proxCard, date, average);
	// }
	// }
	// }

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
							matrixRow.add(
									dao.selectKLDOfTwoEmployeesOfDateInnerJoin(employee1, employee2, date, "00:00:00"));
						} else {
							matrixRow.add(dao.selectKLDOfTwoEmployeesOfDate(employee1, employee2, date, "00:00:00"));
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

	public void calculateKLDOfSortedHistogramPerDepartment(boolean isMatrixSymmetrical, boolean isProxCardAnalysis)
			throws SQLException, IOException {
		Gson gson = new Gson();
		File file = new File("DepartmentComparisonsSortedHistogram.json");
		PrintWriter printWriter = new PrintWriter(file);
		List<String> departmentList = dao.selectAllDepartments();
		for (Iterator<String> iterator = departmentList.iterator(); iterator.hasNext();) {
			String department = (String) iterator.next();
			printWriter.print("\"" + department + "\" : {\n");
			List<String> employeeList;
			if (isProxCardAnalysis) {
				employeeList = dao.selectAllProxCardOfDepartment(department);
			} else {
				employeeList = dao.selectAllEmployeesOfDepartment(department);
			}
			String employeeListJson = gson.toJson(employeeList);
			printWriter.print("\"employees\" : " + employeeListJson + ",\n");
			printWriter.print("\"dates\" : {\n");
			List<String> dateList;
			if (isProxCardAnalysis) {
				dateList = dao.selectDistinctDateForDepartmentProxCard(department);
			} else {
				dateList = dao.selectDistinctDateForDepartment(department);
			}
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
						matrixRow.add(dao.selectSortedHistogramKLDOfTwoEmployeesOfDate(employee1, employee2, date, 5,
								"00:00:00"));
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

	// public void calculateAverageKLDPerDepartment(boolean isInnerJoin) throws
	// SQLException, IOException {
	// List<String> departmentList = dao.selectAllDepartments();
	// for (Iterator<String> iterator = departmentList.iterator();
	// iterator.hasNext();) {
	// String department = (String) iterator.next();
	// List<String> employeeList =
	// dao.selectAllEmployeesOfDepartment(department);
	// List<String> dateList = dao.selectDistinctDateForDepartment(department);
	// for (Iterator<String> iterator2 = dateList.iterator();
	// iterator2.hasNext();) {
	// String date = (String) iterator2.next();
	// List<String> employeeList1 = new LinkedList<String>(employeeList);
	// for (Iterator<String> iterator3 = employeeList1.iterator();
	// iterator3.hasNext();) {
	// String employee1 = (String) iterator3.next();
	// Double average = 0.0;
	// List<String> employeeList2 = new LinkedList<String>(employeeList);
	// for (Iterator<String> iterator4 = employeeList2.iterator();
	// iterator4.hasNext();) {
	// String employee2 = (String) iterator4.next();
	// if (isInnerJoin) {
	// average += dao.selectKLDOfTwoEmployeesOfDateInnerJoin(employee1,
	// employee2, date, "00:00:00");
	// } else {
	// average += dao.selectKLDOfTwoEmployeesOfDate(employee1, employee2, date,
	// "00:00:00");
	// }
	// }
	// average /= employeeList.size() - 1;
	// if (isInnerJoin) {
	// dao.insertToSortedAverage("sorted_average_5", employee1, date, average);
	// } else {
	// dao.insertToSortedAverage("sorted_average_4", employee1, date, average);
	// }
	// }
	// }
	// }
	// }
	//
	// public void calculateAverageKLDOfSortedHistogramPerDepartment() throws
	// SQLException, IOException {
	// List<String> departmentList = dao.selectAllDepartments();
	// for (Iterator<String> iterator = departmentList.iterator();
	// iterator.hasNext();) {
	// String department = (String) iterator.next();
	// List<String> employeeList =
	// dao.selectAllEmployeesOfDepartment(department);
	// List<String> dateList = dao.selectDistinctDateForDepartment(department);
	// for (Iterator<String> iterator2 = dateList.iterator();
	// iterator2.hasNext();) {
	// String date = (String) iterator2.next();
	// List<String> employeeList1 = new LinkedList<String>(employeeList);
	// for (Iterator<String> iterator3 = employeeList1.iterator();
	// iterator3.hasNext();) {
	// String employee1 = (String) iterator3.next();
	// Double average = 0.0;
	// List<String> employeeList2 = new LinkedList<String>(employeeList);
	// for (Iterator<String> iterator4 = employeeList2.iterator();
	// iterator4.hasNext();) {
	// String employee2 = (String) iterator4.next();
	// average += dao.selectSortedHistogramKLDOfTwoEmployeesOfDate(employee1,
	// employee2, date, 5, "00:00:00");
	// }
	// average /= employeeList.size() - 1;
	// dao.insertToSortedAverage("sorted_average_6", employee1, date, average);
	// }
	// }
	// }
	// }

	public void makeMatrixSymmetric(List<List<Double>> matrixRowList) {
		for (int i = 0; i < matrixRowList.size(); i++) {
			for (int j = i + 1; j < matrixRowList.get(i).size(); j++) {
				Double average = (matrixRowList.get(i).get(j) + matrixRowList.get(j).get(i)) / 2;
				matrixRowList.get(i).set(j, average);
				matrixRowList.get(j).set(i, average);
			}
		}
	}

	public void calculateAveragePerRow(List<List<Double>> matrixRowList, String tableName, List<String> proxCardList,
			String date) throws SQLException {
		for (int i = 0; i < matrixRowList.size(); i++) {
			Double average = 0.0;
			Double largestValue = -999.0;
			int largestIndex = -1;
			for (int j = 0; j < matrixRowList.get(i).size(); j++) {
				Double matrixPointData = matrixRowList.get(i).get(j);
				average += matrixPointData;
				if (matrixPointData > largestValue) {
					largestIndex = j;
					largestValue = matrixPointData;
				}
			}
			average /= matrixRowList.get(i).size() - 1;
			dao.insertToSortedAverage(tableName, proxCardList.get(i), date, average, largestValue,
					proxCardList.get(largestIndex), null);
		}
	}

	public void calculateAveragePerRow2(List<List<Double>> matrixRowList, String tableName, String proxCard,
			List<String> dateList) throws SQLException {
		for (int i = 0; i < matrixRowList.size(); i++) {
			Double average = 0.0;
			Double largestValue = -999.0;
			int largestIndex = -1;
			for (int j = 0; j < matrixRowList.get(i).size(); j++) {
				Double matrixPointData = matrixRowList.get(i).get(j);
				average += matrixPointData;
				if (matrixPointData > largestValue) {
					largestIndex = j;
					largestValue = matrixPointData;
				}
			}
			if ((matrixRowList.get(i).size() - 1) > 0) {
				average /= matrixRowList.get(i).size() - 1;
			}
			dao.insertToSortedAverage(tableName, proxCard, dateList.get(i), average, largestValue, null,
					dateList.get(largestIndex));
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

	public void reportSortedAverage(int limit, String tableName) throws SQLException {
		List<ProxSensorData> psdList = dao.selectFromSortedAverage(limit, tableName);
		for (Iterator<ProxSensorData> iterator = psdList.iterator(); iterator.hasNext();) {
			ProxSensorData proxSensorData = (ProxSensorData) iterator.next();
			System.out.println("proxCard: " + proxSensorData.getProxcard() + "\tdatetime: "
					+ proxSensorData.getDatetime() + "\taverage: " + proxSensorData.getProbability() + "\tproxCard2: "
					+ proxSensorData.getProxcard2() + "\tdatetime2: " + proxSensorData.getDatetime2() + "\tlargestValue: " + proxSensorData.getLargestValue());
		}
		System.out.println("===========================================");
	}
}
