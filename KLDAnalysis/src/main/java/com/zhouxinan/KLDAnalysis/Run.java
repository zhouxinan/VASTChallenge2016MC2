package com.zhouxinan.KLDAnalysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class Run {
	public static void main(String[] args) throws FileNotFoundException, SQLException {
		JSONToMySQL jsonToMySQL = new JSONToMySQL();
		try {
			jsonToMySQL.importTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
