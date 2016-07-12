package com.zhouxinan.KLDAnalysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

public class Run {
	public static void main(String[] args) throws SQLException, FileNotFoundException, IOException, ParseException {
		JSONToMySQL jsonToMySQL = new JSONToMySQL();
		jsonToMySQL.importTable();
	}
}
