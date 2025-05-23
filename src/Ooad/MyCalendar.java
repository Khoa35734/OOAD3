package Ooad;
/*Chức năng: Giao diện tổng quan lịch.
Hiển thị danh sách tất cả các cuộc hẹn.
Cập nhật thông tin cuộc hẹn.
Chuyển đến chi tiết cuộc hẹn.*/
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;												

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Model.MySQLConnection;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
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
	private JButton btnEdit, btnDelete, btnRefresh;
	private ArrayList<Object[]> originalData = new ArrayList<>(); // Lưu dữ liệu ban đầu
	
	
	public void getAllAppointment() {
        tableModel.setRowCount(0);
        originalData.clear(); // Xóa dữ liệu ban đầu

        Connection connection = MySQLConnection.getConnection();
        String sql = "SELECT * FROM appointment";
        int stt = 1;

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Object[] rowData = {
                        stt,
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getDate("meeting_date"),
                        rs.getInt("start_hour"),
                        rs.getInt("end_hour"),
                        rs.getString("type_appointment")
                };

                tableModel.addRow(rowData);
                originalData.add(rowData.clone()); // Lưu dữ liệu ban đầu
                stt++;
            }
        } catch (SQLException e) {
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
		setTitle("Danh sách các cuộc hẹn");
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
		tableModel.addColumn("Giờ kết thúc"); tableModel.addColumn("Kiểu cuộc hẹn");
		table.setModel(tableModel);
		
		 // Thay đổi màu nền tiêu đề cột (Vàng nhạt)
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 255, 153)); // Màu vàng nhạt
        header.setForeground(Color.BLACK); // Màu chữ đen

		JScrollPane scrollPane = new JScrollPane(table);
		getAllAppointment();
	//	contentPane.add(table);
		
		
		scrollPane.setBounds(23, 55, 935, 393);
		contentPane.add(scrollPane);
		
		lblNewLabel = new JLabel("Danh sách các cuộc hẹn");
		lblNewLabel.setBounds(429, 21, 161, 13);
		lblNewLabel.setForeground(Color.RED);  // Màu đỏ
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
		btnDetail.setBackground(new Color(34, 139, 34));  // Xanh lá cây
        btnDetail.setForeground(Color.WHITE);
		contentPane.add(btnDetail);
		
		btnEdit = new JButton("Lưu thay đổi");
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
		 btnEdit.setBackground(new Color(34, 139, 34));  // Xanh lá cây
	     btnEdit.setForeground(Color.WHITE);
		contentPane.add(btnEdit);
		
		  // Nút Xóa
        btnDelete = new JButton("Xóa");
        btnDelete.setBounds(380, 470, 100, 21);
        btnDelete.setBackground(new Color(34, 139, 34));
        btnDelete.setForeground(Color.WHITE);
        contentPane.add(btnDelete);

        // Nút Làm mới
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setBounds(520, 470, 100, 21);
        btnRefresh.setBackground(new Color(34, 139, 34));
        btnRefresh.setForeground(Color.WHITE);
        contentPane.add(btnRefresh);
        
        // Sự kiện nút "Xóa"
        btnDelete.addActionListener(e -> deleteAppointment());

        // Sự kiện nút "Làm mới"
        btnRefresh.addActionListener(e -> refreshTable());
	}

	private void deleteAppointment() {
	    int selectedRow = table.getSelectedRow();
	    
	    if (selectedRow != -1) {
	        Object idValue = table.getValueAt(selectedRow, 1);

	        if (idValue != null && idValue instanceof Integer) {
	            int appId = (Integer) idValue;

	            int confirm = JOptionPane.showConfirmDialog(
	                this,
	                "Bạn có chắc chắn muốn xóa cuộc hẹn này?",
	                "Xác nhận xóa",
	                JOptionPane.YES_NO_OPTION
	            );

	            if (confirm == JOptionPane.YES_OPTION) {
	                Connection connection = MySQLConnection.getConnection();

	                try {
	                    // Xóa các dòng trong take_rmd trước
	                    String sqlDeleteTakeRmd = "DELETE FROM take_rmd WHERE appointment_id = ?";
	                    PreparedStatement st1 = connection.prepareStatement(sqlDeleteTakeRmd);
	                    st1.setInt(1, appId);
	                    st1.executeUpdate();

	                    // Xóa dữ liệu trong appointment
	                    String sqlDeleteAppointment = "DELETE FROM appointment WHERE id = ?";
	                    PreparedStatement st2 = connection.prepareStatement(sqlDeleteAppointment);
	                    st2.setInt(1, appId);
	                    int result = st2.executeUpdate();

	                    if (result > 0) {
	                        JOptionPane.showMessageDialog(this, "Xóa thành công!");
	                        refreshTable();
	                    } else {
	                        JOptionPane.showMessageDialog(this, "Không tìm thấy cuộc hẹn để xóa!");
	                    }

	                } catch (SQLException e) {
	                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + e.getMessage());
	                }
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "Dữ liệu ID không hợp lệ!");
	        }
	    } else {
	        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để xóa!");
	    }
	}
	private void refreshTable() {
        if (isDataChanged()) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có thay đổi chưa được lưu. Bạn có muốn làm mới và hủy các thay đổi không?",
                    "Thay đổi chưa lưu",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                getAllAppointment(); // Tải lại dữ liệu ban đầu
                JOptionPane.showMessageDialog(this, "Bảng đã được làm mới!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không có thay đổi để làm mới!");
        }
    }

    private boolean isDataChanged() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object[] rowData = originalData.get(i);

            for (int j = 0; j < rowData.length; j++) {
                Object currentValue = tableModel.getValueAt(i, j);
                if (!currentValue.equals(rowData[j])) {
                    return true;
                }
            }
        }
        return false;
    }
}
