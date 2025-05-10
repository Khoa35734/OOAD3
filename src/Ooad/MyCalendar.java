package Ooad;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Model.MySQLConnection;

import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MyCalendar extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private JLabel lblNewLabel;
	private JButton btnEdit;
	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { MyCalendar1 frame = new MyCalendar1();
	 * frame.setVisible(true); } catch (Exception e) { e.printStackTrace(); } } });
	 * }
	 */
	
	public void getAllAppointment() {
		Connection connection = MySQLConnection.getConnection();
		String sql = "select * from appointment";
		int stt = 1;
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				tableModel.addRow(new Object[] {stt, rs.getInt("id"), rs.getString("name"),
												rs.getString("location"), rs.getDate("meeting_date"),
												rs.getInt("start_hour"), rs.getInt("end_hour"),
												rs.getString("type_appointment")});
				stt++;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public int updateInfo(int appId, String name, String location, Date date, int start, int end) {
		Connection connection = MySQLConnection.getConnection();
		String sql = "update appointment set name = ?, location = ?, meeting_date = ?, start_hour = ?, end_hour = ? where id = ?";
		int isSuccess = 0;
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setString(1, name);
			st.setString(2, location);
			/* SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); */
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			st.setDate(3, sqlDate);
			st.setInt(4, start);
			st.setInt(5, end);
			st.setInt(6, appId);
			isSuccess = st.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	/**
	 * Create the frame.
	 */
	public MyCalendar() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 989, 538);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable();
		table.setBounds(24, 57, 934, 391);
		tableModel = new DefaultTableModel();
		  
		tableModel.addColumn("STT"); tableModel.addColumn("Mã sự kiện");
		tableModel.addColumn("Tên sự kiện"); tableModel.addColumn("Vị trí");
		tableModel.addColumn("Ngày diễn ra"); tableModel.addColumn("Giờ bắt đầu");
		tableModel.addColumn("Giờ kết thúc"); tableModel.addColumn("Kiểu nhóm");
		table.setModel(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(table);
		getAllAppointment();
//		contentPane.add(table);
		
		
		scrollPane.setBounds(23, 55, 935, 393);
		contentPane.add(scrollPane);
		
		lblNewLabel = new JLabel("Danh sách các buổi hẹn");
		lblNewLabel.setBounds(429, 21, 161, 13);
		contentPane.add(lblNewLabel);
		
		JButton btnDetail = new JButton("Chi tiết");
		btnDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if(selectedRow != -1) {
					int appId = (Integer)table.getValueAt(selectedRow, 1);
					String name = (String)table.getValueAt(selectedRow, 2);
					String location = (String)table.getValueAt(selectedRow, 3);
					Date date = null;
					try {
	                    Object dateValue = table.getValueAt(selectedRow, 4);
	                    if (dateValue instanceof String) {
	                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	                        date = format.parse((String) dateValue);
	                    } else if (dateValue instanceof Date) {
	                        date = (Date) dateValue;
	                    }
	                } catch (ParseException e1) {
	                    e1.printStackTrace();
	                }
					int start = (Integer)table.getValueAt(selectedRow, 5);
					int end =  (Integer)table.getValueAt(selectedRow, 6);
					InfoDetail form4 = new InfoDetail(appId, name, location, date, start, end);
					form4.setVisible(true);
				}
			}
		});
		btnDetail.setBounds(657, 470, 116, 21);
		contentPane.add(btnDetail);
		
		btnEdit = new JButton("Thay đổi thông tin");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				int appId = (Integer)table.getValueAt(selectedRow, 1);
				String name = (String)table.getValueAt(selectedRow, 2);
				String location = (String)table.getValueAt(selectedRow, 3);
				Date date = null;
				try {
                    Object dateValue = table.getValueAt(selectedRow, 4);
                    if (dateValue instanceof String) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        date = format.parse((String) dateValue);
                    } else if (dateValue instanceof Date) {
                        date = (Date) dateValue;
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
//				Date date = (Date)table.getValueAt(selectedRow, 4);
				int start = (Integer)table.getValueAt(selectedRow, 5);
				int end =  (Integer)table.getValueAt(selectedRow, 6);
				int kq = JOptionPane.showConfirmDialog(contentPane, "Bạn có muốn thay đổi thông tin chi tiết cuộc hẹn này?",
						"Xác nhận",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(kq == JOptionPane.YES_OPTION) {
					updateInfo(appId, name, location, date, start, end);
					JOptionPane.showMessageDialog(btnDetail, "Đã thay đổi thông tin thành công!");
				}
				
			}
		});
		btnEdit.setBounds(211, 470, 139, 21);
		contentPane.add(btnEdit);
	}
}
