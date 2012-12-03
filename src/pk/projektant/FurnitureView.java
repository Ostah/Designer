package pk.projektant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint({ "ViewConstructor", "UseValueOf" })
public class FurnitureView extends View {

	
	private Rect rect;
	private int color;
	private Furniture reference;
	static private Context ctx;
	static private float scale = 0.5f;
	
	private Paint draw = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint draw2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public FurnitureView(Context context, float x, float y, float dx, float dy, int color){
		super(context);
		ctx = context;  
        this.color=color;
        draw.setColor(color);
        draw2.setColor(Color.BLACK);
        rect = new Rect((int)x,(int)y,(int)(x+dx),(int)(y+dy));
        
        reference = null;

    }
	
	public FurnitureView(Context context,float x, float y, Furniture reference){
        super(context);
        ctx = context;  
        this.reference = reference;
        this.color=0xFF99CCFF;
        draw.setColor(color);
        draw2.setColor(Color.BLACK);
        rect = new Rect((int)x,(int)y,(int)(x+this.reference.mX),(int)(y+this.reference.mY));

       
    }
	public String getString()
	{
		String s="";
		s="x:"+String.valueOf(rect.left)+" y: "+String.valueOf(rect.top) ;
		s += " "+String.valueOf(rect.right-rect.left)+"x"+String.valueOf(rect.bottom-rect.top)+"\n" ;
		return s;
	}
	public FurnitureView(Context context, float x, float y, float dx, float dy){
        super(context);
        ctx = context;  
        this.color=0xFF99CCFF;
        draw.setColor(color);
        draw2.setColor(Color.BLACK);
        rect = new Rect((int)x,(int)y,(int)(x+dx),(int)(y+dy));
        reference = null;
    }
	
	  
	public FurnitureView(FurnitureView f) {
		super(ctx);
		
		this.reference = f.reference;
		this.color = new Integer(f.color);
		this.rect = new Rect(f.rect);
		this.reference = f.reference;
		draw = new Paint(Paint.ANTI_ALIAS_FLAG);
		
	}

	protected void onDraw(Canvas canvas){
	        super.onDraw(canvas);
	        canvas.drawRect(rect, draw);
	        
	        canvas.drawCircle(rect.left, rect.top, 1, draw2);
	        canvas.drawCircle(rect.left, rect.bottom,1, draw2);
	        canvas.drawCircle(rect.right, rect.top, 1, draw2);
	        canvas.drawCircle(rect.right, rect.bottom, 1, draw2);
	}
	
	protected void move(float x, float y){
	       rect.offsetTo((int)(x/*-0.5*rect.width()*/), (int)(y/*-0.5*rect.height()*/));
	}
	
	public Rect getRect(){
		return new Rect(rect);
	}
	
	public void setColor(int c){
		color = c;
		draw.setColor(color);
	}
	
	public FurnitureView getShadow()
	{
		FurnitureView f = new FurnitureView(this);
		f.setColor(0x80FF0000);
		return f;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		   int desiredWidth = rect.width();
		    int desiredHeight = rect.height();

		    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		    int width;
		    int height;

		    //Measure Width
		    if (widthMode == MeasureSpec.EXACTLY) {
		        //Must be this size
		        width = widthSize;
		    } else if (widthMode == MeasureSpec.AT_MOST) {
		        //Can't be bigger than...
		        width = Math.min(desiredWidth, widthSize);
		    } else {
		        //Be whatever you want
		        width = desiredWidth;
		    }

		    //Measure Height
		    if (heightMode == MeasureSpec.EXACTLY) {
		        //Must be this size
		        height = heightSize;
		    } else if (heightMode == MeasureSpec.AT_MOST) {
		        //Can't be bigger than...
		        height = Math.min(desiredHeight, heightSize);
		    } else {
		        //Be whatever you want
		        height = desiredHeight;
		    }

		    //MUST CALL THIS
		    Log.d("MEASURE", String.valueOf(width)+"x" +String.valueOf(height));
		    setMeasuredDimension(width, height);
	}
	
	
}
