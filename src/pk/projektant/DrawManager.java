package pk.projektant;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class DrawManager extends View{
	Context ctx;
	private Paint d_normal = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_grid[];
	private Paint d_shadow = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint d_active = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint draw2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private  ArrayList<FurnitureView> sFv;
	
	public DrawManager(Context context, ArrayList<FurnitureView> furnitures) {
		super(context);
		sFv = furnitures;
		ctx = context;
		
		d_normal.setColor(0xFF99CCFF);
		d_shadow.setColor(0x80FF0000);
		d_active.setColor(0xFF00CC00);
		draw2.setColor(Color.BLACK);
		d_grid = new Paint[2];
		d_grid[0]  = new Paint(Paint.ANTI_ALIAS_FLAG);
		d_grid[1]  = new Paint(Paint.ANTI_ALIAS_FLAG);
		d_grid[0].setColor(0x20000000);
		d_grid[1].setColor(0x10000000);
	}
	@Override
	protected void onDraw(Canvas canvas){

			drawGrid(canvas);
		   for(int i=0; i<sFv.size();i++){
			   if		(sFv.get(i).isShadow) 	sFv.get(i).draw(canvas, d_shadow, draw2);
			   else if	(sFv.get(i).isMoved)	sFv.get(i).draw(canvas, d_active, draw2);
			   else 							sFv.get(i).draw(canvas, d_normal, draw2);
		   }
		   canvas.drawCircle((float) (MapManager.width*0.5), (float) (MapManager.heigth*0.5), 3,d_shadow);
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
