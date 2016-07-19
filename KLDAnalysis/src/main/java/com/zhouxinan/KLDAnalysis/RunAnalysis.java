package com.zhouxinan.KLDAnalysis;

import java.io.IOException;
import java.sql.SQLException;

public class RunAnalysis {
	public static void main(String[] args) throws SQLException, IOException {
		Analysis analysis = new Analysis();
		analysis.buildDailyDataTable();
	}
}
