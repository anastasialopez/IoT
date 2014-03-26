package com.example.iot;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Shelf_Activity extends Activity {
	
	int position;
	String imei;
	private TagsDataSource datasourceT;
	private AntennasDataSource antennasourceT;
	
	static final String URL = "https://dl.dropboxusercontent.com/s/rnrmjvcjnlg0766/tags.xml";
	//static final String URL = "https://dl.dropboxusercontent.com/u/21231089/inventoryOn.xml";
	//static final String URL = "https://dl.dropboxusercontent.com/u/19591347/inventoryOn.xml";
	static final String KEY_ITEM = "item";
	static final String KEY_ID = "epc";
	static final String READER = "device-readerPort";
	static Document doc;
	List <Antenna> antennas;
	ArrayList <ImageButton> buttons = new ArrayList <ImageButton>();
	final String text1 = "Please insert your items in the box and then press ok";
    final String text2 = "This shelf is occupied, please choose another one";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		
		datasourceT = new TagsDataSource(this);
		antennasourceT = new AntennasDataSource(this);
		antennasourceT.open();
		
		antennas = antennasourceT.getAllAntennas();
		
		String h = "Please choose the desired shelf";
		TextView help = (TextView)findViewById(R.id.text);
		
		help.setText(h);
		
		final ImageButton shelf1 = (ImageButton)findViewById(R.id.image1);
		final ImageButton shelf2 = (ImageButton)findViewById(R.id.image2);
		final ImageButton shelf3 = (ImageButton)findViewById(R.id.image3);
		final ImageButton shelf4 = (ImageButton)findViewById(R.id.image4);
		
		buttons.add(shelf1);
		buttons.add(shelf2);
		buttons.add(shelf3);
		buttons.add(shelf4);
		
		Intent intent = getIntent();
	    //String c = intent.getStringExtra("count");
	    imei = intent.getStringExtra("imei");

	    setState();
	    setButtonsListeners();
	}
	
	void setButtonsListeners(){
		for(int i = 0; i<buttons.size(); ++i){
			final int j = i;
			buttons.get(j).setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					if(buttons.get(j).getContentDescription().toString().contains("enable")){
						//buttons.get(j).setBackground(getResources().getDrawable(R.drawable.button3));
						System.out.println("position: " +j);
						position = j;
						alertDialog(text1, j);
					}
					else{
						alertDialog(text2, j);
					}
				}
				
			});
		}
	}
	
	void getTags(int j){
		int count = 0;
		boolean in = false;
		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML
		doc = parser.getDomElement(xml); // getting DOM element

		
		datasourceT.open();

		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			if (datasourceT.existsTag(Long.parseLong(parser.getValue(e, KEY_ID))) == 0) {
			// if the tag is new, it is inserted in the table
				if(Integer.parseInt(parser.getValue(e, READER)) == position + 1){
					datasourceT.createTag(Long.parseLong(parser.getValue(e, KEY_ID)), 1, position, imei);
					++count;
				}
				else{
					String error = "You haven't inserted any items.";
					Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
					in = true;
				}
			} else {
				// if exists, send an error message
				String error = "The item with id: " + parser.getValue(e, KEY_ID) + " is already in the shelf";
				Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
				in = true;
			}
		}
		if(!in){
			String confirmation = "You have inserted " + count + " items.";
			Toast.makeText(this, confirmation, Toast.LENGTH_SHORT).show();
			setButtonColor(1, buttons.get(j));
			Antenna antenna = antennas.get(j);
			antennasourceT.updateAntenna(antenna, 1);
			finish();
		}
	}
	
	void alertDialog(final String text, final int j){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(text)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                if(text.contains("items")){
		                	getTags(j);
		                }
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	void setButtonColor(int i, ImageButton button){
		//Si el boton esta libre
		if(i == 0){
			button.setBackground(getResources().getDrawable(R.drawable.button4));
			String enabled = "enable";
			button.setContentDescription(enabled);
		}
		else{
			button.setBackground(getResources().getDrawable(R.drawable.button3));
			String disabled = "disable";
			button.setContentDescription(disabled);
		}
	}
	
	void setState(){
		for(int i = 0; i < antennas.size(); ++i){
	    	//Si la antena no tiene nada dentro
	    	if(antennas.get(i).getActive() == 0){
	    		setButtonColor(0, buttons.get(i));
	    	}
	    	else{
	    		setButtonColor(1, buttons.get(i));
	    	}
	    }
	}
	
	@Override
	protected void onResume() {
		antennasourceT.open();
		datasourceT.open();
		setState();
		super.onResume();
	}

	@Override
	protected void onPause() {
		antennasourceT.close();
		datasourceT.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shelf_, menu);
		return true;
	}


}
