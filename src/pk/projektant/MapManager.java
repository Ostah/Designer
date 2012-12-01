package pk.projektant;

import android.graphics.Rect;
import android.widget.FrameLayout;

public class MapManager {

	private FrameLayout mapArea;
	private FurnitureView furnitureShadow;
	
	MapManager(FrameLayout mapArea){
		this.mapArea = mapArea;
		furnitureShadow = null;
	}
	
	public void addFurniture(FurnitureView f){
		mapArea.addView(f);
	}
	
	public void moveFurniture(FurnitureView f, float x, float y)
	{	
		//Log.d("move", String.valueOf(x)+" "+String.valueOf(y));
		Boolean valid = isMoveValid(f,x,y);
		if(furnitureShadow == null){
			if(valid){
				f.move(x, y);
				f.invalidate();	
			}
			else{
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
				mapArea.removeView(furnitureShadow);
				furnitureShadow  = null;
				f.move(x, y);
				f.invalidate();		
			}
		}
	}
	
	
	private Boolean isMoveValid(FurnitureView f,float x,float y)
	{
		Rect newPosition = new Rect(f.getRect());
		newPosition.offset((int)x, (int)y);
		
		for(int i=0; i<mapArea.getChildCount();i++)
		{
			if( ((FurnitureView)mapArea.getChildAt(i)).getRect().intersect(newPosition))
			{
				return false;
			}		
		}
		return true;	
	}
}
