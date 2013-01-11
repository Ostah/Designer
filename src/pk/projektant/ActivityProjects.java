package pk.projektant;



import java.util.ArrayList;
import java.util.Date;

import pk.projektant.RestClient.RequestMethod;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;

public class ActivityProjects extends SherlockActivity {

	
	ListView mListProjects;
	TextView mTitle, mCost, mDate;
	Project mActiveProject=null;
	FrameLayout mPreviewLayout;
	Boolean first_time;
	DrawManager mPreviewDraw; 
	Project mCreatedProject=null;
	Context ctx;
	ProjectsListAdapter myAdapter;
	int mLastPos=0;
	Boolean mConnectionError=false, mDeleted=false;
	Dialog mDialog;
	EditText mDialogName, mDialogDescription;
	Button mDialogOK, mDialogCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.l_activity_projects);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
	        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
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
		myAdapter.notifyDataSetChanged();
		// TODO Auto-generated method stub
		
		super.onResume();
	}

	@SuppressWarnings("deprecation")
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
	    		else Toast.makeText(ctx, "Nie wyw wybrano projektu do edycji", Toast.LENGTH_SHORT).show();
	    		break;
	    		
	    	case R.id.menu_refresh:
	    		updateList();
	    		myAdapter.notifyDataSetChanged();
	    		break;
	    	case R.id.menu_logout :
	    		Log.d("options", "logout");
	    		User.clear();
	    		Intent prefIntent = new Intent(this,ActivityLogin.class);
	            this.startActivity(prefIntent);
	    	
	    }
			return false;
	 
	 }
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.l_view_projects, menu);
		return true;
	}
	
	public void eraseProjectDB(){
		mDeleted=false;
		if(mActiveProject==null) return;
		if(mActiveProject.mId<=0) return;
		
		RestClient connection = new RestClient("http://designercms.herokuapp.com/project/"+ String.valueOf(mActiveProject.mId));
		try {
			connection.Execute(RequestMethod.DELETE);	 	
        	String response = connection.getResponse();
        	if(response.contains("Deleted")){
        		mDeleted=true;
	        }
    
		} catch (Exception e) {
			mConnectionError=true;
			e.printStackTrace();
			
		}
		
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
	        	ThreadDeleteProject task= new ThreadDeleteProject();
	 		   	task.execute();	 
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

	protected void hideKeyboard(View view) {
		InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	public void dialogCreate() {
		mDialog = new Dialog(ctx);
		mDialog.setContentView(R.layout.l_view_dialog_project);
		mDialog.setTitle("Nowy Projekt");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		
		LinearLayout l = (LinearLayout) mDialog.findViewById(R.id.new_project_layoutroot);
		l.setOnTouchListener(new OnTouchListener()
		{
    	    public boolean onTouch(View view, MotionEvent ev)
    	    {
    	        hideKeyboard(view);
    	        return false;
    	    }
    	});
		mDialogName = (EditText) mDialog.findViewById(R.id.new_project_name);
		mDialogDescription= (EditText) mDialog.findViewById(R.id.new_project_description);		 
		mDialogOK = (Button) mDialog.findViewById(R.id.new_project_ok);
		mDialogCancel = (Button) mDialog.findViewById(R.id.new_project_cancel);
		
		mDialogOK.setOnClickListener(new View.OnClickListener() {		
	    	   public void onClick(View arg0) {
	    		  String value = mDialogName.getText().toString();    		
	     		  if(!value.isEmpty()){	    			  
	     			  Project p = new Project(value,System.currentTimeMillis());
	     			  p.mDescription= mDialogDescription.getText().toString();
	     			  User.get(ctx).mProjects.add(p);
	     			  User.get(ctx).mActiveProject=p;
	     			  mCreatedProject=p;
	  	              myAdapter.notifyDataSetChanged();
	  	              ThreadNewProject task = new ThreadNewProject();
	  	              task.execute();	 
	  	              mDialog.dismiss();
	     		  }
	     		  else{
	     			  Toast.makeText(ctx, "Nazwa projektu nie mo¿e byæ pusta", Toast.LENGTH_LONG).show();
	     		  }
	   
	    	   }
	    });
		
		mDialogCancel.setOnClickListener(new View.OnClickListener() {		
	    	   public void onClick(View arg0) {
	    		   mDialog.dismiss();
	    	   }
	    });
		
		mDialog.show();
	}

	
	
	private void uploadNewProject(){
		JSONProjectSimple proj = new JSONProjectSimple(mCreatedProject);
		mConnectionError=false;
		RestClient connection = new RestClient("http://designercms.herokuapp.com/project/");
		connection.AddParam("username", User.get(ctx).getEmail());
		connection.AddParam("password", User.get(ctx).getPassword());
		connection.AddParam("title", proj.mTitle);
		connection.AddParam("desc", proj.mDescription);
		connection.AddParam("walls", proj.mWalls);
		connection.AddParam("furniture", proj.mFurnitures);
		
		try {
			connection.Execute(RequestMethod.POST);	 	
        	String response = connection.getResponse();
        	if(response == "WrongCredentionals"){
	        	Toast.makeText(ctx, "Z³e dane u¿ytkownika", Toast.LENGTH_LONG).show();
	   
	        }
        	else{
        		if(response.contains("ID")){
        			String nr = response.substring(3,response.length()-1);		
        			try{
        				int pid = Integer.parseInt(nr);
        					mCreatedProject.mId=pid;
        			}
        			catch(Exception e){
        				mCreatedProject.mId=0;
        			}	
        			Log.d("sr", nr);
        		}
        	}
    
		} catch (Exception e) {
			mConnectionError=true;
			e.printStackTrace();
			
		}
		
	}
	
	private void updateList(){
		ThreadGetProjects task = new ThreadGetProjects();
		task.execute();	 
	}
	public void newProject() {
		dialogCreate();
	}
	private void getProjects(){
		mConnectionError=false;
		User.get(this).mProjects.clear();
		getProjectsList();  	
	}
	
	
//	funkcja ze starego API
//	private Project getOneProject(int id) {
//		RestClient connection = new RestClient("http://designercms.herokuapp.com/project/"+ String.valueOf(id));
//		connection.AddParam("username", User.get(ctx).getEmail());
//		connection.AddParam("password", User.get(ctx).getPassword());
//
//		try {
//			connection.Execute(RequestMethod.GET);	 	
//        	String response = connection.getResponse();
//        	if(response == "WrongCredentionals"){
//	        	Toast.makeText(ctx, "Z³e dane u¿ytkownika", Toast.LENGTH_LONG).show();
//	        	return null;
//	        }
//        	
//        	 Gson myGson = new Gson();
//        	 JSONProjectSimple proj  = myGson.fromJson(response,JSONProjectSimple.class) ;
//        	 return proj.toProject();
//		} catch (Exception e) {
//			mConnectionError=true;
//			e.printStackTrace();
//			
//		}
//		return null;
//	}
	
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
		 		 
				Gson myGson = new Gson();
				JSONProjectSimple[] furnitures = myGson.fromJson(response, JSONProjectSimple[].class);
				
				for(int i=0;i<furnitures.length;i++){		
					 User.get(this).mProjects.add(furnitures[i].toProject());
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
	  
	  private class ThreadNewProject extends AsyncTask<Void, Void, Void> {
			private ProgressDialog Dialog;

			protected void onPreExecute() {
				Dialog = new ProgressDialog(ctx);
				Dialog.setMessage("Zapisywanie Projektu");
				Dialog.setCanceledOnTouchOutside(false);
				Dialog.setCancelable(false);
				Dialog.show();
			}

			protected Void doInBackground(Void... arg0) {
				uploadNewProject();
				return null;
			}

			protected void onPostExecute(Void unused) {
				myAdapter.notifyDataSetChanged();
				Dialog.dismiss();	
				if(mConnectionError)
				{
					Toast.makeText(ctx,"B³¹d Po³¹czenia, Projekt nie zostal wyslany", Toast.LENGTH_SHORT).show();	
				}
				
	    	}
			
		}
	  
	  private class ThreadDeleteProject extends AsyncTask<Void, Void, Void> {
			private ProgressDialog Dialog;

			protected void onPreExecute() {
				Dialog = new ProgressDialog(ctx);
				Dialog.setMessage("Usuwanie");
				Dialog.setCanceledOnTouchOutside(false);
				Dialog.setCancelable(false);
				Dialog.show();
			}

			protected Void doInBackground(Void... arg0) {
				eraseProjectDB();
				return null;
			}

			protected void onPostExecute(Void unused) {
				myAdapter.notifyDataSetChanged();
				Dialog.dismiss();	
				
				if(mConnectionError)
				{
					Toast.makeText(ctx,"B³¹d Po³¹czenia, Projekt nie zostal usuniêty", Toast.LENGTH_SHORT).show();	
				}
				else if(!mDeleted){
					Toast.makeText(ctx,"Projekt nie móg³ zostaæ usuniêty", Toast.LENGTH_SHORT).show();	
				}
				else {
					 User.get(ctx).mProjects.remove(mActiveProject);
				     myAdapter.notifyDataSetChanged();
				     mTitle.setText(".");
				     mDate.setText(".");
				     mCost.setText(".");
				     mPreviewDraw.clear();
				     Toast.makeText(ctx, "Usuniêto Projekt", Toast.LENGTH_SHORT).show();
				}
				
	    	}
			
		}
	
}
