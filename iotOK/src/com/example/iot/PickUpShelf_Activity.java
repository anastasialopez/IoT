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

public class PickUpShelf_Activity extends Activity {
	
	int position;
	String imei;
	private TagsDataSource datasourceT;
	private AntennasDataSource antennasourceT;
	
	static final String URL = "https://dl.dropboxusercontent.com/s/rnrmjvcjnlg0766/tags.xml";
	static final String URL1 = "https://dl.dropboxusercontent.com/s/y2dhrcsh2xmcvt9/emptyTag.xml";
	//static final String URL = "https://dl.dropboxusercontent.com/u/19591347/inventoryOn.xml";
	static final String KEY_ITEM = "item";
	static final String KEY_ID = "epc";
	static final String KEY_RESPONSE = "response";
	static final String KEY_SIZE = "inventorySize";
	static final String READER = "device-readerPort";
	
	static Document doc;
	final String text1 = "Thank you for your visit!";
    final String text2 = "Your leaving ";
    final String text3 = "Nothing in this shelf";
    
	List<Tag> tags;
	List<Antenna> antennas;
	ArrayList <ImageButton> buttons = new ArrayList <ImageButton>();
	ArrayList <Integer> positions = new ArrayList <Integer>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		
		datasourceT = new TagsDataSource(this);
		antennasourceT = new AntennasDataSource(this);
		
		antennasourceT.open();
		datasourceT.open();
		
		antennas = antennasourceT.getAllAntennas();

		String h = "Pick your items from the yellow shelf";
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
	    
	    tags = datasourceT.getTagsByIMEI(imei);
	    
	    getTagsPosition();
	   /* position = tags.get(0).getPosition();
	    
	    for(int i = 0; i < tags.size(); ++i){
	    	if(tags.get(0).getPosition() != position){
	    		System.out.println("ERROR!!!!");
	    	}
	    }
	    
	    antenna = antennasourceT.getAntennaByPosition(position);
	    */
	    setState();
	    setButtonsListeners();
		
	}
	
	void getTagsPosition(){
		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML
		doc = parser.getDomElement(xml); // getting DOM element

		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			System.out.println(tags.size());
			for(int j = 0; j < tags.size(); ++j){
				if (tags.get(i).getId() == Long.parseLong(parser.getValue(e, KEY_ID))) {
					System.out.println("t");
				// if the tag corresponds to the tag stored with this imei
					int aux = Integer.parseInt(parser.getValue(e, READER));
					System.out.println("leo: " + aux);
					if (!positions.contains(aux)) {
						System.out.println("toy");
						positions.add(aux - 1);
					}
				}
			}
			
		}
	}

	void setButtonColor(int i, ImageButton button){
		//Si el boton esta libre
		if(i == 0){
			button.setBackground(getResources().getDrawable(R.drawable.button4));
			String enabled = "enable";
			button.setContentDescription(enabled);
		}
		else if(i == 1){
			button.setBackground(getResources().getDrawable(R.drawable.button3));
			String disabled = "disable";
			button.setContentDescription(disabled);
		}
		else{
			button.setBackground(getResources().getDrawable(R.drawable.button5));
			String disabled = "in_use";
			button.setContentDescription(disabled);
		}
	}
	
	void setState(){
		for(int i = 0; i < antennas.size(); ++i){
			if(positions.contains(antennas.get(i).getPosition())){
				setButtonColor(2, buttons.get(i));
			}
			else if(antennas.get(i).getActive() == 0){
	    		setButtonColor(0, buttons.get(i));
	    	}
	    	else if(antennas.get(i).getActive() == 1){
	    		setButtonColor(1, buttons.get(i));
	    	}
	    }
	}
	
	void setButtonsListeners(){
		final int leave = getTags();
		for(int i = 0; i<buttons.size(); ++i){
			final int j = i;
			buttons.get(j).setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					if(buttons.get(j).getContentDescription().toString().contains("in_use")){
						//buttons.get(j).setBackground(getResources().getDrawable(R.drawable.button3));						
						if(leave == 0){
							alertDialog(text1, j);
						}
						else{
							alertDialog(text2, j);
						}
					}
					else{
						alertDialog(text3, j);
					}
				}
				
			});
		}
	}
	
	void alertDialog(final String text, final int j){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(text)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                if(text.contains("visit")){
		                	for(int i = 0; i < tags.size(); ++i){
		                		datasourceT.deleteTag(tags.get(i));
		                	}
		                	setButtonColor(0, buttons.get(j));
		                	Antenna antenna = antennas.get(j);
		        			antennasourceT.updateAntenna(antenna, 0);
		                	finish();
		                }
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	int getTags(){
		
		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL1); // getting XML
		doc = parser.getDomElement(xml); // getting DOM element
		Integer size = 0;
		
		NodeList nl = doc.getElementsByTagName("response");
		// looping through all item nodes <item>
		
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			size = Integer.parseInt(parser.getValue(e, KEY_SIZE));
		}	
		if(size > 0){
			return size;
		}
		
		else{
			return 0;
		}
	}
	
	@Override
	protected void onResume() {
		antennasourceT.open();
		datasourceT.open();
		setState();
		setButtonsListeners();
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
		getMenuInflater().inflate(R.menu.pick_up_shelf_, menu);
		return true;
	}

}
