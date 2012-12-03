package pk.projektant;


import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MapManager {

	private FrameLayout mapArea;
	private FurnitureView furnitureShadow;
	private String debugString="";
	private TextView debugTV;
	MapManager(FrameLayout mapArea){
		this.mapArea = mapArea;
		furnitureShadow = null;
	}
	
	public void addFurniture(FurnitureView f){
		mapArea.addView(f);
		mapArea.setBackgroundColor(Color.TRANSPARENT);
		//mapArea.setOnDragListener(l)
		f.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				
			 Log.d("CLICK", "click! + "+v.toString())		;
//				 
//				 ClipData data = ClipData.newPlainText("", "");
//				 DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
//
//		         v.startDrag(data,shadowBuilder , v, 0);
		         
				return false;
			}
		});   
	}
	
	public void moveFurniture(FurnitureView f, float x, float y, TextView tv)
	{	
		debugTV= tv;
		debugString=f.getString();
		//Log.d("move", String.valueOf(x)+" "+String.valueOf(y));
		Boolean valid = isMoveValid(f,x,y);
		if(furnitureShadow == null){
			if(valid){
				f.move(x, y);
				f.invalidate();	
			}
			else{
				Log.d("INVALID!","INVALID!");
			//	Log.d("shadow before:",String.valueOf(f.co))
				furnitureShadow = f.getShadow();
				mapArea.addView(furnitureShadow);
			}
		}
		else{
			if(!valid){
				furnitureShadow.move(x, y);
				furnitureShadow.invalidate();
			}
			else{
				Log.d("VALID!","VALID!");
				furnitureShadow.invalidate();
				mapArea.removeView(furnitureShadow);
				furnitureShadow.invalidate();
				furnitureShadow  = null;
				f.move(x, y);
				f.invalidate();		
			}
		}
		tv.setText(debugString);
	}
	
	
	private Boolean isMoveValid(FurnitureView f,float x,float y)
	{
		Rect newPosition = new Rect(f.getRect());
		newPosition.offsetTo((int)x, (int)y);
		
		for(int i=0; i<mapArea.getChildCount();i++)
		{
			
			if(mapArea.getChildAt(i).equals(f) || (furnitureShadow != null && mapArea.getChildAt(i).equals(furnitureShadow))) continue;
			debugString += ((FurnitureView)(mapArea.getChildAt(i))).getString();
			if(((FurnitureView)mapArea.getChildAt(i)).getRect().intersect(newPosition))
			{
				debugString +="COLLISION!\n";
				return false;
			}		
		}
		return true;	
	}
	
	public void drop(){
		if(furnitureShadow!= null)
		{
			mapArea.removeView(furnitureShadow);
			furnitureShadow = null;
			mapArea.invalidate();
		}
	}
	public void clear(){
			furnitureShadow=null;	
			mapArea.removeViews(0, mapArea.getChildCount());
	}
}
