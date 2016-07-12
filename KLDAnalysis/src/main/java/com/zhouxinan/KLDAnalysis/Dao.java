package com.zhouxinan.KLDAnalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

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
					+ proxCard + "', '" + datetime + "', '" + floor + "', '" + zone + "', '" + type + "', '" + offset
					+ "')";
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

	public List<String> selectAllProxCard() throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "select distinct proxCard from prox_sensor_data;";
			results = sm.executeQuery(sql);
			List<String> proxCardList = new LinkedList<String>();
			while (results.next()) {
				proxCardList.add(results.getString("proxCard"));
			}
			return proxCardList;
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
		return null;
	}

	public List<ProxSensorData> selectAllProxSensorDataOfProxCard(String proxCard) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "select * from prox_sensor_data where proxCard = '" + proxCard + "' order by offset asc;";
			results = sm.executeQuery(sql);
			List<ProxSensorData> psdList = new LinkedList<ProxSensorData>();
			while (results.next()) {
				ProxSensorData psd = new ProxSensorData();
				psd.setFloor(results.getInt("floor"));
				psd.setZone(results.getString("zone"));
				psd.setOffset(results.getDouble("offset"));
				psdList.add(psd);
			}
			return psdList;
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
		return null;
	}

	public List<String> selectDistinctDateOfProxCard(String proxCard) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "select distinct date(datetime) as date from prox_sensor_data where proxCard = '" + proxCard
					+ "';";
			results = sm.executeQuery(sql);
			List<String> dateList = new LinkedList<String>();
			while (results.next()) {
				dateList.add(results.getString("date"));
			}
			return dateList;
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
		return null;
	}

	public List<ProxSensorData> selectByDateAndProxCard(String proxCard, String date) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT * FROM prox_sensor_data WHERE datetime BETWEEN '" + date + " 00:00:00' AND '" + date
					+ " 23:59:59' and proxCard = '" + proxCard + "';";
			results = sm.executeQuery(sql);
			List<ProxSensorData> psdList = new LinkedList<ProxSensorData>();
			while (results.next()) {
				ProxSensorData psd = new ProxSensorData();
				psd.setFloor(results.getInt("floor"));
				psd.setZone(results.getString("zone"));
				psd.setOffset(results.getDouble("offset"));
				psdList.add(psd);
			}
			return psdList;
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
		return null;
	}

	public void insertToAnalysisTable(String proxCard, String zone, String date, Integer floor, Double duration)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "INSERT INTO daily_data (proxCard,datetime,floor,zone,duration) VALUES ('" + proxCard + "', '"
					+ date + " 00:00:00', '" + floor + "', '" + zone + "', '" + duration
					+ "') ON DUPLICATE KEY UPDATE duration=duration+" + duration + ";";
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

	public void calculateProbabilityForDayAndPerson(String proxCard, String date, Double durationOfDay)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "UPDATE daily_data SET probability=duration/" + durationOfDay + " WHERE `datetime` between '"
					+ date + " 00:00:00' and '" + date + " 23:59:59' and proxCard = '" + proxCard + "';";
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
