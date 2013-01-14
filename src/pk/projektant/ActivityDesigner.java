package pk.projektant;

import java.util.ArrayList;
import java.util.List;

import pk.projektant.RestClient.RequestMethod;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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

public class ActivityDesigner extends SherlockActivity {
	
	private ListView listViewCity;
	private FrameLayout mapArea;
	private TextView searchText;
	private Context ctx;
	private  DragShadowBuilder shadowBuilder;
	private MapManager mapManager;
	private MenuItem menu_custom=null;
	private MenuItem menu_wall=null;
	Boolean mConnectionError=false;
	List<Furniture> furnitureList; 
	ArrayList<FurnitureView> sFurnitures;
	 FurnitureListAdapter aa ;
	 Dialog mDialog;
		EditText mDialogName, mDialogDescription;
		Button mDialogOK, mDialogCancel;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	
    	super.onWindowFocusChanged(hasFocus);
    	MapManager.width =mapArea.getWidth();
    	MapManager.heigth = mapArea.getHeight();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.l_activity_designer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        searchText = (TextView) findViewById(R.id.search_text);
        furnitureList = Tokenizer.sFurnitures;
        LinearLayout temporat = ( LinearLayout ) findViewById( R.id.LinearLayout2);
        temporat.setBackgroundColor(Color.TRANSPARENT);
        listViewCity = ( ListView ) findViewById( R.id.listFurnitures);
         aa =  new FurnitureListAdapter(ctx, R.layout.l_view_list_element, furnitureList );
        listViewCity.setAdapter(aa ); 

        
        
