package Ooad;

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
	private JPanel contentPane;
	private JPanel calendarPanel;
	private JLabel monthLabel;
	private Connection conn;
	private Calendar currentMonth;

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

	public CalendarUI() {
		setTitle("Google Calendar-like UI - Ooad");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout(10, 10));
		setContentPane(contentPane);

		// Navigation Panel
		JPanel navigationPanel = new JPanel(new BorderLayout());
		JButton prevButton = new JButton("<");
		JButton nextButton = new JButton(">");
		monthLabel = new JLabel("", SwingConstants.CENTER);
		monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
		navigationPanel.add(prevButton, BorderLayout.WEST);
		navigationPanel.add(nextButton, BorderLayout.EAST);
		navigationPanel.add(monthLabel, BorderLayout.CENTER);
		contentPane.add(navigationPanel, BorderLayout.NORTH);

		// Calendar Panel
		calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
		contentPane.add(calendarPanel, BorderLayout.CENTER);

		// Buttons for Adding and Updating Events
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton addEventButton = new JButton("Thêm sự kiện");
		JButton updateEventButton = new JButton("Cập nhật sự kiện");
		buttonPanel.add(addEventButton);
		buttonPanel.add(updateEventButton);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

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

		// Event Listeners for Buttons
		addEventButton.addActionListener(e -> showAddEventDialog(false, null));
		updateEventButton.addActionListener(e -> {
			String eventName = JOptionPane.showInputDialog(this, "Nhập tên sự kiện để cập nhật:");
			if (eventName != null && !eventName.trim().isEmpty()) {
				showAddEventDialog(true, eventName.trim());
			}
		});
	}

	private void connectToDatabase() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ooad", "root", "khoaphamby");
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

		// Add Empty Cells for Days Before Start
		for (int i = 1; i < startDayOfWeek; i++) {
			calendarPanel.add(new JLabel());
		}

		// Add Days with Appointments
		for (int day = 1; day <= daysInMonth; day++) {
			JButton dayButton = new JButton(String.valueOf(day));
			dayButton.setHorizontalAlignment(SwingConstants.LEFT);
			dayButton.setVerticalAlignment(SwingConstants.TOP);

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
							showAddEventDialog(true, event);
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

	private void showAddEventDialog(boolean isUpdate, String existingEventName) {
		JTextField nameField = new JTextField(existingEventName != null ? existingEventName : "");
		JTextField locationField = new JTextField();
		JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		JTextField startHourField = new JTextField();
		JTextField endHourField = new JTextField();
		JTextField typeField = new JTextField();

		Object[] message = {
				"Tên:", nameField,
				"Địa điểm:", locationField,
				"Ngày (yyyy-MM-dd):", dateField,
				"Giờ bắt đầu:", startHourField,
				"Giờ kết thúc:", endHourField,
				"Loại:", typeField
		};

		int option = JOptionPane.showConfirmDialog(this, message, isUpdate ? "Cập nhật sự kiện" : "Thêm sự kiện", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if (nameField.getText().isEmpty() || locationField.getText().isEmpty() ||
					dateField.getText().isEmpty() || startHourField.getText().isEmpty() ||
					endHourField.getText().isEmpty() || typeField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Tất cả các trường phải được điền đầy đủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				java.sql.Date meetingDate = java.sql.Date.valueOf(dateField.getText());
				int startHour = Integer.parseInt(startHourField.getText());
				int endHour = Integer.parseInt(endHourField.getText());
				if (checkDuplicateEvent(nameField.getText(), meetingDate) && !isUpdate) {
					JOptionPane.showMessageDialog(this, "Đã có sự kiện trùng tên và ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (checkTimeConflict(meetingDate, startHour, endHour, existingEventName)) {
					int replaceOption = JOptionPane.showConfirmDialog(this, "Đã có lịch hẹn trùng. Bạn có muốn thay thế không?", "Xung đột lịch", JOptionPane.YES_NO_OPTION);
					if (replaceOption == JOptionPane.NO_OPTION) return;
					deleteConflictingEvent(meetingDate, startHour, endHour);
				}

				if (isUpdate) {
					try (PreparedStatement stmt = conn.prepareStatement(
							"UPDATE appointment SET location = ?, meeting_date = ?, start_hour = ?, end_hour = ?, type_appointment = ? WHERE name = ?")) {
						stmt.setString(1, locationField.getText());
						stmt.setDate(2, meetingDate);
						stmt.setInt(3, startHour);
						stmt.setInt(4, endHour);
						stmt.setString(5, typeField.getText());
						stmt.setString(6, existingEventName);
						stmt.executeUpdate();
					}
				} else {
					try (PreparedStatement stmt = conn.prepareStatement(
							"INSERT INTO appointment (name, location, meeting_date, start_hour, end_hour, type_appointment) VALUES (?, ?, ?, ?, ?, ?)")) {
						stmt.setString(1, nameField.getText());
						stmt.setString(2, locationField.getText());
						stmt.setDate(3, meetingDate);
						stmt.setInt(4, startHour);
						stmt.setInt(5, endHour);
						stmt.setString(6, typeField.getText());
						stmt.executeUpdate();
					}
				}

				updateCalendarView();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi thêm/cập nhật sự kiện: " + e.getMessage());
			}
		}
	}

	private boolean checkDuplicateEvent(String name, java.sql.Date date) {
		if (conn == null) return false;

		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT name FROM appointment WHERE name = ? AND meeting_date = ?")) {
			stmt.setString(1, name);
			stmt.setDate(2, date);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi kiểm tra trùng sự kiện: " + e.getMessage());
		}

		return false;
	}

	private boolean checkTimeConflict(java.sql.Date date, int startHour, int endHour, String eventName) {
		if (conn == null) return false;

		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT name FROM appointment WHERE meeting_date = ? AND ((start_hour < ? AND end_hour > ?) OR (start_hour < ? AND end_hour > ?)) AND name != ?")) {
			stmt.setDate(1, date);
			stmt.setInt(2, endHour);
			stmt.setInt(3, startHour);
			stmt.setInt(4, startHour);
			stmt.setInt(5, endHour);
			stmt.setString(6, eventName != null ? eventName : "");
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi kiểm tra xung đột: " + e.getMessage());
		}

		return false;
	}

	private void deleteConflictingEvent(java.sql.Date date, int startHour, int endHour) {
		if (conn == null) return;

		try (PreparedStatement stmt = conn.prepareStatement(
				"DELETE FROM appointment WHERE meeting_date = ? AND ((start_hour < ? AND end_hour > ?) OR (start_hour < ? AND end_hour > ?))")) {
			stmt.setDate(1, date);
			stmt.setInt(2, endHour);
			stmt.setInt(3, startHour);
			stmt.setInt(4, startHour);
			stmt.setInt(5, endHour);
			stmt.executeUpdate();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xóa sự kiện trùng: " + e.getMessage());
		}
	}
}