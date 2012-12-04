package pk.projektant;


import java.util.ArrayList;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MapManager {

	private FrameLayout mMapArea;
	private FurnitureView mFurnitureShadow;
	private FurnitureView mFurnitureActive;
	private String debugString="";
	private TextView debugTV;
	private ArrayList<FurnitureView> sFv;
	private Context ctx;
	private DrawManager drawManager;

	private float yInit=0.0f;
	private float xInit=0.0f;
	
	private float xDown=0.0f;
	private float yDown=0.0f;
	
	public static float mScale=1.0f;
	public static int mOffsetXBefore=0;
	public static int mOffsetYBefore=0;
	
	public static int mOffsetX=0;
	public static int mOffsetY=0;
	
	public static int  tPX(int p)
	{
		return new Integer(	(int)((p/mScale-mOffsetX))	);
	}
	
	public static int  tPY(int p)
	{
		return new Integer((int)((p/mScale-mOffsetY)));
	}
	
	MapManager(FrameLayout mapArea, Context context){
		sFv = new ArrayList<FurnitureView>();	
		this.mMapArea = mapArea;
		
		 drawManager = new DrawManager(context,sFv);
		mapArea.addView(drawManager);
		mFurnitureShadow = null;
		ctx=context; 
		
		mapArea.setOnTouchListener(new OnTouchListener(){
			
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
	                   {
	                	   mOffsetXBefore = mOffsetX;
	                	   mOffsetYBefore = mOffsetY;
	                    xInit = event.getX();
	                    yInit= event.getY();
	                 //   event.getX()
	                  }
	            }
				if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					Log.d("change",String.valueOf(xInit)+" "+String.valueOf(event.getX())+" "+ String.valueOf(xInit- event.getX())+" "+String.valueOf(yInit- event.getY()));
					mOffsetX=mOffsetXBefore-(int) ((xInit- event.getX()));
					mOffsetY=mOffsetYBefore-(int) ((yInit- event.getY()));
					drawManager.invalidate();
					
				}
				
				return false;
			}

		}) ;
	 
		
		
		mapArea.setOnLongClickListener( new View.OnLongClickListener() {			
			public boolean onLongClick(View view) {
				Log.d("Long", String.valueOf(xInit)+" "+String.valueOf(yInit));
				
				//znajdz prostok¹t
				FurnitureView f = findFurniture((int)xInit, (int)yInit);
				if(f==null )Log.d("CLICK", "null");
				else {
					Log.d("CLICK", f.getString());
					User.newFurniture=false;
					 ClipData data = ClipData.newPlainText("type", "map");
					 DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
					 view.startDrag(data,shadowBuilder , f, 0);
					 mFurnitureActive=f;
					 f.isMoved=true;
				}
				return false;
			}
		});
		
	}
	
	public void scaleChanged(int s)
	{
		mScale = (float) (1.3 +(s-500)*0.002);
		//mOffset 
		drawManager.invalidate();
	}
	
	private FurnitureView findFurniture(int x, int y)
	{
		for(int i=0;i<sFv.size();i++)
		{
			if(pInRect(sFv.get(i).getRect(true),x,y)) return sFv.get(i);
		}
		return null;
	}
	
	static public Boolean pInRect(Rect r, int x, int y)
	{
		if (x>=r.left && x<= r.right && y>= r.top && y<= r.bottom)	{
			return true;
		}
		return false;
	}
	
	public void addFurniture(FurnitureView f){
		sFv.add(f);
		f.isMoved=true;
		mFurnitureActive=f;

	}
	
	public void moveFurniture(FurnitureView f, float x, float y, TextView tv)
	{	
		//x-=f.getWidth()*0.5f;
		//y-=f.getHeigth()*0.5f;
		
		x=tPX((int)x);
		y=tPY((int)y);
		
		mMapArea.invalidate();
		debugTV= tv;
		debugString="pos"+String.valueOf(x)+" "+String.valueOf(x)+"\n";
		debugString+=f.getString();
		//Log.d("move", String.valueOf(x)+" "+String.valueOf(y));
		
		
		Boolean valid = isMoveValid(f,x,y);
		if(mFurnitureShadow == null){
			if(valid){
				f.move(x, y,true);
				drawManager.invalidate();	
			}
			else{
				Log.d("INVALID!","INVALID!");
				mFurnitureShadow = f.getShadow();
				sFv.add(mFurnitureShadow);		
			}
		}
		else{
			if(!valid){
				mFurnitureShadow.move(x, y,true);
				drawManager.invalidate();	
			}
			else{
				Log.d("VALID!","VALID!");
				drawManager.invalidate();	
				sFv.remove(mFurnitureShadow);
				drawManager.invalidate();	
				mFurnitureShadow  = null;
				f.move(x, y,true);
				drawManager.invalidate();	
			}
		}
		tv.setText(debugString);
	}
	
	
	private Boolean isMoveValid(FurnitureView f,float x,float y)
	{
		Rect newPosition = new Rect(f.getRect(true));
		newPosition.offsetTo((int)x, (int)y);
		
		for(int i=0; i<sFv.size();i++)
		{
			
			if(sFv.get(i).equals(f) || (mFurnitureShadow != null && sFv.get(i).equals(mFurnitureShadow))) continue;
			debugString += ((FurnitureView)(sFv.get(i))).getString();
			if(((FurnitureView)sFv.get(i)).getRect(true).intersect(newPosition))
			{
				debugString +="COLLISION!\n";
				return false;
			}		
		}
		return true;	
	}
	
	
	public void drop(){
		if(mFurnitureShadow!= null)
		{
			sFv.remove(mFurnitureShadow);
			mFurnitureShadow = null;
			
		}
		if(mFurnitureActive != null)
		{
			mFurnitureActive.isMoved=false;
			mFurnitureActive=null;
		}
		drawManager.invalidate();
	}
	public void clear(){
			mFurnitureShadow=null;	
			mMapArea.removeViews(0, mMapArea.getChildCount());
	}
}
