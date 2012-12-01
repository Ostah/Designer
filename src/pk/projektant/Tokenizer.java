package pk.projektant;

import java.io.*;
import java.util.ArrayList;

import android.util.Log;

public class Tokenizer {
	
	static String content;
	static ArrayList<Furniture> sFurnitures;
	
	static void open(InputStream raw )
	{
		InputStreamReader isreader= null;
		sFurnitures = new ArrayList<Furniture>();
		
		try {
			 isreader = new InputStreamReader(raw, "UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 StringBuffer buffer = new StringBuffer();
		Reader in = new BufferedReader(isreader);
		int ch;
		try {
			while ((ch = in.read()) > -1) {
			         buffer.append((char)ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String a =  buffer.toString();
		String[] result = a.split("\\n");
		Log.d("ile",Integer.toString(result.length));
		for(int i=0;i<result.length;i++)
		{
			String[] row = result[i].split("\\|");
			Furniture element = new Furniture(row[0].toLowerCase(),row[1],row[2],row[3],row[4],row[5],Float.valueOf(row[6]),Float.valueOf(row[7]),Float.valueOf(row[8]),Float.valueOf(row[9]));
			sFurnitures.add(element);
	
		}
		
		Log.d("ile","ll");
		
	}
}
