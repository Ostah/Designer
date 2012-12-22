package pk.projektant;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ActivityDesigner extends SherlockActivity {
	
	private ListView listViewCity;
	private FrameLayout mapArea;
	private Context ctx;
	private  DragShadowBuilder shadowBuilder;
	private MapManager mapManager;
	List<Furniture> furnitureList; 
	ArrayList<FurnitureView> sFurnitures;

	TextView txtDebug = null;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	
    	super.onWindowFocusChanged(hasFocus);
    	MapManager.width =mapArea.getWidth();
    	MapManager.heigth = mapArea.getHeight();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.l_view_list);
      
        furnitureList = Tokenizer.sFurnitures;
        LinearLayout temporat = ( LinearLayout ) findViewById( R.id.LinearLayout2);
        temporat.setBackgroundColor(Color.TRANSPARENT);
        listViewCity = ( ListView ) findViewById( R.id.listFurnitures);
        listViewCity.setAdapter( new FurnitureListAdapter(ctx, R.layout.l_view_list_element, furnitureList ) );      
        mapArea = (FrameLayout)findViewById(R.id.FrameLaytoDraw);     
        mapManager = new MapManager(mapArea, ctx,this);
        txtDebug = (TextView) findViewById(R.id.debug1);
        listViewCity.setOnItemLongClickListener(listSourceItemLongClickListener);
        listViewCity.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MapManager.showProperties(furnitureList.get(arg2)); // to musi byc po inicjalizacji managera!
				
			}
		});
        mapArea.setOnDragListener(new MyDragNewListener(mapManager, ctx, txtDebug,true));
        listViewCity.setOnDragListener(new MyDragNewListener(mapManager, ctx, txtDebug,false));
        getSupportActionBar();
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
   
    private void clearArea(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle("Usuñ wszystkie elementy");
	    builder.setMessage("Czy na pewno chcesz wyszyœciæ obszar?");

	    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	          mapManager.clear();
	          mapManager.invalidate();
	          dialog.dismiss();
	        }

	    });

	    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });

	    AlertDialog  alert = builder.create();
	    alert.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	getSupportMenuInflater().inflate(R.menu.l_view_list, menu);
    	int a = listViewCity.getWidth();
    	
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
    		
    	case R.id.menu_wall:
    		mapManager.isWallDrawning = ! mapManager.isWallDrawning;
    		if(	mapManager.isWallDrawning) 	item.setIcon(R.drawable.icon_wall_active);
    		else							item.setIcon(R.drawable.icon_wall);		
    		break;
            
    	 case R.id.menu_show_grid :
    		 changeGrid();
    		  break;
    		  
    	}
   
    	return true;
    }
    
    OnItemLongClickListener listSourceItemLongClickListener = new OnItemLongClickListener(){

    
    
    public boolean onItemLongClick(AdapterView<?> l, View v,
      int position, long id) {
    	Log.d("OMG", "FUCK!");
    	 Vibrator vibe = ( Vibrator ) getSystemService( VIBRATOR_SERVICE );
         vibe.vibrate( 100 );
    	 ClipData data = ClipData.newPlainText("type", "list");
    	 User.get(ctx).dragType="new";
         shadowBuilder = new View.DragShadowBuilder();
         v.startDrag(data,shadowBuilder , furnitureList.get(position), 0);
         return true;
    }};
     
}
