package Ooad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import Model.Appointment;
import Model.MySQLConnection;
import Model.Reminder;


public class AppointmentDAO {
	public Appointment getExistGroupAppointment(String name, Date date, int startTime, int endTime) {
		Connection connection = MySQLConnection.getConnection();
		String sql = "select * from appointment where name = ? and type_appointment = ? and meeting_date = ? and start_hour = ? and end_hour = ?";
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setString(1, name);
			st.setString(2, "Nhóm");
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			format.setLenient(false);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			st.setDate(3, sqlDate);
			st.setInt(4, startTime);
			st.setInt(5, endTime);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				return new Appointment(rs.getInt("id"), rs.getString("name"),
												rs.getString("location"), rs.getDate("meeting_date"),
												rs.getInt("start_hour"), rs.getInt("end_hour"),
												rs.getString("type_appointment"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Appointment getUserExistAppointment(int userId, Date date, int startTime, int endTime) {
		Connection connection = MySQLConnection.getConnection();
		String sql = "select * "
				+ "from appointment join take on id = appointment_id "
				+ "where user_id = ? and meeting_date = ? and start_hour = ? and end_hour = ?";
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setInt(1, userId);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			format.setLenient(false);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			st.setDate(2, sqlDate);
			st.setInt(3, startTime);
			st.setInt(4, endTime);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				return new Appointment(rs.getInt("id"), rs.getString("name"),
												rs.getString("location"), rs.getDate("meeting_date"),
												rs.getInt("start_hour"), rs.getInt("end_hour"),
												rs.getString("type_appointment"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public int InsertAppointment(int userId, String name, String location, Date date, int startTime, int endTime, String typeAppointment) {
		Connection connection = MySQLConnection.getConnection();
		String sql = "insert into appointment(name, location, meeting_date, start_hour, end_hour, type_appointment) values (?, ?, ?, ?, ?, ?);";
		int isSuccess = 0;
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setString(1, name);
			st.setString(2, location);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			format.setLenient(false);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			st.setDate(3, sqlDate);
			st.setInt(4, startTime);
			st.setInt(5, endTime);
			st.setString(6, typeAppointment);
			isSuccess = st.executeUpdate();
			String sql1 = "select * from appointment order by id desc limit 1;";
			PreparedStatement st1 = connection.prepareStatement(sql1);
			ResultSet rs = st1.executeQuery();
			if(rs.next()) {
				int appointmentId = rs.getInt(1);
				String sql2 = "insert into take(user_id, appointment_id) values (?, ?);";
				PreparedStatement st2 = connection.prepareStatement(sql2);
				st2.setInt(1, userId);
				st2.setInt(2, appointmentId);
				isSuccess = st2.executeUpdate();
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	public int updateAppointment(int userId, Date date, int startTime, int endTime, String name, String location) {
		Connection connection = MySQLConnection.getConnection();
		Appointment old = getUserExistAppointment(userId, date, startTime, endTime);
		String sql = "update appointment set name = ?, location = ? where id = ?";
		int isSuccess = 0;
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setString(1, name);
			st.setString(2, location);
			st.setInt(3, old.getId());
			isSuccess = st.executeUpdate();
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	public int InsertMemberGroupAppointment(int userId, Date date, int startTime, int endTime, String name, String location) {
		Connection connection = MySQLConnection.getConnection();
		Appointment old = getExistGroupAppointment(name, date, startTime, endTime);
		String sql = "insert into take(user_id, appointment_id) values(?, ?);";
		int isSuccess = 0;
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setInt(1, userId);
			st.setInt(2, old.getId());
			isSuccess = st.executeUpdate();
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	public List<Reminder> getAllReminder(){
		Connection connection = MySQLConnection.getConnection();
		List<Reminder> list = new ArrayList<Reminder>();
		String sql = "select * from reminder";
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				list.add(new Reminder(rs.getInt("id"), rs.getString("title")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public int InsertTakeReminder(String remTitle) {
		Connection connection = MySQLConnection.getConnection();
		int remId = 0;
		switch(remTitle) {
			case "15 phút": remId = 1; break;
			case "30 phút": remId = 2; break;
			case "45 phút": remId = 3; break;
		}
		
		int isSuccess = 0; 
		try {
			String sql1 = "select * from appointment order by id desc limit 1;";
			PreparedStatement st1 = connection.prepareStatement(sql1);
			ResultSet rs = st1.executeQuery();
			if(rs.next()) {
				int appId = rs.getInt(1);
				String sql3 = "insert into take_rmd(appointment_id, reminder_id) values (?, ?);";
				PreparedStatement st3 = connection.prepareStatement(sql3);
				System.out.println(appId + " " + remId);
				st3.setInt(1, appId);
				st3.setInt(2, remId);
				isSuccess = st3.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
}
