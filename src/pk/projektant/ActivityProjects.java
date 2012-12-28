package pk.projektant;



import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pk.projektant.RestClient.RequestMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class ActivityProjects extends SherlockActivity {

	
	ListView mListProjects;
	TextView mTitle, mCost, mDate;
	Project mActiveProject=null;
	FrameLayout mPreviewLayout;
	Boolean first_time;
	DrawManager mPreviewDraw; 
	Context ctx;
	ProjectsListAdapter myAdapter;
	int mLastPos=0;
	Boolean mConnectionError=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.l_view_projects);
		
		mPreviewLayout = (FrameLayout) findViewById(R.id.preview_layout);
		mListProjects = (ListView) findViewById(R.id.projects_list);
		mTitle = (TextView) findViewById(R.id.projects_title);
		mCost = (TextView) findViewById(R.id.projects_cost);
		mDate = (TextView) findViewById(R.id.projects_date);
		ctx = this;
		if(User.get(this).mProjects==null) User.get(this).mProjects = new  ArrayList<Project>();
		myAdapter = new ProjectsListAdapter(this, User.get(this).mProjects);
		mListProjects.setAdapter(myAdapter);
		mListProjects.setOnItemClickListener(new OnItemClickListener() { 
	        public void onItemClick(AdapterView arg0, View arg1, int pos,
	            long arg3) {
	        		listClicked(pos);
	        }
	    });
		
		updateList();
		
		 mPreviewDraw = new DrawManager(ctx, new  ArrayList<FurnitureView>(),1.0f);
		 mPreviewLayout.addView(mPreviewDraw);
		  first_time=true;
	}

	@Override
	protected void onResume() {
		if(mPreviewDraw!=null&&!first_time){
			listClicked(mLastPos);
		}
		first_time=false;
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void listClicked(int pos){
		mActiveProject =User.get(this).mProjects.get(pos); 
		mTitle.setText(User.get(this).mProjects.get(pos).mName);
		Date d = new Date(User.get(this).mProjects.get(pos).mDateCreation);
		mDate.setText(d.toLocaleString());
		mCost.setText(String.valueOf(User.get(this).mProjects.get(pos).getCost())+" z³");
		mPreviewDraw.changeFurnitures(User.get(this).mProjects.get(pos).mFurnitures);
		mPreviewDraw.scaleTo(0.2f);
		mLastPos=pos;

	}
	
	 public boolean onOptionsItemSelected(MenuItem item){
	    	switch(item.getItemId()){
	    	case R.id.menu_new:
	    		newProject();
	    		break;
	    	case R.id.menu_erase:
	    		if(mActiveProject!=null) eraseProject();
	    		else Toast.makeText(ctx, "Nie wybrano projektu do usuniêcia", Toast.LENGTH_SHORT).show();
	    		break;
	    	case R.id.menu_edit:
	    		if(mActiveProject!=null) editProject();
	    		else Toast.makeText(ctx, "Nie wybrano projektu do edycji", Toast.LENGTH_SHORT).show();
	    		break;
	    		
	    	case R.id.menu_refresh:
	    		updateList();
	    		myAdapter.notifyDataSetChanged();
	    		break;
	    	
	    }
			return false;
	 
	 }
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.l_view_projects, menu);
		return true;
	}
	
	public void editProject() {
		 User.get(ctx).mActiveProject=mActiveProject;
		 Intent prefIntent = new Intent(ctx,ActivityDesigner.class);
         ctx.startActivity(prefIntent);
	}
	public void eraseProject() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Usuñ Projekt");
	    builder.setMessage("Czy na pewno chcesz usun¹æ \""+mActiveProject.mName+"\"?");

	    builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
		        User.get(ctx).mProjects.remove(mActiveProject);
		        myAdapter.notifyDataSetChanged();
		        mTitle.setText(".");
	    		mDate.setText(".");
	    		mCost.setText(".");
	    		mPreviewDraw.clear();
	    		 Toast.makeText(ctx, "Usuniêto Projekt", Toast.LENGTH_SHORT).show();
		        dialog.dismiss();
		        
	        }

	    });

	    builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	           // dialog.dismiss();
	        }
	    });
	 
	   

	    AlertDialog  alert = builder.create();
	    
	    alert.show();
	 
	}
	
	

	public void newProject() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Nowy Projekt");
		alert.setMessage("Podaj Nazwê Projektu");
		final EditText input = new EditText(this);
	
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();    		
    		  if(!value.isEmpty()){	    			  
    			  Project p = new Project(value,System.currentTimeMillis());
    			  User.get(ctx).mProjects.add(p);
    			  User.get(ctx).mActiveProject=p;
    			 // Intent prefIntent = new Intent(ctx,ActivityDesigner.class);
 	              //ctx.startActivity(prefIntent);
 	              myAdapter.notifyDataSetChanged();
 	             Toast.makeText(ctx, "Utworzono nowy projekt", Toast.LENGTH_LONG).show();
    		  }
    		  else{
    			  Toast.makeText(ctx, "Nazwa projektu nie mo¿e byæ pusta", Toast.LENGTH_LONG).show();
    		  }
		  }
		});

		alert.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		  }
		});

		alert.show();
		
	}
	
	private void updateList(){
		ThreadGetProjects task = new ThreadGetProjects();
		   task.execute();	 
	}
	private void getProjects(){
		mConnectionError=false;
		User.get(this).mProjects.clear();
		getProjectsList();  
		
		ArrayList<FurnitureView> furnit = new ArrayList<FurnitureView>();
		
		furnit.add(new FurnitureView(this,100,100,Tokenizer.sFurnitures.get(0)));
		Project p = new Project("Projekt1",System.currentTimeMillis(),new ArrayList<FurnitureView>(furnit));
		User.get(this).mProjects.add(p);
		
		furnit.clear();
		furnit.add(new FurnitureView(this,100,100,Tokenizer.sFurnitures.get(0)));
		furnit.add(new FurnitureView(this,300,300,Tokenizer.sFurnitures.get(5)));
		 p = new Project("Projekt2",System.currentTimeMillis(),new ArrayList<FurnitureView>(furnit));
		 User.get(this).mProjects.add(p);
		 
		 furnit.clear();
		furnit.add(new FurnitureView(this,100,100,Tokenizer.sFurnitures.get(0)));
		furnit.add(new FurnitureView(this,300,300,Tokenizer.sFurnitures.get(5)));
		furnit.add(new FurnitureView(this,150,300,Tokenizer.sFurnitures.get(7)));
		 p = new Project("Projekt3",System.currentTimeMillis(),new ArrayList<FurnitureView>(furnit));
		 User.get(this).mProjects.add(p);
		 
		
		
		
	}
	private String[] getStringArray(ArrayList<Project> a){
		String[] toReturn = new String[a.size()];
		for(int i=0 ;i<a.size();i++){
			toReturn[i]=a.get(i).mName;
		}
		return toReturn;
		
	}
	
	private Project getOneProject(int id) {
		RestClient connection = new RestClient("http://designercms.herokuapp.com/project/"+ String.valueOf(id));
		connection.AddParam("username", User.get(ctx).getEmail());
		connection.AddParam("password", User.get(ctx).getPassword());

		
		try {
			connection.Execute(RequestMethod.GET);	 	
        	String response = connection.getResponse();
        	if(response == "WrongCredentionals"){
	        	Toast.makeText(ctx, "Z³e dane u¿ytkownika", Toast.LENGTH_LONG).show();
	        	return null;
	        }
        	
        	JsonReader reader = new JsonReader(new StringReader(response));
        	 Gson myGson = new Gson();
        	 JSONProjectSimple proj  = myGson.fromJson(response,JSONProjectSimple.class) ;
        	 return proj.toProject();
		} catch (Exception e) {
			mConnectionError=true;
			e.printStackTrace();
			
		}
		return null;
	}
	
	private void getProjectsList(){
	        RestClient connection = new RestClient("http://designercms.herokuapp.com/project/");
	        connection.AddParam("username", User.get(ctx).getEmail());
	        connection.AddParam("password",User.get(ctx).getPassword());
	        try 
	        {
	        	connection.Execute(RequestMethod.GET);	 	
	        	String response = connection.getResponse();
		       
	        	if(response == "WrongCredentionals"){
		        	Toast.makeText(ctx, "Z³e dane u¿ytkownika", Toast.LENGTH_LONG).show();
		        	return;
		        }
		       
	        	JSONArray jObject = null;
				jObject = new JSONArray(response);		
					
				for(int i=0;i<jObject.length();i++){
					int id = -1;
					id = jObject.getJSONObject(i).getInt("id");	
					Project proj = getOneProject(id);
					if(proj!=null) User.get(this).mProjects.add(proj);
				}
	      
	        }catch (Exception e) {
	        	mConnectionError=true;
				e.printStackTrace();
			}
	}

	  private class ThreadGetProjects extends AsyncTask<Void, Void, Void> {
			private ProgressDialog Dialog;

			protected void onPreExecute() {
				Dialog = new ProgressDialog(ctx);
				Dialog.setMessage("Aktualizowanie Listy Projektów");
				Dialog.setCanceledOnTouchOutside(false);
				Dialog.setCancelable(false);
				Dialog.show();
			}

			protected Void doInBackground(Void... arg0) {
				getProjects();
				return null;
			}

			protected void onPostExecute(Void unused) {
				myAdapter.notifyDataSetChanged();
				Dialog.dismiss();	
				if(mConnectionError)
				{
					Toast.makeText(ctx,"B³¹d Po³¹czenia", Toast.LENGTH_SHORT).show();	
				}
				else if(User.get(ctx).mProjects.size()==0)
				{
					Toast.makeText(ctx,"Brak Projektów", Toast.LENGTH_SHORT).show();	
				}
				else{
					Toast.makeText(ctx,"Lista Projektów zaktualizowana", Toast.LENGTH_SHORT).show();	
				}
				
	    	}
			
		}
	
}
