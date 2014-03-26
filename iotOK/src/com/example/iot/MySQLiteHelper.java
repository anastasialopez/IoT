package com.example.iot;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_TAGS = "tags";
  public static final String TABLE_HISTORYTAGS = "historyTags";
  public static final String TABLE_ANTENNAS = "antennas";
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_TS = "timestamp";
  public static final String COLUMN_ACTIVE = "active";
  public static final String COLUMN_POSITION = "position";
  public static final String COLUMN_IMEI = "imei";
  private static final String DATABASE_NAME = "iot.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String CREATE_TAGS_TABLE = "create table "
	  +  TABLE_TAGS + "(" + COLUMN_ID
      + " long primary key, " + COLUMN_ACTIVE
      + " integer not null, " + COLUMN_POSITION 
      + " integer not null, " + COLUMN_IMEI
      + " string" + "); ";
  
  private static final String CREATE_HISTORYTAGS_TABLE = "create table "
      +  TABLE_HISTORYTAGS + "(" + COLUMN_ID
      + " long not null, " + COLUMN_TS
      + " long not null, " + COLUMN_ACTIVE
      + " integer not null, " 
      + " primary key (" + COLUMN_ID + ", " + COLUMN_TS + "));";
  
  private static final String CREATE_ANTENNAS_TABLE = "create table "
      +  TABLE_ANTENNAS + "(" + COLUMN_ID
	  + " long primary key, " + COLUMN_POSITION 
	  + " integer not null, " + COLUMN_ACTIVE
	  + " integer not null" + ");";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(CREATE_TAGS_TABLE);
    database.execSQL(CREATE_HISTORYTAGS_TABLE);
    database.execSQL(CREATE_ANTENNAS_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORYTAGS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANTENNAS);
    onCreate(db);
  }
  
}
