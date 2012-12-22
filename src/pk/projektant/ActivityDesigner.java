package pk.projektant;

import java.util.ArrayList;
import java.util.List;

import org.codegist.crest.CRest;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActivityDesigner extends Activity {
	
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
        mapManager = new MapManager(mapArea, ctx);
        txtDebug = (TextView) findViewById(R.id.debug1);
        listViewCity.setOnItemLongClickListener(listSourceItemLongClickListener);
        listViewCity.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MapManager.showProperties(furnitureList.get(arg2)); // to musi byc po inicjalizacji managera!
				
			}
		});
        mapArea.setOnDragListener(new MyDragNewListener(mapManager, ctx, txtDebug));
        
        
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.l_view_list, menu);
        return true;
    }
    

    OnItemLongClickListener listSourceItemLongClickListener = new OnItemLongClickListener(){

    public boolean onItemLongClick(AdapterView<?> l, View v,
      int position, long id) {
    	Log.d("OMG", "FUCK!");
    	  Vibrator vibe = ( Vibrator ) getSystemService( VIBRATOR_SERVICE );
          vibe.vibrate( 100 );
    	 ClipData data = ClipData.newPlainText("type", "list");
    	 User.get(ctx).newFurniture=true;
         shadowBuilder = new View.DragShadowBuilder();
         v.startDrag(data,shadowBuilder , furnitureList.get(position), 0);
         return true;
    }};
     
}
