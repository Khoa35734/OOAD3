package Ooad;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;

public class CalendarUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalendarUI frame = new CalendarUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CalendarUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 385, 376);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JCalendar calendar = new JCalendar();
		calendar.setBounds(10, 39, 323, 231);
		contentPane.add(calendar);
		
		JButton btnConfirm = new JButton("Xác nhận");
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				AssignmentDetail form2 = new AssignmentDetail(calendar.getDate() );
				form2.setVisible(true);
				
			}
		});
		btnConfirm.setBounds(139, 303, 100, 21);
		contentPane.add(btnConfirm);
		
		JButton btnMyCalendar = new JButton("Lịch của tôi");
		btnMyCalendar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MyCalendar form3 = new MyCalendar();
				form3.setVisible(true);
				
			}
		});
		btnMyCalendar.setBounds(10, 10, 113, 21);
		contentPane.add(btnMyCalendar);
	}
}
