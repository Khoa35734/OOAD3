package Ooad;
//Giao diện chính của ứng dụng lịch.
import java.awt.*;

import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CalendarUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel topPane;
	private JPanel contentPane; 
	private JPanel calendarPanel;
	private JLabel monthLabel;
	private Calendar currentMonth;
	private JPanel bottomPane;
	private Connection conn;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				CalendarUI frame = new CalendarUI();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	private void styleButton(JButton button) {
	    button.setBackground(new Color(34, 139, 34));
	    button.setForeground(Color.WHITE);
	    button.setFocusPainted(false);
	    button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
	    button.setFont(new Font("Arial", Font.PLAIN, 14));
	    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

	    button.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            button.setBackground(new Color(50, 205, 50));
	        }

	        @Override
	        public void mouseExited(MouseEvent e) {
	            button.setBackground(new Color(34, 139, 34));
	        }
	    });
	}

	public CalendarUI() {
		setTitle("MYCalendar");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout(10, 10));
		setContentPane(contentPane);

		 JPanel headerPanel = new JPanel(new BorderLayout(10,0));
		 headerPanel.setBackground(new Color(255, 192, 203)); // Hồng nhạt
		 headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10)); // Tạo khoảng cách bên trái và phải
		 topPane = new JPanel(new BorderLayout());
		 topPane.setBackground(new Color(255, 192, 203));

		 // Button "Lịch của tôi" căn phải
		 JButton myCalendarButton = new JButton("Lịch của tôi");
		 myCalendarButton.setBackground(new Color(34, 139, 34)); 
		 myCalendarButton.setForeground(Color.WHITE);
		 myCalendarButton.setFocusPainted(false);
		 topPane.add(myCalendarButton, BorderLayout.EAST);

		 headerPanel.add(topPane, BorderLayout.NORTH);
		// Add gap between topPane and navigationPanel
		 headerPanel.add(Box.createRigidArea(new Dimension(0, 15)), BorderLayout.CENTER);


		 
		// Navigation Panel
		 JPanel navigationPanel = new JPanel(new BorderLayout());
		 navigationPanel.setBackground(new Color(255, 192, 203)); // Hồng nhạt
		 JButton prevButton = new JButton("<");
		 JButton nextButton = new JButton(">");
		 styleButton(prevButton);
		 styleButton(nextButton);

		 monthLabel = new JLabel("", SwingConstants.CENTER);
		 monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
		 monthLabel.setForeground(Color.BLACK);

		 navigationPanel.add(prevButton, BorderLayout.WEST);
		 navigationPanel.add(monthLabel, BorderLayout.CENTER);
		 navigationPanel.add(nextButton, BorderLayout.EAST);
		 headerPanel.add(navigationPanel, BorderLayout.SOUTH);
		 
		contentPane.add(headerPanel, BorderLayout.NORTH);
		
		// Calendar Panel
		calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
		calendarPanel.setBackground(Color.WHITE); // Màu nền trắng
		calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPane.add(calendarPanel, BorderLayout.CENTER);
		
		//BottomPane
		bottomPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPane.setBackground(new Color(245, 245, 245)); 
		JButton addEventButton = new JButton("Thêm sự kiện");	
		JButton closeButton = new JButton("Đóng");
		styleButton(addEventButton);
		styleButton(closeButton);
		bottomPane.add(addEventButton);
		bottomPane.add(closeButton);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		
		//AddEvent for button
		myCalendarButton.addActionListener(e -> {
			MyCalendar myCalendar = new MyCalendar();
			myCalendar.setVisible(true);
		});
		
		addEventButton.addActionListener(e -> showAddEventDialog(false, null));
		
		closeButton.addActionListener(e -> {
		    int confirm = JOptionPane.showConfirmDialog(
		        this,
		        "Bạn có chắc chắn muốn thoát chương trình?",
		        "Xác nhận thoát",
		        JOptionPane.YES_NO_OPTION
		    );

		    if (confirm == JOptionPane.YES_OPTION) {
		        System.exit(0);
		    }
		});

		// Database Connection
		connectToDatabase();

		// Initialize Current Month
		currentMonth = Calendar.getInstance();
		updateCalendarView();

		// Event Listeners for Navigation
		prevButton.addActionListener(e -> {
			currentMonth.add(Calendar.MONTH, -1);
			updateCalendarView();
		});

		nextButton.addActionListener(e -> {
			currentMonth.add(Calendar.MONTH, 1);
			updateCalendarView();
		});
		
		// chỉnh màu button
		addEventButton.setBackground(new Color(34, 139, 34)); 
		addEventButton.setForeground(Color.WHITE);

		closeButton.setBackground(new Color(34, 139, 34)); 
		closeButton.setForeground(Color.WHITE);

		prevButton.setBackground(new Color(34, 139, 34)); 
		prevButton.setForeground(Color.WHITE);

		nextButton.setBackground(new Color(34, 139, 34)); 
		nextButton.setForeground(Color.WHITE);

	}

	private void connectToDatabase() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ooad", "root", "root");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + e.getMessage());
		}
	}

	private void updateCalendarView() {
		calendarPanel.removeAll();

		// Add Weekdays
		String[] weekdays = { "CN", "T2", "T3", "T4", "T5", "T6", "T7" };
		for (String weekday : weekdays) {
			JLabel label = new JLabel(weekday, SwingConstants.CENTER);
			label.setFont(new Font("Arial", Font.BOLD, 12));
			calendarPanel.add(label);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
		monthLabel.setText(sdf.format(currentMonth.getTime()));

		// Get Start of Month
		Calendar cal = (Calendar) currentMonth.clone();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		// Get today's date
		Calendar today = Calendar.getInstance();
		int todayDay = today.get(Calendar.DAY_OF_MONTH);
		int todayMonth = today.get(Calendar.MONTH);
		int todayYear = today.get(Calendar.YEAR);

		boolean isCurrentMonth = (todayMonth == currentMonth.get(Calendar.MONTH) && todayYear == currentMonth.get(Calendar.YEAR));

		// Add Empty Cells for Days Before Start
		for (int i = 1; i < startDayOfWeek; i++) {
			calendarPanel.add(new JLabel());
		}

		// Add Days with Appointments
		for (int day = 1; day <= daysInMonth; day++) {
			JButton dayButton = new JButton(String.valueOf(day));
			dayButton.setHorizontalAlignment(SwingConstants.LEFT);
			dayButton.setVerticalAlignment(SwingConstants.TOP);

			// Highlight today's date in green
			if (isCurrentMonth && day == todayDay) {
				dayButton.setBackground(Color.YELLOW);
				dayButton.setOpaque(true);
				dayButton.setBorderPainted(false);
			}

			cal.set(Calendar.DAY_OF_MONTH, day);
			java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
			List<String> events = getAppointmentsForDate(sqlDate);

			if (!events.isEmpty()) {
				JPanel dayPanel = new JPanel(new BorderLayout());
				dayPanel.add(dayButton, BorderLayout.NORTH);

				JPanel eventsPanel = new JPanel(new GridLayout(events.size(), 1, 2, 2));
				for (String event : events) {
					JLabel eventLabel = new JLabel(event);
					eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
					eventLabel.setForeground(Color.BLUE);
					eventLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					eventLabel.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							showAppointmentDetail(event, sqlDate);
						}
					});
					eventsPanel.add(eventLabel);
				}
				dayPanel.add(eventsPanel, BorderLayout.CENTER);
				calendarPanel.add(dayPanel);
			} else {
				calendarPanel.add(dayButton);
			}
		}

		calendarPanel.revalidate();
		calendarPanel.repaint();
	}

	private List<String> getAppointmentsForDate(java.sql.Date date) {
		List<String> events = new ArrayList<>();
		if (conn == null) return events;

		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT name FROM appointment WHERE DATE(meeting_date) = ? ORDER BY start_hour")) {
			stmt.setDate(1, date);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				events.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi truy vấn: " + e.getMessage());
		}

		return events;
	}

	private void showAppointmentDetail(String eventName, java.sql.Date date) {
		if (conn == null) return;

		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT * FROM appointment WHERE name = ? AND DATE(meeting_date) = ?")) {
			stmt.setString(1, eventName);
			stmt.setDate(2, date);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String detail = String.format(
						"Tên: %s\nĐịa điểm: %s\nNgày: %s\nGiờ: %dh - %dh\nLoại: %s",
						rs.getString("name"),
						rs.getString("location"),
						rs.getDate("meeting_date"),
						rs.getInt("start_hour"),
						rs.getInt("end_hour"),
						rs.getString("type_appointment")
				);

				Object[] options = { "Sửa", "Xóa", "OK" };
				int choice = JOptionPane.showOptionDialog(this, detail, "Chi tiết cuộc hẹn",
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

				if (choice == 0) { // Edit
					showAddEventDialog(true, eventName);
				} else if (choice == 1) { // Delete
					int confirm = JOptionPane.showConfirmDialog(this,
							"Bạn có chắc muốn xóa sự kiện này không?", "Xác nhận xóa",
							JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						deleteEvent(eventName);
						updateCalendarView();
					}
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xem chi tiết: " + e.getMessage());
		}
	}

	private void deleteEvent(String eventName) {
		if (conn == null) return;

		try {
			// Get the appointment ID
			int appointmentId = -1;
			try (PreparedStatement stmt = conn.prepareStatement(
					"SELECT id FROM appointment WHERE name = ?")) {
				stmt.setString(1, eventName);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					appointmentId = rs.getInt("id");
				}
			}

			if (appointmentId != -1) {
				// Delete related reminders in take_rmd
				try (PreparedStatement stmt = conn.prepareStatement(
						"DELETE FROM take_rmd WHERE appointment_id = ?")) {
					stmt.setInt(1, appointmentId);
					stmt.executeUpdate();
				}

				// Delete the appointment
				try (PreparedStatement stmt = conn.prepareStatement(
						"DELETE FROM appointment WHERE id = ?")) {
					stmt.setInt(1, appointmentId);
					stmt.executeUpdate();
				}

				JOptionPane.showMessageDialog(this, "Sự kiện đã được xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Không tìm thấy sự kiện cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xóa sự kiện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showAddEventDialog(boolean isUpdate, String existingEventName) {
		JTextField nameField = new JTextField(existingEventName != null ? existingEventName : "");
		JTextField locationField = new JTextField();
		JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

		// ComboBox for Start and End Hour (7-24)
		JComboBox<Integer> startHourCombo = new JComboBox<>();
		JComboBox<Integer> endHourCombo = new JComboBox<>();
		for (int i = 7; i <= 24; i++) {
			startHourCombo.addItem(i);
			endHourCombo.addItem(i);
		}

		// ComboBox for Type (Đơn, Nhóm)
		JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Đơn", "Nhóm" });

		// ComboBox for Reminder (1 tiếng, 30 phút, 15 phút)
		JComboBox<String> reminderCombo = new JComboBox<>(new String[] { "1 tiếng", "30 phút", "15 phút" });

		Object[] message = {
				"Tên:", nameField,
				"Địa điểm:", locationField,
				"Ngày (yyyy-MM-dd):", dateField,
				"Giờ bắt đầu:", startHourCombo,
				"Giờ kết thúc:", endHourCombo,
				"Loại:", typeCombo,
				"Bộ nhắc:", reminderCombo
		};

		int option = JOptionPane.showConfirmDialog(this, message, isUpdate ? "Cập nhật sự kiện" : "Thêm sự kiện",
				JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			if (nameField.getText().isEmpty() || locationField.getText().isEmpty() || dateField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Tất cả các trường phải được điền đầy đủ!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				String newName = nameField.getText();
				String newLocation = locationField.getText();
				java.sql.Date meetingDate = java.sql.Date.valueOf(dateField.getText());
				int startHour = (int) startHourCombo.getSelectedItem();
				int endHour = (int) endHourCombo.getSelectedItem();
				String newType = (String) typeCombo.getSelectedItem();
				String reminder = (String) reminderCombo.getSelectedItem();

				if (startHour >= endHour) {
					JOptionPane.showMessageDialog(this, "Giờ bắt đầu phải trước giờ kết thúc!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Check for duplicate name and date
				if (checkDuplicateNameAndDate(newName, meetingDate, existingEventName)) {
					JOptionPane.showMessageDialog(this, "Đã có sự kiện trùng tên và ngày!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Check for time conflict
				if (checkTimeConflict(meetingDate, startHour, endHour, existingEventName)) {
					int replaceOption = JOptionPane.showConfirmDialog(this,
							"Đã có lịch hẹn trùng thời gian. Bạn có muốn thay thế không?", "Xung đột lịch",
							JOptionPane.YES_NO_OPTION);

					if (replaceOption == JOptionPane.NO_OPTION) {
						return; // Cancel the operation
					} else {
						// Replace the existing conflicting events
						deleteConflictingEvents(meetingDate, startHour, endHour);
					}
				}

				if (isUpdate) {
					try (PreparedStatement stmt = conn.prepareStatement(
							"UPDATE appointment SET name = ?, location = ?, meeting_date = ?, start_hour = ?, end_hour = ?, type_appointment = ? WHERE name = ?")) {
						stmt.setString(1, newName);
						stmt.setString(2, newLocation);
						stmt.setDate(3, meetingDate);
						stmt.setInt(4, startHour);
						stmt.setInt(5, endHour);
						stmt.setString(6, newType);
						stmt.setString(7, existingEventName);
						stmt.executeUpdate();
					}
				} else {
					try (PreparedStatement stmt = conn.prepareStatement(
							"INSERT INTO appointment (name, location, meeting_date, start_hour, end_hour, type_appointment) VALUES (?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
						stmt.setString(1, newName);
						stmt.setString(2, newLocation);
						stmt.setDate(3, meetingDate);
						stmt.setInt(4, startHour);
						stmt.setInt(5, endHour);
						stmt.setString(6, newType);
						stmt.executeUpdate();

						// Get Generated Appointment ID
						ResultSet rs = stmt.getGeneratedKeys();
						if (rs.next() && reminder != null && !reminder.isEmpty()) {
							int appointmentId = rs.getInt(1);
							addReminder(appointmentId, reminder);
						}
					}
				}

				updateCalendarView();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi thêm/cập nhật sự kiện: " + e.getMessage());
			}
		}
	}
	private void addReminder(int appointmentId, String reminder) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO reminder (title) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, reminder);
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int reminderId = rs.getInt(1);

				try (PreparedStatement linkStmt = conn.prepareStatement(
						"INSERT INTO take_rmd (appointment_id, reminder_id) VALUES (?, ?)")) {
					linkStmt.setInt(1, appointmentId);
					linkStmt.setInt(2, reminderId);
					linkStmt.executeUpdate();
				}
			}
		}
	}

	private boolean checkDuplicateNameAndDate(String name, java.sql.Date date, String existingEventName) {
		if (conn == null) return false;

		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT name FROM appointment WHERE name = ? AND meeting_date = ? AND name != ?")) {
			stmt.setString(1, name);
			stmt.setDate(2, date);
			stmt.setString(3, existingEventName != null ? existingEventName : "");
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi kiểm tra trùng sự kiện: " + e.getMessage());
		}

		return false;
	}

	private boolean checkTimeConflict(java.sql.Date date, int startHour, int endHour, String existingEventName) {
		if (conn == null) return false;

		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT name FROM appointment WHERE meeting_date = ? AND ((start_hour < ? AND end_hour > ?) OR (start_hour < ? AND end_hour > ?)) AND name != ?")) {
			stmt.setDate(1, date);
			stmt.setInt(2, endHour);
			stmt.setInt(3, startHour);
			stmt.setInt(4, startHour);
			stmt.setInt(5, endHour);
			stmt.setString(6, existingEventName != null ? existingEventName : "");
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi kiểm tra xung đột thời gian: " + e.getMessage());
		}

		return false;
	}

	private void deleteConflictingEvents(java.sql.Date date, int startHour, int endHour) {
		if (conn == null) return;

		try {
			// Step 1: Get a list of IDs for conflicting events
			List<Integer> appointmentIds = new ArrayList<>();
			try (PreparedStatement stmt = conn.prepareStatement(
					"SELECT id FROM appointment WHERE meeting_date = ? AND ((start_hour < ? AND end_hour > ?) OR (start_hour < ? AND end_hour > ?))")) {
				stmt.setDate(1, date);
				stmt.setInt(2, endHour);
				stmt.setInt(3, startHour);
				stmt.setInt(4, startHour);
				stmt.setInt(5, endHour);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					appointmentIds.add(rs.getInt("id"));
				}
			}

			// Step 2: Delete related reminders in `take_rmd`
			for (int appointmentId : appointmentIds) {
				try (PreparedStatement stmt = conn.prepareStatement(
						"DELETE FROM take_rmd WHERE appointment_id = ?")) {
					stmt.setInt(1, appointmentId);
					stmt.executeUpdate();
				}
			}

			// Step 3: Delete conflicting events in `appointment`
			try (PreparedStatement stmt = conn.prepareStatement(
					"DELETE FROM appointment WHERE meeting_date = ? AND ((start_hour < ? AND end_hour > ?) OR (start_hour < ? AND end_hour > ?))")) {
				stmt.setDate(1, date);
				stmt.setInt(2, endHour);
				stmt.setInt(3, startHour);
				stmt.setInt(4, startHour);
				stmt.setInt(5, endHour);
				stmt.executeUpdate();
			}

			JOptionPane.showMessageDialog(this, "Các sự kiện trùng đã được xóa!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xóa các sự kiện trùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}}