package pk.projektant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

@SuppressLint({ "ViewConstructor", "UseValueOf" })
public class FurnitureView {

	
	private Rect rect;
	public Furniture reference;
	static private Context ctx;
	public Boolean isShadow=false;
	public Boolean isMoved=false;
	public Boolean isRotated=false;
	public int mStartX=0;
	public int mStartY=0;
	
	public FurnitureView(Context context, float x, float y, float dx, float dy, int color){
		
		ctx = context;  
        rect = new Rect((int)x,(int)y,(int)(x+dx),(int)(y+dy));
        reference = null;
    }
	
	public FurnitureView(Context context,float x, float y, Furniture reference){
        ctx = context;  
        	this.reference = reference;
            rect = new Rect((int)x,(int)y,(int)(x+this.reference.mX),(int)(y+this.reference.mY));                
    }
	
	public FurnitureView(Context context,float x, float y){
		ctx = context; 
		reference=null;
		mStartX=(int)x;
		mStartY=(int)y;
		rect = new Rect((int)x-1,(int)y-1,(int)x,(int)(y));  
		Log.d("dd","dd");
	}
	
	public String getString()
	{
		String s="";
		s="x:"+String.valueOf(rect.left)+" y: "+String.valueOf(rect.top) ;
		s += " "+String.valueOf(rect.right-rect.left)+"x"+String.valueOf(rect.bottom-rect.top)+"\n" ;
		return s;
	}
	
	public void rotate()
	{
		isRotated=!isRotated;
		rect = getRotatedRect();
	}
	
	public void resize(float x, float y)
	{
		Rect a = rect;
		if(x>=mStartX){
			a.left=mStartX;
			a.right=(int) x;
		}
		else if(x<mStartX){
			a.right=mStartX;
			a.left=(int) x;
		}
		
		if(y>=mStartY){
			a.top=mStartY;
			a.bottom=(int) y;
		}
		else if(y<mStartY){
			a.bottom=mStartY;
			a.top=(int) y;
		}
	}
	
	public Rect getRotatedRect()
	{
		int left =(int)( rect.left+0.5*rect.width()-0.5*rect.height());
		int top = (int)(rect.top-0.5*rect.width()+0.5*rect.height());
		
		return new Rect(left,top,left+rect.height(),top+rect.width());
	}
	
	public Rect rectInScale(DrawManager d){
			Rect scaled = new Rect(rect);
			scaled.top*=d.mScale;
			scaled.bottom*=d.mScale;
			scaled.left*=d.mScale;
			scaled.right*=d.mScale;
			scaled.offset(d.mOffsetX, d.mOffsetY);
			return scaled;
	}
	public int getWidth(){
		return rect.right-rect.left;
	}
	public int getHeigth(){
		return rect.bottom-rect.top;
	}
	
	public FurnitureView(Context context, float x, float y, float dx, float dy){

        ctx = context;  
        rect = new Rect((int)x,(int)y,(int)(x+dx),(int)(y+dy));
        reference = null;
    }
	
	  
	public FurnitureView(FurnitureView f) {
		this.reference = f.reference;
		this.mStartX = new Integer(f.mStartX);
		this.mStartY = new Integer(f.mStartY);
		this.rect = new Rect(f.rect);
		this.reference = f.reference;	
	}
	
	public FurnitureView clone(){
		return new FurnitureView(this);
	}
	protected void draw(Canvas canvas, Paint paint, Paint paint2, DrawManager d){

	        canvas.drawRect(rectInScale(d), paint);
	
	        canvas.drawCircle(rectInScale(d).left, rectInScale(d).top, 2, paint2);
	        canvas.drawCircle(rectInScale(d).left, rectInScale(d).bottom,2, paint2);
	        canvas.drawCircle(rectInScale(d).right, rectInScale(d).top, 2, paint2);
	        canvas.drawCircle(rectInScale(d).right, rectInScale(d).bottom, 2, paint2);
	}
	
	protected void move(float x, float y, Boolean rescale){
		if(rescale)
		{
			//x=x-MapManager.mOffsetX;
			//x=x/MapManager.mScale;
			
			//y=y-MapManager.mOffsetY;
			//y=y/MapManager.mScale;	
		}
			
			
	       rect.offsetTo((int)(x), (int)(y));
	}
	
	public Rect getRect(Boolean transfigured){
		Rect toReturn = new Rect(rect);		
		if(transfigured)
		{
			toReturn.top*=MapManager.drawManager.mScale;
			toReturn.bottom*=MapManager.drawManager.mScale;
			toReturn.left*=MapManager.drawManager.mScale;
			toReturn.right*=MapManager.drawManager.mScale;
			toReturn.offset(MapManager.drawManager.mOffsetX, MapManager.drawManager.mOffsetY);	
		}
		return toReturn;
	}
	public void setRect(Rect r){
		rect = r;
	}
	public Rect getResizedRect(float x, float y)
	{
		Rect a = new Rect(rect);
		if(x>=mStartX){
			a.left=mStartX;
			a.right=(int) x;
		}
		else if(x<mStartX){
			a.right=mStartX;
			a.left=(int) x;
		}
		
		if(y>=mStartY){
			a.top=mStartY;
			a.bottom=(int) y;
		}
		else if(y<mStartY){
			a.bottom=mStartY;
			a.top=(int) y;
		}
		return a;
	}
	
	public FurnitureView getShadow()
	{
		FurnitureView f = new FurnitureView(this);
		f.isShadow = true;
		return f;
	}
	
}
