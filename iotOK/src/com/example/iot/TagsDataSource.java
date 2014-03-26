package com.example.iot;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TagsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_ACTIVE, MySQLiteHelper.COLUMN_POSITION,
			MySQLiteHelper.COLUMN_IMEI };

	public TagsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Tag createTag(long id, int active, int position, String imei) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ID, id);
		values.put(MySQLiteHelper.COLUMN_ACTIVE, active);
		values.put(MySQLiteHelper.COLUMN_POSITION, position);
		values.put(MySQLiteHelper.COLUMN_IMEI, imei);

		database.insert(MySQLiteHelper.TABLE_TAGS, null, values);
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
		System.out.println("Tag with id: " + id + " has been inserted in: " + position);
		cursor.moveToFirst();
		Tag newTag = cursorToTag(cursor);
		cursor.close();
		return newTag;
	}

	public void updateTag(Tag tag, int active) {
		long id = tag.getId();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ACTIVE, active);

		database.update(MySQLiteHelper.TABLE_TAGS, values,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
		System.out.println("The tag with id " + id + " has been updated");
	}

	public void deleteTag(Tag tag) {
		long id = tag.getId();
		database.delete(MySQLiteHelper.TABLE_TAGS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
		System.out.println("Tag deleted with id: " + id);
	}

	public int existsTag(long id) {
		int alreadyIN = 0;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
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

	public Tag getTag(long id) {
		Tag tagIN = new Tag();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null,
				null);

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			tagIN = cursorToTag(cursor);
		}

		// close the cursor
		cursor.close();
		return tagIN;
	}

	public List<Tag> getAllTags() {
		List<Tag> tags = new ArrayList<Tag>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Tag tag = cursorToTag(cursor);
			tags.add(tag);
			cursor.moveToNext();
		}
		// Close the cursor
		cursor.close();
		return tags;
	}

	public List<Tag> getAllActiveTags() {
		List<Tag> tags = new ArrayList<Tag>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				MySQLiteHelper.COLUMN_ACTIVE + " = " + 1, null, null, null,
				null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Tag tag = cursorToTag(cursor);
			tags.add(tag);
			cursor.moveToNext();
		}
		// Close the cursor
		cursor.close();
		return tags;
	}
	
	public int getNumberActiveTags() {

		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				MySQLiteHelper.COLUMN_ACTIVE + " = " + 1, null, null, null,
				null);

		int count = cursor.getCount();
		/*while (!cursor.isAfterLast()) {
			Tag tag = cursorToTag(cursor);
			tags.add(tag);
			cursor.moveToNext();
		}*/
		// Close the cursor
		cursor.close();
		return count;
	}
	
	public List<Tag> getTagsByIMEI(String imei) {
		List<Tag> tags = new ArrayList<Tag>();
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				MySQLiteHelper.COLUMN_IMEI + "=\"" + imei + "\"", null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Tag tag = cursorToTag(cursor);
			tags.add(tag);
			cursor.moveToNext();
		}
		// Close the cursor
		cursor.close();
		return tags;
	}

	public int existsTagsByIMEI(String imei) {
		int alreadyIN = 0;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAGS, allColumns,
				MySQLiteHelper.COLUMN_IMEI + "=\"" + imei + "\"", null, null, null, null,
				null);
		if (cursor.getCount() == 0)
			alreadyIN = 0; // false
		else
			alreadyIN = 1; // true

		// close the cursor
		cursor.close();
		return alreadyIN;
	}

	private Tag cursorToTag(Cursor cursor) {
		Tag tag = new Tag();
		tag.setId(cursor.getLong(0));
		tag.setActive(cursor.getInt(1));
		tag.setPosition(cursor.getInt(2));
		tag.setIMEI(cursor.getString(3));
		return tag;
	}
}