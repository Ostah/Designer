package pk.projektant;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.FrameLayout;
import android.view.View;

public class DrawManager extends View{
	Context ctx;
	private Paint d_custom = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_grid[];
	private Paint d_shadow = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_active = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_black = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint[]	FurnituresPaints = new Paint[8];
	
	public  float mScale = 1.0f;
	public  int mOffsetX = 0;
	public  int mOffsetY = 0;
	
	private  ArrayList<FurnitureView> sFv;
	SharedPreferences pref ;
	
	public DrawManager(Context context, ArrayList<FurnitureView> furnitures, float scale) {	
		super(context);
		
		mScale = scale;
		sFv = furnitures;
		ctx = context;
		pref = context.getSharedPreferences("options", 0);
		d_custom.setColor(0xFF606048);
		d_shadow.setColor(0x80FF0000);
		d_active.setColor(0xFF00CC00);
		d_black.setColor(Color.BLACK);
		
		d_grid = new Paint[2];
		d_grid[0]  = new Paint(Paint.ANTI_ALIAS_FLAG);
		d_grid[1]  = new Paint(Paint.ANTI_ALIAS_FLAG);
		d_grid[0].setColor(0x20000000);
		d_grid[1].setColor(0x10000000);
		
		for(int i=0;i<8;i++) FurnituresPaints[i]= new Paint(Paint.ANTI_ALIAS_FLAG);
		FurnituresPaints[0].setColor(0xFF606048);
		FurnituresPaints[1].setColor(0xFF33B5E5);
		FurnituresPaints[2].setColor(0xFFAA66CC);
		FurnituresPaints[3].setColor(0xFF99CC00);
		FurnituresPaints[4].setColor(0xFFFFBB33);
		FurnituresPaints[5].setColor(0xFFFF4444);
		FurnituresPaints[6].setColor(0xFFFF3D7F);
		FurnituresPaints[7].setColor(0xFFFF5500);
	
	}
	@Override
	protected void onDraw(Canvas canvas){

		if(pref.getBoolean("showGrid", true)) drawGrid(canvas);
		
		   for(int i=0; i<sFv.size();i++){
			   if		(sFv.get(i).isShadow){
				   sFv.get(i).draw(canvas, d_shadow, d_black,this);
			   }
			   else if	(sFv.get(i).isMoved){
				   sFv.get(i).draw(canvas, d_active, d_black,this);
			   }
			   else if	(sFv.get(i).isWall){
				   sFv.get(i).draw(canvas, d_black, d_black,this);
			   }
			   else if	(sFv.get(i).reference==null){
				   sFv.get(i).draw(canvas, d_custom, d_black,this);
			   }
			   else 				{
				   int col = sFv.get(i).reference.color;
				   sFv.get(i).draw(canvas, FurnituresPaints[col], d_black,this);
			   }
				 
		   }
	}
	public void scaleTo(float newScale){
		int myWidth=((View)this.getParent()).getWidth();
		int myHeigth=((View)this.getParent()).getHeight();
		
		float scaleChange = mScale - newScale;

		mScale = newScale;
		

		mOffsetX += (int) ((int) (myWidth * 0.5) * scaleChange);
		mOffsetY += (int) ((int) (myHeigth * 0.5) * scaleChange);
		invalidate();

	}
	public void changeFurnitures( ArrayList<FurnitureView> furniture)
	{
		sFv = furniture;
		invalidate();
	}
	
	public void clear()
	{
		sFv= new ArrayList<FurnitureView>();
		invalidate();
		
	}
	protected void drawGrid(Canvas canvas)
	{
		
		int gridSize=10;
		
		
		int myWidth=((View)this.getParent()).getWidth();
		int myHeigth=((View)this.getParent()).getHeight();
		
		if(mScale<=0.2) gridSize = 60;
		else if(mScale<0.3) gridSize = 30;
		else if(mScale<0.6) gridSize = 20;
		else if(mScale>3) gridSize = 5;
		for(int i=0;true;i++)
		{	
			if(0+gridSize*mScale*i+mOffsetX>myWidth) break;
			canvas.drawLine(0+gridSize*mScale*i+mOffsetX, 0, 0+gridSize*mScale*i+mOffsetX,myHeigth, d_grid[i%2]);
		}	
		for(int i=1;true;i++)
		{	
			if(0-gridSize*mScale*i+mOffsetX<0) break;
			canvas.drawLine(0-gridSize*mScale*i+mOffsetX, 0, 0-gridSize*mScale*i+mOffsetX,myHeigth, d_grid[i%2]);
		}	
		
		for(int i=0;true;i++)
		{	
			if(0+gridSize*mScale*i+mOffsetY>myHeigth) break;
			canvas.drawLine( 0,0+gridSize*mScale*i+mOffsetY,myWidth, 0+gridSize*mScale*i+mOffsetY, d_grid[i%2]);
		}	
		for(int i=1;true;i++)
		{	
			if(0-gridSize*mScale*i+mOffsetY<0) break;
			canvas.drawLine( 0,0-gridSize*mScale*i+mOffsetY,myWidth, 0-gridSize*mScale*i+mOffsetY, d_grid[i%2]);
		}
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    this.setMeasuredDimension(parentWidth, parentHeight);
	}
	  
	
}
