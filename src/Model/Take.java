package Model;

public class Take {
	private int userId;
	private int appointmentId;
	
	public Take() {}
	
	public Take(int userId, int appointmentId) {
		this.userId = userId;
		this.appointmentId = appointmentId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	
}
