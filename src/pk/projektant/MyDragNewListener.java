package pk.projektant;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.TextView;

class MyDragNewListener implements OnDragListener {
	//  Drawable enterShape = getResources().getDrawable(R.drawable.pe084883);
	 // Drawable normalShape = getResources().getDrawable(R.drawable.pe088472);
	  FurnitureView activeFurniture;
	  private MapManager mapManager;
	  private TextView debugTxt;
	  Context ctx;
	
	  MyDragNewListener(MapManager m, Context c, TextView debug)
	  {
		  mapManager = m;
		  ctx = c;
		  debugTxt = debug;
	  }
	  
	  public boolean onDrag(View v, DragEvent event) {
	    switch (event.getAction()) {
	    case DragEvent.ACTION_DRAG_STARTED:
	    	if(User.newFurniture)
	    	{
	    		activeFurniture = new FurnitureView(ctx,-200,-200, (Furniture) event.getLocalState());
		    	mapManager.addFurniture(activeFurniture);
	    	}
	    	else
	    	{
	    		activeFurniture =  (FurnitureView) event.getLocalState();
	    	}
	    	
	      break;
	    case DragEvent.ACTION_DRAG_ENTERED: 
	    	
	      break;
	    case DragEvent.ACTION_DRAG_EXITED:        
	 
	     // v.setBackgroundDrawable(normalShape);
	      break;
	    case DragEvent.ACTION_DROP:
	    	
	    	mapManager.drop();
	      break;
	    case DragEvent.ACTION_DRAG_LOCATION:
	    	mapManager.moveFurniture(activeFurniture, event.getX(),event.getY(),debugTxt);
	    	//Log.d(String.valueOf(event.getX())+" "+String.valueOf(event.getY()),"ll");
	    	
	    case DragEvent.ACTION_DRAG_ENDED:
	   
	    	
	      default:
	      break;
	    }
	    return true;
	  }
	}