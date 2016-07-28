package com.zhouxinan.KLDAnalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
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

	public List<String> selectAllProxCardByHour(int hour) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT distinct(proxCard) from daily_data_by_section where HOUR(datetime) = " + hour + ";";
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

	public List<String> selectDistinctDateOfProxCardAndHour(String proxCard, int hour) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "select distinct date(datetime) as date from daily_data_by_section where proxCard = '"
					+ proxCard + "' and HOUR(datetime) = '" + hour + "';";
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

	public List<ProxSensorData> selectByProxCardAndDate(String proxCard, String date) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT * FROM prox_sensor_data WHERE datetime BETWEEN '" + date + " 00:00:00' AND '" + date
					+ " 23:59:59' and proxCard = '" + proxCard + "' order by offset asc;";
			results = sm.executeQuery(sql);
			List<ProxSensorData> psdList = new LinkedList<ProxSensorData>();
			while (results.next()) {
				ProxSensorData psd = new ProxSensorData();
				psd.setFloor(results.getInt("floor"));
				psd.setZone(results.getString("zone"));
				psd.setOffset(results.getDouble("offset"));
				psd.setDatetime(results.getTimestamp("datetime"));
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

	public List<ProxSensorData> selectByProxCardAndDate(String proxCard, String date, String startTime, String endTime)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT * FROM prox_sensor_data WHERE datetime BETWEEN '" + date + " " + startTime + "' AND '"
					+ date + " " + endTime + "' and proxCard = '" + proxCard + "' order by offset asc;";
			results = sm.executeQuery(sql);
			List<ProxSensorData> psdList = new LinkedList<ProxSensorData>();
			while (results.next()) {
				ProxSensorData psd = new ProxSensorData();
				psd.setFloor(results.getInt("floor"));
				psd.setZone(results.getString("zone"));
				psd.setOffset(results.getDouble("offset"));
				psd.setDatetime(results.getTimestamp("datetime"));
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

	public List<ProxSensorData> selectByProxCardAndDateFromDailyData2(String proxCard, String date, String startTime,
			String endTime) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT * FROM daily_data_2 WHERE datetime BETWEEN '" + date + " " + startTime + "' AND '"
					+ date + " " + endTime + "' and proxCard = '" + proxCard + "' order by datetime asc;";
			results = sm.executeQuery(sql);
			List<ProxSensorData> psdList = new LinkedList<ProxSensorData>();
			while (results.next()) {
				ProxSensorData psd = new ProxSensorData();
				psd.setFloor(results.getInt("floor"));
				psd.setZone(results.getString("zone"));
				psd.setDuration(results.getDouble("duration"));
				psd.setDatetime(results.getTimestamp("datetime"));
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

	public void insertToAnalysisTable2(String proxCard, String zone, Date datetime, Integer floor, Double duration)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "INSERT INTO daily_data_2 (proxCard,datetime,floor,zone,duration) VALUES ('" + proxCard
					+ "', '" + datetime + "', '" + floor + "', '" + zone + "', '" + duration + "');";
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

	public void insertToDailyDataBySection(String proxCard, String zone, String datetime, Integer floor,
			Double duration) throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "INSERT INTO daily_data_by_section (proxCard,datetime,floor,zone,duration) VALUES ('"
					+ proxCard + "', '" + datetime + "', '" + floor + "', '" + zone + "', '" + duration
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

	public void calculateProbabilityForDayAndPerson(String proxCard, String date, Double durationOfDay,
			String startTime, String endTime) throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "UPDATE daily_data SET probability=duration/" + durationOfDay + " WHERE `datetime` between '"
					+ date + " " + startTime + "' and '" + date + " " + endTime + "' and proxCard = '" + proxCard
					+ "';";
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

	public void calculateProbabilityForDayAndPersonAndSection(String proxCard, String date, Double durationOfSection,
			String sectionStartTime) throws SQLException {
		Connection con = null;
		Statement sm = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "UPDATE daily_data_by_section SET probability=duration/" + durationOfSection
					+ " WHERE `datetime` = '" + date + " " + sectionStartTime + "' and proxCard = '" + proxCard + "';";
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

	public Double selectJSDOfTwoDatesOfProxCard(String proxCard, String date1, String date2, String time,
			String tableName) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT (sum(a_prob*(log2(a_prob)-log2(m_prob))) + sum(b_prob*(log2(b_prob)-log2(m_prob))))/2  as JSD from (SELECT ifnull(A.probability, 0.00000001) as a_prob, ifnull(B.probability, 0.00000001) as b_prob, (ifnull(A.probability, 0.00000001)+ifnull(B.probability, 0.00000001))/2 as m_prob from ((select * from "
					+ tableName + " where datetime='" + date1 + " " + time + "' and proxCard = '" + proxCard
					+ "') as A left join (SELECT * from " + tableName + " where datetime='" + date2 + " " + time
					+ "' and proxCard = '" + proxCard
					+ "') as B on A.floor = B.floor and A.zone = B.zone) UNION SELECT ifnull(C.probability, 0.00000001) as a_prob, ifnull(D.probability, 0.00000001) as b_prob, (ifnull(C.probability, 0.00000001)+ifnull(D.probability, 0.00000001))/2 as m_prob from ((select * from "
					+ tableName + " where datetime='" + date1 + " " + time + "' and proxCard = '" + proxCard
					+ "') as C right join (SELECT * from " + tableName + " where datetime='" + date2 + " " + time
					+ "' and proxCard = '" + proxCard + "') as D on C.floor = D.floor and C.zone = D.zone)) as E";
			results = sm.executeQuery(sql);
			if (results.next()) {
				return results.getDouble("JSD");
			}
			return null;
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

	public Double selectKLDOfTwoDatesOfProxCard(String proxCard, String date1, String date2, String time,
			String tableName) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT SUM(KLDi) as KLD from (SELECT IFNULL(A.probability, 0.00000001)*(log2(IFNULL(A.probability, 0.00000001))-log2(IFNULL(B.probability, 0.00000001))) as KLDi from ((SELECT * from "
					+ tableName + " where datetime='" + date1 + " " + time + "' and proxCard = '" + proxCard
					+ "') as A LEFT JOIN (SELECT * from " + tableName + " where datetime='" + date2 + " " + time
					+ "' and proxCard = '" + proxCard
					+ "') as B on A.floor = B.floor and A.zone = B.zone) UNION SELECT IFNULL(A.probability, 0.00000001)*(log2(IFNULL(A.probability, 0.00000001))-log2(IFNULL(B.probability, 0.00000001))) as KLDi from ((SELECT * from "
					+ tableName + " where datetime='" + date1 + " " + time + "' and proxCard = '" + proxCard
					+ "') as A RIGHT JOIN (SELECT * from " + tableName + " where datetime='" + date2 + " " + time
					+ "' and proxCard = '" + proxCard + "') as B on A.floor = B.floor and A.zone = B.zone)) as C";
			results = sm.executeQuery(sql);
			if (results.next()) {
				return results.getDouble("KLD");
			}
			return null;
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

	public Double selectKLDOfTwoDatesOfProxCardInnerJoin(String proxCard, String date1, String date2, String time,
			String tableName) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT SUM(A.probability*(log2(A.probability)-log2(B.probability))) as KLD from ((SELECT * from "
					+ tableName + " where datetime='" + date1 + " " + time + "' and proxCard = '" + proxCard
					+ "') as A INNER JOIN (SELECT * from " + tableName + " where datetime='" + date2 + " " + time
					+ "' and proxCard = '" + proxCard + "') as B on A.floor = B.floor and A.zone = B.zone)";
			results = sm.executeQuery(sql);
			if (results.next()) {
				return results.getDouble("KLD");
			}
			return null;
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

	public List<String> selectAllDepartments() throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT DISTINCT department from employee;";
			results = sm.executeQuery(sql);
			List<String> departmentList = new LinkedList<String>();
			while (results.next()) {
				departmentList.add(results.getString("department"));
			}
			return departmentList;
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

	public List<String> selectAllEmployeesOfDepartment(String department) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT * from employee where department = '" + department + "'";
			results = sm.executeQuery(sql);
			List<String> employeeList = new LinkedList<String>();
			while (results.next()) {
				employeeList.add(results.getString("name"));
			}
			return employeeList;
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

	public List<String> selectAllProxCardOfDepartment(String department) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "select distinct proxCard from daily_data as B inner join (select * from employee where department = '"
					+ department + "') as A on substring(proxCard, 1, length(proxCard) - 3) = A.name";
			results = sm.executeQuery(sql);
			List<String> employeeList = new LinkedList<String>();
			while (results.next()) {
				employeeList.add(results.getString("proxCard"));
			}
			return employeeList;
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

	public List<String> selectDistinctDateForDepartment(String department) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT distinct date(datetime) as date from ((SELECT * from employee where department = '"
					+ department + "') as A inner join daily_data on daily_data.proxCard = A.name)";
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

	public List<String> selectDistinctDateForDepartmentProxCard(String department) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT distinct date(datetime) as date from daily_data as B inner join (SELECT * from employee where department = '"
					+ department + "') as A on substring(proxCard, 1, length(proxCard) - 3) = A.name";
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

	public Double selectKLDOfTwoEmployeesOfDate(String employee1, String employee2, String date, String time)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT SUM(KLDi) as KLD from (SELECT IFNULL(A.probability, 0.00000001)*(log2(IFNULL(A.probability, 0.00000001))-log2(IFNULL(B.probability, 0.00000001))) as KLDi from ((SELECT * from daily_data where datetime='"
					+ date + " " + time + "' and proxCard = '" + employee1
					+ "') as A LEFT JOIN (SELECT * from daily_data where datetime='" + date + " " + time
					+ "' and proxCard = '" + employee2
					+ "') as B on A.floor = B.floor and A.zone = B.zone) UNION SELECT IFNULL(A.probability, 0.00000001)*(log2(IFNULL(A.probability, 0.00000001))-log2(IFNULL(B.probability, 0.00000001))) as KLDi from ((SELECT * from daily_data where datetime='"
					+ date + " " + time + "' and proxCard = '" + employee1
					+ "') as A RIGHT JOIN (SELECT * from daily_data where datetime='" + date + " " + time
					+ "' and proxCard = '" + employee2 + "') as B on A.floor = B.floor and A.zone = B.zone)) as C";
			results = sm.executeQuery(sql);
			if (results.next()) {
				return results.getDouble("KLD");
			}
			return null;
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

	public Double selectKLDOfTwoEmployeesOfDateInnerJoin(String employee1, String employee2, String date, String time)
			throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT SUM(A.probability*(log2(A.probability)-log2(B.probability))) as KLD from ((SELECT * from daily_data where datetime='"
					+ date + " " + time + "' and proxCard = '" + employee1
					+ "') as A INNER JOIN (SELECT * from daily_data where datetime='" + date + " " + time
					+ "' and proxCard = '" + employee2 + "') as B ON A.floor = B.floor and A.zone = B.zone)";
			results = sm.executeQuery(sql);
			if (results.next()) {
				return results.getDouble("KLD");
			}
			return null;
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

	public Double selectSortedHistogramKLDOfTwoEmployeesOfDate(String employee1, String employee2, String date,
			int listSizeLimit, String time) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql1 = "SELECT * from daily_data where datetime='" + date + " " + time + "' and proxCard = '"
					+ employee1 + "' ORDER BY duration DESC";
			results = sm.executeQuery(sql1);
			List<Double> list1 = new LinkedList<Double>();
			while (results.next()) {
				list1.add(results.getDouble("probability"));
			}
			results.close();
			String sql2 = "SELECT * from daily_data where datetime='" + date + " " + time + "' and proxCard = '"
					+ employee2 + "' ORDER BY duration DESC";
			results = sm.executeQuery(sql2);
			List<Double> list2 = new LinkedList<Double>();
			while (results.next()) {
				list2.add(results.getDouble("probability"));
			}
			results.close();
			Double KLD = 0.0;
			int smallerListSize = (list1.size() < list2.size()) ? list1.size() : list2.size();
			if (smallerListSize > listSizeLimit) {
				smallerListSize = listSizeLimit;
			}
			for (int i = 0; i < smallerListSize; i++) {
				KLD += list1.get(i) * ((Math.log(list1.get(i)) - Math.log(list2.get(i))) / Math.log(2));
			}
			return KLD;
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

	public Double selectSortedHistogramKLDOfTwoDatesOfProxCard(String proxCard, String date1, String date2,
			int listSizeLimit, String time) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql1 = "SELECT * from daily_data where datetime='" + date1 + " " + time + "' and proxCard = '"
					+ proxCard + "' ORDER BY duration DESC";
			results = sm.executeQuery(sql1);
			List<Double> list1 = new LinkedList<Double>();
			while (results.next()) {
				list1.add(results.getDouble("probability"));
			}
			results.close();
			String sql2 = "SELECT * from daily_data where datetime='" + date2 + " " + time + "' and proxCard = '"
					+ proxCard + "' ORDER BY duration DESC";
			results = sm.executeQuery(sql2);
			List<Double> list2 = new LinkedList<Double>();
			while (results.next()) {
				list2.add(results.getDouble("probability"));
			}
			results.close();
			Double KLD = 0.0;
			int smallerListSize = (list1.size() < list2.size()) ? list1.size() : list2.size();
			if (smallerListSize > listSizeLimit) {
				smallerListSize = listSizeLimit;
			}
			for (int i = 0; i < smallerListSize; i++) {
				KLD += list1.get(i) * ((Math.log(list1.get(i)) - Math.log(list2.get(i))) / Math.log(2));
			}
			return KLD;
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

	public void insertToSortedAverage(String table, String proxCard, String date, Double average, Double largestValue,
			String proxCard2, String datetime2) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			String sql = "insert into " + table
					+ "(proxCard, datetime, average, largestValue, proxCard2, datetime2) values(?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, proxCard);
			ps.setString(2, date + " 00:00:00");
			ps.setDouble(3, average);
			ps.setDouble(4, largestValue);
			ps.setString(5, proxCard2);
			ps.setString(6, datetime2);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

	public List<ProxSensorData> selectFromSortedAverage(int limit, String tableName) throws SQLException {
		Connection con = null;
		Statement sm = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			sm = con.createStatement();
			String sql = "SELECT * from " + tableName + " order by average desc limit " + limit + ";";
			results = sm.executeQuery(sql);
			List<ProxSensorData> list = new LinkedList<ProxSensorData>();
			while (results.next()) {
				ProxSensorData psd = new ProxSensorData();
				psd.setProxcard(results.getString("proxCard"));
				psd.setDatetime(results.getDate("datetime"));
				psd.setProbability(results.getDouble("average"));
				psd.setProxcard2(results.getString("proxCard2"));
				psd.setDatetime2(results.getDate("datetime2"));
				psd.setLargestValue(results.getDouble("largestValue"));
				list.add(psd);
			}
			return list;
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
}
