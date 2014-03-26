package com.example.iot;

public class Antenna {
	private long id;
	private int active;
	private int position;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
