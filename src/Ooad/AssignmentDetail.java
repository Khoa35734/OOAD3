package Ooad;
//Giao diện cho việc thêm chi tiết cuộc hẹn.
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Model.Reminder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class AssignmentDetail extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNameEvent;
	private JTextField txtLocation;
	AppointmentDAO apd = new AppointmentDAO();

//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					AssignmentDetail frame = new AssignmentDetail();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	public AssignmentDetail(java.util.Date date) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 448, 232);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Chi tiết cuộc hẹn");
		lblNewLabel.setBounds(195, 10, 99, 13);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Tên sự kiện");
		lblNewLabel_1.setBounds(27, 42, 75, 13);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Vị trí");
		lblNewLabel_2.setBounds(27, 87, 55, 13);
		contentPane.add(lblNewLabel_2);	
		
		JLabel lblNewLabel_3 = new JLabel("Kiểu cuộc họp");
		lblNewLabel_3.setBounds(59, 135, 75, 13);
		contentPane.add(lblNewLabel_3);
		
		txtNameEvent = new JTextField();
		txtNameEvent.setBounds(98, 39, 96, 19);
		contentPane.add(txtNameEvent);
		txtNameEvent.setColumns(10);
		
		txtLocation = new JTextField();
		txtLocation.setBounds(98, 84, 96, 19);
		contentPane.add(txtLocation);
		txtLocation.setColumns(10);
		
		JRadioButton rdBtnDon = new JRadioButton("Đơn");
		rdBtnDon.setBounds(138, 131, 103, 21);
		rdBtnDon.setSelected(true);
		contentPane.add(rdBtnDon);
		
		JRadioButton rdBtnNhom = new JRadioButton("Nhóm");
		rdBtnNhom.setBounds(279, 131, 103, 21);
		contentPane.add(rdBtnNhom);
		
		
		
		JLabel lblNewLabel_4 = new JLabel("Thời gian bắt đầu");
		lblNewLabel_4.setBounds(231, 42, 85, 13);
		contentPane.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Thời gian kết thúc");
		lblNewLabel_5.setBounds(231, 87, 85, 13);
		contentPane.add(lblNewLabel_5);
		
		JComboBox cbbStart = new JComboBox();
		cbbStart.setModel(new DefaultComboBoxModel(new String[] {"7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"}));
		cbbStart.setBounds(339, 38, 75, 21);
		contentPane.add(cbbStart);
		
		JComboBox cbbEnd = new JComboBox();
		cbbEnd.setModel(new DefaultComboBoxModel(new String[] {"7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"}));
		cbbEnd.setBounds(339, 83, 75, 21);
		contentPane.add(cbbEnd);
		
		JButton btnConfirmDetail = new JButton("Xác nhận");
		btnConfirmDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtNameEvent.getText();
				String location = txtLocation.getText();
				int startHour = Integer.parseInt((String)cbbStart.getSelectedItem());
				int endHour =  Integer.parseInt((String)cbbEnd.getSelectedItem());
				String type = "";
				if(rdBtnDon.isSelected()) type = "Đơn";
				else if(rdBtnNhom.isSelected()) type = "Nhóm";
				if(txtNameEvent.getText().equals("") || txtLocation.getText().equals("")) {
					JOptionPane.showMessageDialog(btnConfirmDetail, "Vui lòng điền đủ thông tin!");
					return;
				}
				if(startHour >= endHour) {
					JOptionPane.showMessageDialog(btnConfirmDetail, "Giờ bắt đầu phải bé hơn giờ kết thúc!");
					return;
				}
				if(apd.getUserExistAppointment(1, date, startHour, endHour) != null) {
					int result = JOptionPane.showConfirmDialog(contentPane, "Lịch hẹn đã trùng với lịch cũ của bạn, bạn có muốn thay thế không?",
							"Xác nhận",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(result == JOptionPane.YES_OPTION) {
						apd.updateAppointment(1, date, startHour, endHour, name, location);
						JOptionPane.showMessageDialog(btnConfirmDetail, "Đã thay thế lịch hẹn thành công!");
						showForm3AndDispose();
					}			
				}
				else if(apd.getExistGroupAppointment(name, date, startHour, endHour) != null) {
					int kq = JOptionPane.showConfirmDialog(contentPane, "Lịch hẹn này trùng với 1 group meeting, wanna join in ?",
							"Xác nhận",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(kq == JOptionPane.YES_OPTION) {
						apd.InsertMemberGroupAppointment(1, date, startHour, endHour, name, location);
						JOptionPane.showMessageDialog(btnConfirmDetail, "Đã thêm lịch hẹn thành công!");
						showForm3AndDispose();
					}
				}else {
					
					apd.InsertAppointment(1, name, location, date, startHour, endHour, type);
					int kq = JOptionPane.showConfirmDialog(contentPane, "Bạn có muốn thêm bộ nhắc?",
							"Xác nhận",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(kq == JOptionPane.YES_OPTION) {
						showFormReminderAndDispose();
					}else if (kq == JOptionPane.NO_OPTION) {
						JOptionPane.showMessageDialog(btnConfirmDetail, "Thêm lịch hẹn mới thành công!");
						showForm3AndDispose();
					}
					
				}
					
			}
		});
		btnConfirmDetail.setBounds(179, 158, 85, 21);
		contentPane.add(btnConfirmDetail);
	}
	private void showForm3AndDispose() {
		MyCalendar form3 = new MyCalendar();
		form3.setVisible(true);
		this.dispose();
	}
	private void showFormReminderAndDispose() {
		ReminderUI form4 = new ReminderUI();
		form4.setVisible(true);
		this.dispose();
	}
}
