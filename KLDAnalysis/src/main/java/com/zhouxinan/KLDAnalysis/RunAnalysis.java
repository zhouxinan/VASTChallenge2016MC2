package com.zhouxinan.KLDAnalysis;

import java.sql.SQLException;

public class RunAnalysis {
	public static void main(String[] args) throws SQLException {
		Analysis analysis = new Analysis();
		analysis.analyze();
	}
}
