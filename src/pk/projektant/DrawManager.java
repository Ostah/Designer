package pk.projektant;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawManager extends View{
	Context ctx;
	private Paint d_normal = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_grid[];
	private Paint d_shadow = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_active = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint draw2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint[]	FurnituresPaints = new Paint[8];
	
	private  ArrayList<FurnitureView> sFv;
	SharedPreferences pref ;
	
	public DrawManager(Context context, ArrayList<FurnitureView> furnitures) {	
		super(context);
		
		
		sFv = furnitures;
		ctx = context;
		pref = context.getSharedPreferences("options", 0);
		d_normal.setColor(0xFF99CCFF);
		d_shadow.setColor(0x80FF0000);
		d_active.setColor(0xFF00CC00);
		draw2.setColor(Color.BLACK);
		d_grid = new Paint[2];
		d_grid[0]  = new Paint(Paint.ANTI_ALIAS_FLAG);
		d_grid[1]  = new Paint(Paint.ANTI_ALIAS_FLAG);
		d_grid[0].setColor(0x20000000);
		d_grid[1].setColor(0x10000000);
		
		for(int i=0;i<8;i++) FurnituresPaints[i]= new Paint(Paint.ANTI_ALIAS_FLAG);
		FurnituresPaints[0].setColor(0x00000000);
		FurnituresPaints[1].setColor(0xFF33B5E5);
		FurnituresPaints[2].setColor(0xFFAA66CC);
		FurnituresPaints[3].setColor(0xFF99CC00);
		FurnituresPaints[4].setColor(0xFFFFBB33);
		FurnituresPaints[5].setColor(0xFFFF4444);
		FurnituresPaints[6].setColor(0xFF0099CC);
		FurnituresPaints[7].setColor(0xFF9933CC);
	
	}
	@Override
	protected void onDraw(Canvas canvas){

		if(pref.getBoolean("showGrid", true)) drawGrid(canvas);
		
		   for(int i=0; i<sFv.size();i++){
			   if		(sFv.get(i).isShadow) 		sFv.get(i).draw(canvas, d_shadow, draw2);
			   else if	(sFv.get(i).isMoved)		sFv.get(i).draw(canvas, d_active, draw2);
			   else if	(sFv.get(i).reference==null)sFv.get(i).draw(canvas, draw2, draw2);
			   else 				{
				   int col = sFv.get(i).reference.color;
				   sFv.get(i).draw(canvas, FurnituresPaints[col], draw2);
			   }
				 
		   }
	}
	protected void drawGrid(Canvas canvas)
	{
		
		int gridSize=10;
		
		if(MapManager.mScale<0.3) gridSize = 30;
		else if(MapManager.mScale<0.6) gridSize = 20;
		else if(MapManager.mScale>3) gridSize = 5;
		for(int i=0;true;i++)
		{	
			if(0+gridSize*MapManager.mScale*i+MapManager.mOffsetX>MapManager.width) break;
			canvas.drawLine(0+gridSize*MapManager.mScale*i+MapManager.mOffsetX, 0, 0+gridSize*MapManager.mScale*i+MapManager.mOffsetX,MapManager.heigth, d_grid[i%2]);
		}	
		for(int i=1;true;i++)
		{	
			if(0-gridSize*MapManager.mScale*i+MapManager.mOffsetX<0) break;
			canvas.drawLine(0-gridSize*MapManager.mScale*i+MapManager.mOffsetX, 0, 0-gridSize*MapManager.mScale*i+MapManager.mOffsetX,MapManager.heigth, d_grid[i%2]);
		}	
		
		for(int i=0;true;i++)
		{	
			if(0+gridSize*MapManager.mScale*i+MapManager.mOffsetY>MapManager.heigth) break;
			canvas.drawLine( 0,0+gridSize*MapManager.mScale*i+MapManager.mOffsetY,MapManager.width, 0+gridSize*MapManager.mScale*i+MapManager.mOffsetY, d_grid[i%2]);
		}	
		for(int i=1;true;i++)
		{	
			if(0-gridSize*MapManager.mScale*i+MapManager.mOffsetY<0) break;
			canvas.drawLine( 0,0-gridSize*MapManager.mScale*i+MapManager.mOffsetY,MapManager.width, 0-gridSize*MapManager.mScale*i+MapManager.mOffsetY, d_grid[i%2]);
		}
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    this.setMeasuredDimension(parentWidth, parentHeight);
	}
	  
	
}
