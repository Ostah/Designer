package pk.projektant;



import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ActivityProjects extends SherlockActivity {

	
	ListView mListProjects;
	TextView mTitle, mCost, mDate;
	Project mActiveProject=null;
	FrameLayout mPreviewLayout;
	DrawManager mPreviewDraw; 
	Context ctx;
	ProjectsListAdapter myAdapter;
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
		getProjects();
		myAdapter = new ProjectsListAdapter(this, User.get(this).mProjects);
		mListProjects.setAdapter(myAdapter);
		mListProjects.setOnItemClickListener(new OnItemClickListener() { 
	        public void onItemClick(AdapterView arg0, View arg1, int pos,
	            long arg3) {
	        		listClicked(pos);
	        }
	    });
		 mPreviewDraw = new DrawManager(ctx, new  ArrayList<FurnitureView>(),1.0f);
		 mPreviewLayout.addView(mPreviewDraw);

	}

	private void listClicked(int pos){
		mActiveProject =User.get(this).mProjects.get(pos); 
		mTitle.setText(User.get(this).mProjects.get(pos).mName);
		Date d = new Date(User.get(this).mProjects.get(pos).mDate);
		mDate.setText(d.toLocaleString());
		mCost.setText(String.valueOf(User.get(this).mProjects.get(pos).getCost())+" z³");
		mPreviewDraw.changeFurnitures(User.get(this).mProjects.get(pos).mFurnitures);
		mPreviewDraw.scaleTo(0.2f);

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
	    		getProjects();
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
	    		 Toast.makeText(ctx, "Usuniêto Projekt", Toast.LENGTH_SHORT).show();
		        dialog.dismiss();
		        
	        }

	    });

	    builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
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
	
	private void getProjects(){
		User.get(this).mProjects.clear();
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

	
}
