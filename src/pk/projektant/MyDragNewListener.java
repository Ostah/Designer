package pk.projektant;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.TextView;

class MyDragNewListener implements OnDragListener {
	  static FurnitureView activeFurniture;
	  static private MapManager mapManager;
	  static  Context ctx;
	  private Boolean valid = false;
	  
	  MyDragNewListener(MapManager m, Context c, Boolean v)
	  {
		  mapManager = m;
		  ctx = c;
	
		  valid = v;
	  }
	  
	  public boolean onDrag(View v, DragEvent event) {
	    switch (event.getAction()) {
	    case DragEvent.ACTION_DRAG_STARTED:
	    	Log.d("drag", "started");
	    	if(User.dragType == "new")
	    	{
	    		activeFurniture = new FurnitureView(ctx,-200,-200, (Furniture) event.getLocalState());
		    	mapManager.addFurniture(activeFurniture);
	    	}
	    	else if(User.dragType == "wall")
	    	{
	    		
	    		int aaa = mapManager.mMapArea.getTop() ;
	    		activeFurniture = new FurnitureView(ctx,event.getX()- mapManager.onScreenX,event.getY()-mapManager.onScreenY);
	    		mapManager.addFurniture(activeFurniture);
	    	}
	    	else
	    	{
	    		activeFurniture =  (FurnitureView) event.getLocalState();
	    	}
	    	
	      break;
	    case DragEvent.ACTION_DRAG_ENTERED: 
	    	Log.d("drag", "entered");
	      break;
	    case DragEvent.ACTION_DRAG_EXITED:  
	    	Log.d("drag", "exited");
	    	//mapManager.removeView(activeFurniture);
	    	//activeFurniture=null;
	      break;
	    case DragEvent.ACTION_DROP:
	    	Log.d("drag", "drop");
	    	mapManager.drop();
	      break;
	    case DragEvent.ACTION_DRAG_LOCATION:	
	    	Log.d("drag", "location");
	    	if(valid) mapManager.moveFurniture(activeFurniture, event.getX(),event.getY());
	    	//Log.d(String.valueOf(event.getX())+" "+String.valueOf(event.getY()),"ll");
	    	
	    case DragEvent.ACTION_DRAG_ENDED:
	    Log.d("drag", "ended");
	    	
	      default:
	      break;
	    }
	    return true;
	  }
	}