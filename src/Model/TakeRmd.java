package Model;

public class TakeRmd {
	private int appointment_id;
	private int reminder_id;
	
	public TakeRmd() {}

	public TakeRmd(int appointment_id, int reminder_id) {

		this.appointment_id = appointment_id;
		this.reminder_id = reminder_id;
	}

	public int getAppointment_id() {
		return appointment_id;
	}

	public void setAppointment_id(int appointment_id) {
		this.appointment_id = appointment_id;
	}

	public int getReminder_id() {
		return reminder_id;
	}

	public void setReminder_id(int reminder_id) {
		this.reminder_id = reminder_id;
	}
	

	
}
