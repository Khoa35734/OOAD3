package Ooad;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Model.MySQLConnection;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InfoDetail extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { InfoDetail frame = new InfoDetail();
	 * frame.setVisible(true); } catch (Exception e) { e.printStackTrace(); } } });
	 * }
	 */
	public void getDetailTable(int appointment_id) {
		Connection connection = MySQLConnection.getConnection();
		String sql = "select * "
				+ "from users join take on id = user_id "
				+ "where appointment_id = ?";
		int stt = 1;
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setInt(1, appointment_id);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				tableModel.addRow(new Object[] {stt, rs.getInt("id"), rs.getString("name"),
												rs.getString("phone_number")
												});
				stt++;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getReminderTable(int appId) {
		Connection connection = MySQLConnection.getConnection();
		String sql = "select * from appointment join take_rmd on id = appointment_id where id = ?";
		List<Integer> list = new ArrayList<>();
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			st.setInt(1, appId);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				list.add(rs.getInt("reminder_id"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public InfoDetail(int appointment_id, String name, String location, Date date, int start, int end) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 532, 476);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Thông tin chi tiết");
		lblNewLabel.setBounds(211, 10, 121, 20);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Tên sự kiện:");
		lblNewLabel_1.setBounds(62, 38, 80, 13);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Vị trí:");
		lblNewLabel_2.setBounds(62, 77, 45, 13);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Ngày diễn ra:");
		lblNewLabel_3.setBounds(62, 110, 80, 13);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Thời gian bắt đầu:");
		lblNewLabel_4.setBounds(62, 144, 122, 13);
		contentPane.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Thời gian kết thúc:");
		lblNewLabel_5.setBounds(62, 178, 122, 13);
		contentPane.add(lblNewLabel_5);
		
		JLabel lbName = new JLabel(name);
		lbName.setBounds(152, 40, 78, 13);
		contentPane.add(lbName);
		
		JLabel lbLocation = new JLabel(location);
		lbLocation.setBounds(152, 77, 66, 13);
		contentPane.add(lbLocation);
		
		 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
		  String strDate = formatter.format(date);  
		JLabel lbDate = new JLabel(strDate);
		lbDate.setBounds(152, 110, 66, 13);
		contentPane.add(lbDate);
		
		JLabel lbStart = new JLabel(Integer.toString(start));
		lbStart.setBounds(185, 144, 45, 13);
		contentPane.add(lbStart);
		
		JLabel lbEnd = new JLabel(Integer.toString(end));
		lbEnd.setBounds(185, 178, 45, 13);
		contentPane.add(lbEnd);
		
		JLabel lblNewLabel_6 = new JLabel("Thông tin người tham gia:");
		lblNewLabel_6.setBounds(62, 218, 168, 13);
		contentPane.add(lblNewLabel_6);
		
		table = new JTable();
		tableModel = new DefaultTableModel();
		  
		tableModel.addColumn("STT"); tableModel.addColumn("ID");
		tableModel.addColumn("Họ và tên"); tableModel.addColumn("SĐT");
		table.setModel(tableModel);
		table.setBounds(62, 242, 431, 193);
		getDetailTable(appointment_id);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(61, 241, 432, 194);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel_7 = new JLabel("Bộ nhắc:");
		lblNewLabel_7.setBounds(305, 77, 66, 13);
		contentPane.add(lblNewLabel_7);
		
		JTextArea textArea = new JTextArea();
		List<Integer> listRmdId = getReminderTable(appointment_id);
		for(int i : listRmdId) {
			String s = "";
			switch(i) {
			case 1 : s = "15 phút"; break;
			case 2 : s = "30 phút"; break;
			case 3 : s = "45 phút"; break;
			}
			textArea.append(s + "\n");
		}
		textArea.setBounds(305, 110, 187, 101);
		contentPane.add(textArea);
	}
}
