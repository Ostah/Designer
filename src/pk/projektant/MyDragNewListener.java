package pk.projektant;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

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
	    	if(valid)
	    	{
		    	if(User.dragType == "new")
		    	{
		    		activeFurniture = new FurnitureView(-200,-200, (Furniture) event.getLocalState());
			    	mapManager.addFurniture(activeFurniture);
			    	
		    	}
		    	else if(User.dragType == "custom"||User.dragType == "wall")
		    	{
		    		activeFurniture = new FurnitureView(MapManager.tPX((int) (event.getX()- MapManager.onScreenX)),MapManager.tPY((int) (event.getY()-MapManager.onScreenY)));
		    		mapManager.addFurniture(activeFurniture);
		    		if(User.dragType == "wall"){
		    			activeFurniture.isWall=true;
		    		}
		    	}
		    	else
		    	{
		    		activeFurniture =  (FurnitureView) event.getLocalState();
		    	}	
	    	}
	      break;  
	      
	    case DragEvent.ACTION_DROP:
	    	mapManager.drop(activeFurniture);
	      break;
	      
	    case DragEvent.ACTION_DRAG_LOCATION:	
	    	if(valid) mapManager.moveFurniture(activeFurniture, event.getX(),event.getY()); 	
	    	break;
	    	
	      default:
	      break;
	    }
	    return true;
	  }
	}