package pk.projektant;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.Toast;

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
	
	private Boolean isScaling=false;
	private float xDown=0.0f;
	private float yDown=0.0f;
	ScaleGestureDetector scaleGestureDetector;
	public static float mScale=1.0f;
	public static int mOffsetXBefore=0;
	public static int mOffsetYBefore=0;
	
	public static int mOffsetX=0;
	public static int mOffsetY=0;
	public static int width=0;
	public static int heigth=0;
	
	public long tapStart=0;
	public long tapDuration=0;
	public int 	tapCount=0;
	public static int  tPX(int p)
	{
		return new Integer(	(int)((p-mOffsetX)/mScale)	);
	}
	
	public static int  tPY(int p)
	{
		return new Integer((int)((p-mOffsetY)/mScale));
	}
	
	MapManager(FrameLayout mapArea, Context context){
		sFv = new ArrayList<FurnitureView>();	
		this.mMapArea = mapArea;
		scaleGestureDetector = new ScaleGestureDetector(context, new simpleOnScaleGestureListener());	
		drawManager = new DrawManager(context,sFv);
		mapArea.addView(drawManager);
		
		drawManager.invalidate();
		mFurnitureShadow = null;
		ctx=context; 
		
		mapArea.setOnTouchListener(new OnTouchListener(){
			
			public boolean onTouch(View v, MotionEvent event)
			{
				width=mMapArea.getWidth();
				heigth=mMapArea.getHeight();
				 scaleGestureDetector.onTouchEvent(event);
				 if(isScaling) return false;
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
						tapStart = System.currentTimeMillis();
						tapCount++;
	                	drawManager.invalidate();
	                	mOffsetXBefore = mOffsetX;
	                	mOffsetYBefore = mOffsetY;
	                    xInit = event.getX();
	                    yInit= event.getY();
	    
	                  
	            }
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					 long time = System.currentTimeMillis() - tapStart;
					 tapDuration=  tapDuration + time;
					 if(tapCount == 2)
			            {
						 	Log.d("time", String.valueOf(time));
			                if(time <= 70)
			                {
			                	FurnitureView f = findFurniture((int)xInit, (int)yInit);
			    				if(f==null )Log.d("CLICK", "null");
			    				else {
			    					//Log.d("CLICK", f.getString());
			    					//showPopupMenu(mMapArea);
			    					final CharSequence[] items = {"Info", "Rotate", "Delete"};

			    					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			    					builder.setTitle("Make your selection");
			    					builder.setItems(items, new DialogInterface.OnClickListener() {
			    					    public void onClick(DialogInterface dialog, int item) {
			    					         // Do something with the selection
			    					    }
			    					});
			    					AlertDialog alert = builder.create();
			    					alert.show();
			    				//	f.rotate();
								//	drawManager.invalidate();
			    				}
			                	
			                    Toast.makeText(ctx, "double tap",Toast.LENGTH_SHORT).show();
			                }
			                tapCount = 0;
			                tapDuration = 0;
			                return false;
			            }
					
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					//Log.d("change",String.valueOf(xInit)+" "+String.valueOf(event.getX())+" "+ String.valueOf(xInit- event.getX())+" "+String.valueOf(yInit- event.getY()));
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
					//Log.d("CLICK", f.getString());
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
	
//	public void scaleChanged(float factor)
//	{
//		if(factor<1.0f && mScale<0.1) return;
//		if(factor>1.0f && mScale>5) return;
//		float scaleChange = mScale;
//		debugString=String.valueOf(mMapArea.getWidth())+"x"+ String.valueOf(mMapArea.getHeight())+"\n";
//		mScale *=factor;
//		scaleChange = mScale-scaleChange;
//		//mOffsetX= (int) ((mOffsetX+mMapArea.getWidth()*0.5)*scaleChange);
//		//mOffsetY= (int) ((mOffsetY+mMapArea.getHeight()*0.5)*scaleChange);
//		
//		mOffsetX-= (int) ((int) (mMapArea.getWidth()*0.5)*scaleChange);
//		mOffsetY-= (int) ((int) (mMapArea.getHeight()*0.5)*scaleChange);
//		drawManager.invalidate();
//	}
	
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
		//x-=f.getWidth()*mScale*0.5f;
		//y-=f.getHeigth()*mScale*0.5f;
		
		x=tPX((int)x);
		y=tPY((int)y);
		
		mMapArea.invalidate();
		debugTV= tv;
		debugString="pos"+String.valueOf(x)+" "+String.valueOf(x)+"\n";
		debugString+="OFFSET"+String.valueOf(mOffsetX)+" "+String.valueOf(mOffsetY)+"\n";
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
		Rect newPosition = new Rect(f.getRect(false));
		newPosition.offsetTo((int)x, (int)y);
		
		for(int i=0; i<sFv.size();i++)
		{
			
			if(sFv.get(i).equals(f) || (mFurnitureShadow != null && sFv.get(i).equals(mFurnitureShadow))) continue;
			debugString += ((FurnitureView)(sFv.get(i))).getString();
			if(((FurnitureView)sFv.get(i)).getRect(false).intersect(newPosition))
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
	public class simpleOnScaleGestureListener extends
	  SimpleOnScaleGestureListener {
		float mStartScale=1.0f;

	  public boolean onScale(ScaleGestureDetector detector) {
		
		 	float factor=detector.getScaleFactor();
		 	if(mStartScale*factor<0.2 || mStartScale*factor>5 ) return false;
		 	
			float scaleChange = mScale;
			debugString=String.valueOf(mMapArea.getWidth())+"x"+ String.valueOf(mMapArea.getHeight())+"\n";
			
			mScale = mStartScale*factor;
			scaleChange = mScale-scaleChange;
			//mOffsetX= (int) ((mOffsetX+mMapArea.getWidth()*0.5)*scaleChange);
			//mOffsetY= (int) ((mOffsetY+mMapArea.getHeight()*0.5)*scaleChange);
			
			mOffsetX-= (int) ((int) (mMapArea.getWidth()*0.5)*scaleChange);
			mOffsetY-= (int) ((int) (mMapArea.getHeight()*0.5)*scaleChange);
			drawManager.invalidate();
			debugString = "SCALE: "+String.valueOf(mScale);
			debugTV.setText(debugString);
		    return false;
		   }

		   @Override
		   public boolean onScaleBegin(ScaleGestureDetector detector) {
		    // TODO Auto-generated method stub
			   mStartScale=mScale;
			   isScaling=true;
		    return true;
		   }

		   @Override
		   public void onScaleEnd(ScaleGestureDetector detector) {
		    // TODO Auto-generated method stub
	
			   isScaling=false;
		   }

		  }  
	

}
