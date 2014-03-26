package com.example.iot;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AntennasDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_ACTIVE, MySQLiteHelper.COLUMN_POSITION};

	public AntennasDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Antenna createAntenna(long id, int active, int position) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ID, id);
		values.put(MySQLiteHelper.COLUMN_ACTIVE, active);
		values.put(MySQLiteHelper.COLUMN_POSITION, position);
		
		database.insert(MySQLiteHelper.TABLE_ANTENNAS, null, values);
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ANTENNAS, allColumns,
				MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
		System.out.println("Antenna with id: " + id + "and active: " + active + "has been inserted");
		
		cursor.moveToFirst();
		Antenna newAntenna = cursorToAntenna(cursor);
		cursor.close();
		return newAntenna;
	}

	public void updateAntenna(Antenna antenna, int active) {
		long id = antenna.getId();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ACTIVE, active);

		database.update(MySQLiteHelper.TABLE_ANTENNAS, values,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
		System.out.println("Antenna with id " + id + " has been updated");
	}

	public void deleteAntenna(Antenna antenna) {
		long id = antenna.getId();
		database.delete(MySQLiteHelper.TABLE_ANTENNAS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
		System.out.println("Antenna deleted with id: " + id);
	}

	public int existsAntenna(long id) {
		int alreadyIN = 0;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ANTENNAS, allColumns,
				MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null,
				null);
		if (cursor.getCount() == 0)
			alreadyIN = 0; // false
		else
			alreadyIN = 1; // true

		// close the cursor
		cursor.close();
		return alreadyIN;
	}

	public Antenna getAntenna(long id) {
		Antenna antennaIN = new Antenna();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ANTENNAS, allColumns,
				MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null,
				null);

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			antennaIN = cursorToAntenna(cursor);
		}

		// close the cursor
		cursor.close();
		return antennaIN;
	}

	public List<Antenna> getAllAntennas() {
		List<Antenna> antennas = new ArrayList<Antenna>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ANTENNAS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Antenna antenna = cursorToAntenna(cursor);
			antennas.add(antenna);
			cursor.moveToNext();
		}
		// Close the cursor
		cursor.close();
		return antennas;
	}

	public List<Antenna> getAllActiveAntennas() {
		List<Antenna> antennas = new ArrayList<Antenna>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ANTENNAS, allColumns,
				MySQLiteHelper.COLUMN_ACTIVE + " = " + 1, null, null, null,
				null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Antenna antenna = cursorToAntenna(cursor);
			antennas.add(antenna);
			cursor.moveToNext();
		}
		// Close the cursor
		cursor.close();
		return antennas;
	}
	
	public Antenna getAntennaByPosition(int i) {
		Antenna antenna = new Antenna();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ANTENNAS, allColumns,
				MySQLiteHelper.COLUMN_POSITION + " = " + i, null, null, null,
				null);

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			antenna = cursorToAntenna(cursor);
		}

		// close the cursor
		cursor.close();
		return antenna;
	}

	private Antenna cursorToAntenna(Cursor cursor) {
		Antenna antenna = new Antenna();
		antenna.setId(cursor.getLong(0));
		antenna.setActive(cursor.getInt(1));
		antenna.setPosition(cursor.getInt(2));
		return antenna;
	}
}
