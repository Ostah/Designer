package pk.projektant;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

public class ActivityDesigner extends Activity {
	
	private ListView listViewCity;
	private FrameLayout mapArea;
	private Context ctx;
	private  DragShadowBuilder shadowBuilder;
	FurnitureView activeFurniture;
	List<Furniture> furnitureList; 
	ArrayList<FurnitureView> sFurnitures;
	private MapManager mapManager;
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.l_view_list);
        furnitureList = Tokenizer.sFurnitures;
        
        listViewCity = ( ListView ) findViewById( R.id.listFurnitures);
        listViewCity.setAdapter( new FurnitureListAdapter(ctx, R.layout.l_view_list_element, furnitureList ) );
        
        mapArea = (FrameLayout)findViewById(R.id.FrameLaytoDraw);     
        mapManager = new MapManager(mapArea);
     
        listViewCity.setOnItemLongClickListener(listSourceItemLongClickListener);
        mapArea.setOnDragListener(new MyDragListener());
 
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.l_view_list, menu);
        return true;
    }
    

    OnItemLongClickListener listSourceItemLongClickListener = new OnItemLongClickListener(){

    public boolean onItemLongClick(AdapterView<?> l, View v,
      int position, long id) {
     
    	 ClipData data = ClipData.newPlainText("", "");
         shadowBuilder = new View.DragShadowBuilder(v);
         v.startDrag(data, shadowBuilder, furnitureList.get(position), 0);
         return true;
    }};
    
    class MyDragListener implements OnDragListener {
    	  Drawable enterShape = getResources().getDrawable(R.drawable.pe084883);
    	  Drawable normalShape = getResources().getDrawable(R.drawable.pe088472);
    	  

    	  public boolean onDrag(View v, DragEvent event) {
    	    switch (event.getAction()) {
    	    case DragEvent.ACTION_DRAG_STARTED:
    	    	activeFurniture = new FurnitureView(ctx,-200,-200, (Furniture) event.getLocalState());
    	    	mapManager.addFurniture(activeFurniture);
    	      break;
    	    case DragEvent.ACTION_DRAG_ENTERED: 
    	    	
    	    	//((FrameLayout)mapArea).addView(a);
    	      break;
    	    case DragEvent.ACTION_DRAG_EXITED:        
    	     // v.setBackgroundDrawable(normalShape);
    	      break;
    	    case DragEvent.ACTION_DROP:
    	     
    	      break;
    	    case DragEvent.ACTION_DRAG_LOCATION:
    	    	mapManager.moveFurniture(activeFurniture, event.getX(),event.getY());
    	    	//Log.d(String.valueOf(event.getX())+" "+String.valueOf(event.getY()),"ll");
    	    	
    	    case DragEvent.ACTION_DRAG_ENDED:
    	     
    	      default:
    	      break;
    	    }
    	    return true;
    	  }
    	} 
}
