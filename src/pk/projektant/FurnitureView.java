package pk.projektant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

@SuppressLint({ "ViewConstructor", "UseValueOf" })
public class FurnitureView {

	
	private Rect rect;
	private Furniture reference;
	static private Context ctx;
	public Boolean isShadow=false;
	public Boolean isMoved=false;
	
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
	
	public String getString()
	{
		String s="";
		s="x:"+String.valueOf(rect.left)+" y: "+String.valueOf(rect.top) ;
		s += " "+String.valueOf(rect.right-rect.left)+"x"+String.valueOf(rect.bottom-rect.top)+"\n" ;
		return s;
	}
	
	public Rect rectInScale(){
			Rect scaled = new Rect(rect);
			scaled.top*=MapManager.mScale;
			scaled.bottom*=MapManager.mScale;
			scaled.left*=MapManager.mScale;
			scaled.right*=MapManager.mScale;
			scaled.offset(MapManager.mOffsetX, MapManager.mOffsetY);
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
		this.rect = new Rect(f.rect);
		this.reference = f.reference;	
	}

	protected void draw(Canvas canvas, Paint paint, Paint paint2){

	        canvas.drawRect(rectInScale(), paint);
	
	        canvas.drawCircle(rectInScale().left, rectInScale().top, 2, paint2);
	        canvas.drawCircle(rectInScale().left, rectInScale().bottom,2, paint2);
	        canvas.drawCircle(rectInScale().right, rectInScale().top, 2, paint2);
	        canvas.drawCircle(rectInScale().right, rectInScale().bottom, 2, paint2);
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
			toReturn.top*=MapManager.mScale;
			toReturn.bottom*=MapManager.mScale;
			toReturn.left*=MapManager.mScale;
			toReturn.right*=MapManager.mScale;
			toReturn.offset(MapManager.mOffsetX, MapManager.mOffsetY);	
		}
		return toReturn;
	}
	
	public FurnitureView getShadow()
	{
		FurnitureView f = new FurnitureView(this);
		f.isShadow = true;
		return f;
	}
	
}