        mapArea = (FrameLayout)findViewById(R.id.FrameLaytoDraw);     
        mapManager = new MapManager(mapArea, ctx,this,User.get(ctx).mActiveProject.mFurnitures);
        searchText.addTextChangedListener(new TextWatcher() {
 
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
              aa.filter(cs.toString());
              Log.d("FILRET", cs.toString());
            }
 
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
 
            }
 
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        
        listViewCity.setOnItemLongClickListener(listSourceItemLongClickListener);
        listViewCity.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MapManager.showProperties(furnitureList.get(arg2)); // to musi byc po inicjalizacji managera!
				
			}
		});
        mapArea.setOnDragListener(new MyDragNewListener(mapManager, ctx,true));
        listViewCity.setOnDragListener(new MyDragNewListener(mapManager, ctx,false));
       
        
        if(User.get(ctx).mActiveProject!=null)
		  {
			 setTitle("Projekt \""+User.get(ctx).mActiveProject.mName+"\"");
		  }
    }
    
	protected void onResume() {
		
		if(User.get(ctx).mActiveProject!=null)
		  {
			 setTitle("Projekt \""+User.get(ctx).mActiveProject.mName+"\"");
		  }
		super.onResume();
	}
    public void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    private void changeGrid(){
    	 SharedPreferences pref ;
		  Editor editor ;
		  pref=ctx.getSharedPreferences("options",0);
		  Boolean now = pref.getBoolean("showGrid", true);
		  editor = pref.edit();
		  now = !now;
		  editor.putBoolean("showGrid", now);
		  Log.d("grid", now.toString());
		  editor.commit();
		  mapManager.invalidate();
    }
   
    public void dialogEdit() {
		mDialog = new Dialog(ctx);
		mDialog.setContentView(R.layout.l_view_dialog_project);
		mDialog.setTitle("Edytuj W³aœciwoœci Projektu");
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
		
		mDialogName.setText(User.get(ctx).mActiveProject.mName);
		mDialogDescription.setText(User.get(ctx).mActiveProject.mDescription);
		mDialogOK.setOnClickListener(new View.OnClickListener() {		
	    	   public void onClick(View arg0) {
	    		  String value = mDialogName.getText().toString();    		
	     		  if(!value.isEmpty()){	    
	     			  
	     			 if( mDialogDescription.getText().length()==0){
	     				User.get(ctx).mActiveProject.mDescription=" ";
	     			 }
	     			 else{
	     				User.get(ctx).mActiveProject.mDescription=mDialogDescription.getText().toString();
	     			 }
	     			 
	     			 
	     			 User.get(ctx).mActiveProject.mName = mDialogName.getText().toString();
	     			 User.get(ctx).mActiveProject.mDescription = mDialogDescription.getText().toString();
	     			if(User.get(ctx).mActiveProject!=null) setTitle("Projekt \""+User.get(ctx).mActiveProject.mName+"\"");
	      		  	
	     			ThreadUpdateProject task = new ThreadUpdateProject();
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
    
    private void clearArea(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle("Usuñ wszystkie elementy");
	    builder.setMessage("Czy na pewno chcesz wyszyœciæ obszar?");

	    builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	          mapManager.clear();
	          mapManager.invalidate();
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
    
    
    private class ThreadUpdateProject extends AsyncTask<Void, Void, Void> {
		private ProgressDialog Dialog;

		protected void onPreExecute() {
			Dialog = new ProgressDialog(ctx);
			Dialog.setMessage("Zapisywanie Zmian");
			Dialog.setCanceledOnTouchOutside(false);
			Dialog.setCancelable(false);
			Dialog.show();
		}

		protected Void doInBackground(Void... arg0) {
			updateProjectDB();
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();			
			if(mConnectionError)	Toast.makeText(ctx,"B³¹d Po³¹czenia, Projekt nie zosta³ zapisany", Toast.LENGTH_SHORT).show();	
			else Toast.makeText(ctx,"Zapisano Projekt", Toast.LENGTH_SHORT).show();		
    	}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.l_view_list, menu);
    	TypedValue tv = new TypedValue();
		ctx.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
		MapManager.onScreenY = getResources().getDimensionPixelSize(tv.resourceId);
		MapManager.onScreenX = mapArea.getLeft();
        return true;
    }
    
    @SuppressWarnings("static-access")
	@Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	
    	case R.id.menu_settings :
    		
            startActivity(new Intent(ActivityDesigner.this, ActivityPreferences.class));
    		Log.d("options", "settings");
    		return true;
    	
    	case R.id.menu_logout :
    		Log.d("options", "logout");
    		User.get(ctx).clear();
    		Intent prefIntent = new Intent(this,ActivityLogin.class);
            this.startActivity(prefIntent);
            
    	case R.id.menu_center_view :
    		Log.d("center", "logout");
    		mapManager.centerView();
    		break;
    		
    	case R.id.menu_exit :
    		moveTaskToBack(true);
    		break;
    		
    	case R.id.menu_clear :
    		
    		clearArea();
    		break;
    	case R.id.menu_edit_title :
    		dialogEdit();
    		
    		break;
    		
    	case R.id.menu_custom:
    		//mapManager.getDrawManager().scaleTo(mapManager.getDrawManager().mScale -0.1f, true);

    		mapManager.isCustomDrawning = ! mapManager.isCustomDrawning;
    		mapManager.isWallDrawning = false;
    		menu_custom=item;
    		if(menu_wall!= null ) menu_wall.setIcon(R.drawable.icon_wall);
    		if(	mapManager.isCustomDrawning) 	item.setIcon(R.drawable.icon_custom_active);
    		else							item.setIcon(R.drawable.icon_custom);		
    		break;
    		
    	case R.id.menu_wall:
    		//mapManager.getDrawManager().scaleTo(mapManager.getDrawManager().mScale +0.1f, true);
   
    		mapManager.isWallDrawning = ! mapManager.isWallDrawning;
    		mapManager.isCustomDrawning = false;
    		menu_wall=item;
    		if(menu_custom!= null ) menu_custom.setIcon(R.drawable.icon_custom);
    		if(	mapManager.isWallDrawning) 	item.setIcon(R.drawable.icon_wall_active);
    		else							item.setIcon(R.drawable.icon_wall);		
   		break;
            
    	 case R.id.menu_show_grid :
    		 changeGrid();
    		  break;
    		  
    	 case R.id.menu_save :
    		 User.get(ctx).mActiveProject.mDateUpdate=System.currentTimeMillis();
    		 User.get(ctx).mActiveProject.mFurnitures = mapManager.sFv;
    		 ThreadUpdateProject task= new ThreadUpdateProject();
	 		  task.execute();	 
    		 break;	  
    	}
    	return true;
    }
    
    OnItemLongClickListener listSourceItemLongClickListener = new OnItemLongClickListener(){
    	
    public boolean onItemLongClick(AdapterView<?> l, View v,
      int position, long id) {
    	 Vibrator vibe = ( Vibrator ) getSystemService( VIBRATOR_SERVICE );
         vibe.vibrate( 100 );
    	 ClipData data = ClipData.newPlainText("type", "list");
		 User.dragType="new";
         shadowBuilder = new View.DragShadowBuilder();
         v.startDrag(data,shadowBuilder , aa.data.get(position), 0);
         return true;
    }};
     
    void updateProjectDB(){
    	RestClient connection = new RestClient("http://designercms.herokuapp.com/project/"+ String.valueOf(User.get(ctx).mActiveProject.mId));
		connection.AddParam("username", User.get(ctx).getEmail());
		connection.AddParam("password", User.get(ctx).getPassword());
		
		JSONProjectSimple proj = new JSONProjectSimple(User.get(ctx).mActiveProject);
		connection.AddParam("title", proj.mTitle);
		connection.AddParam("desc", proj.mDescription);
		connection.AddParam("walls", proj.mWalls);
		connection.AddParam("furniture",  proj.mFurnitures);
		
		try {
			connection.Execute(RequestMethod.PUT);	 	
        	String response = connection.getResponse();
        
        	Log.d("response", response);
        	if(!response.contains("ProjectUpdated")){
        		mConnectionError=true;
        	}
      	
		} catch (Exception e){
			Log.d("response2", e.getLocalizedMessage());
			mConnectionError=true;
			e.printStackTrace();	
		}
    }
    
   
    
}
