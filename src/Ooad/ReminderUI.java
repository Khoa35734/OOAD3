package Ooad;
//Giao diện quản lý lời nhắc.
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Model.Reminder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ReminderUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private AppointmentDAO apd = new AppointmentDAO();

	public static List<String> getLinesFromTextArea(JTextArea textArea) {
		List<String> lines = new ArrayList<>();
        try {
            int lineCount = textArea.getLineCount();
            for (int i = 0; i < lineCount; i++) {
                int start = textArea.getLineStartOffset(i);
                int end = textArea.getLineEndOffset(i);
                String line = textArea.getText(start, end - start);
                lines.add(line.trim()); // Loại bỏ khoảng trắng ở đầu và cuối dòng
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lines;
    }
	
	private void showForm3AndDispose() {
		MyCalendar form3 = new MyCalendar();
		form3.setVisible(true);
		this.dispose();
	}
	
	public ReminderUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 339, 324);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Bộ nhắc");
		lblNewLabel.setBounds(133, 10, 69, 13);
		contentPane.add(lblNewLabel);
		
		JComboBox cbReminder = new JComboBox();
		List<Reminder> listRmd = apd.getAllReminder();
		for(Reminder r: listRmd)
			cbReminder.addItem(r.getTitle());
		cbReminder.setBounds(44, 43, 113, 21);
		contentPane.add(cbReminder);
		JTextArea textArea = new JTextArea();
		textArea.setBounds(44, 89, 239, 149);
		contentPane.add(textArea);
		JButton btnAddReminder = new JButton("Thêm");
		btnAddReminder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> rmd = getLinesFromTextArea(textArea);
				boolean check = true;
				for(String r : rmd) {
					if(r.equals((String)cbReminder.getSelectedItem())) {
						check = false;
						break;
					}
				}
				if(check == true)
					textArea.append((String)cbReminder.getSelectedItem() + "\n");
				else JOptionPane.showMessageDialog(btnAddReminder, "Lời nhắc này đã được thêm rồi!");
			}
		});
		btnAddReminder.setBounds(195, 43, 85, 21);
		contentPane.add(btnAddReminder);
		
		JButton btnCfRmd = new JButton("Xác nhận");
		btnCfRmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> rmd = getLinesFromTextArea(textArea);
				for(String r : rmd) {
					if(!r.equals(""))
						apd.InsertTakeReminder(r);
				}
				showForm3AndDispose();
			}
		});
		btnCfRmd.setBounds(104, 256, 119, 21);
		contentPane.add(btnCfRmd);
		
		
		
		
	}
}
