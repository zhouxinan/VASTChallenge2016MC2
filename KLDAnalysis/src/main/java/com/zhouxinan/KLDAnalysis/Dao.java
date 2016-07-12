package com.zhouxinan.KLDAnalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Dao {
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://127.0.0.1:3306/Analysis?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
	private static String dbUsername = "root";
	private static String dbPassword = "mohamade";
	private static Dao dao;

	private Dao() {
	}

	public static Dao getInstance() {
		if (dao == null) {
			dao = new Dao();
		}
		return dao;
	}

	static {
		try {
			Class.forName(driver).newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertRowToTable(String proxCard, String zone, String datetime, Integer floor, int type, Double offset)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "insert into prox_sensor_data(proxCard, datetime, floor, zone, type, offset) values('"
					+ proxCard + "', '"  + datetime + "', '" + floor + "', '" + zone + "', '" + type + "', '" + offset + "')";
			sm.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sm != null) {
				sm.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}
}
