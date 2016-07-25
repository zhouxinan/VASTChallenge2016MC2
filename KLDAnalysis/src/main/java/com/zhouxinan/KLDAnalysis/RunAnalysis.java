package com.zhouxinan.KLDAnalysis;

import java.io.IOException;
import java.sql.SQLException;

public class RunAnalysis {
	public static void main(String[] args) throws SQLException, IOException {
		Analysis analysis = new Analysis();
//		analysis.buildDailyDataTable2();
//		analysis.calculateKLDPerPerson(true, true);
//		analysis.calculateKLDPerPerson(false, true);
//		analysis.calculateKLDOfSortedHistogramPerPerson(true);
//		analysis.calculateAverageKLDPerDayPerPerson(true);
//		analysis.calculateAverageKLDPerDayPerPerson(false);
//		analysis.calculateAverageKLDOfSortedHistogramPerDayPerPerson();
//		analysis.calculateKLDPerDepartment(true, true);
//		analysis.calculateKLDPerDepartment(false, true);
//		analysis.calculateKLDOfSortedHistogramPerDepartment(true);
//		analysis.calculateAverageKLDPerDepartment(true);
//		analysis.calculateAverageKLDPerDepartment(false);
//		analysis.calculateAverageKLDOfSortedHistogramPerDepartment();
//		analysis.reportSortedAverage(10, "sorted_average");
//		analysis.reportSortedAverage(10, "sorted_average_2");
//		analysis.reportSortedAverage(10, "sorted_average_3");
//		analysis.reportSortedAverage(10, "sorted_average_4");
//		analysis.reportSortedAverage(10, "sorted_average_5");
//		analysis.reportSortedAverage(10, "sorted_average_6");
		analysis.divideSection();
	}
}
