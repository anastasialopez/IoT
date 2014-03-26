package com.example.iot;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	private AntennasDataSource antennasourceT;
	private String deviceID;
	private TagsDataSource tagsourceT;
	static final String URL = "https://dl.dropboxusercontent.com/s/z0b79r6swxmfrn9/antenas.xml";
	static final String ENTRY = "entry";
	static final String READER = "reader-port";
	static Document doc;
	Integer count = 1;
	MediaPlayer song;
	MediaPlayer buttonError;
	MediaPlayer buttonClick;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate(Bundle savedInstanceState) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		//sonidos
		buttonError = MediaPlayer.create(this, R.raw.error);
		buttonClick = MediaPlayer.create(this, R.raw.click);
		song = MediaPlayer.create(this, R.raw.copacobana);
		
		song.start();
		deviceID = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		//Toast.makeText(this, deviceID, Toast.LENGTH_SHORT).show();
		
		tagsourceT = new TagsDataSource(this);
		
		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML
		doc = parser.getDomElement(xml); // getting DOM element
		
		antennasourceT = new AntennasDataSource(this);
		antennasourceT.open();

		NodeList nl = doc.getElementsByTagName(ENTRY);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			if (antennasourceT.existsAntenna(Long.parseLong(parser.getValue(e, READER))) == 0) {
			// if the antenna is new, it is inserted in the table.
				antennasourceT.createAntenna(Long.parseLong(parser.getValue(e, READER)), 0, i);
			}
		}
		
		List<Antenna> antennas = antennasourceT.getAllAntennas();
		
		System.out.println("tamaño create: " + antennas.size());
		

	}


	 public void pickCoat(View view) {
		 tagsourceT.open();
		 int tagTest = tagsourceT.existsTagsByIMEI(deviceID);
		 if(tagTest == 0){
			 buttonError.start();
			 Toast.makeText(this, "You don't have any items here", Toast.LENGTH_SHORT).show();
		 }
		 else{
			 buttonClick.start();
			 Intent intent = new Intent(this, PickUpShelf_Activity.class);
			 intent.putExtra("imei", deviceID);
			 startActivity(intent);
		 }
	 }

	public void checkCoat(View view) {
		Intent intent = new Intent(this, Shelf_Activity.class);
		intent.putExtra("count", count.toString());
		intent.putExtra("imei", deviceID);
		buttonClick.start();
		++count;
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		antennasourceT.open();
		deviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		antennasourceT.close();
		tagsourceT.close();
		super.onPause();
	}
}
