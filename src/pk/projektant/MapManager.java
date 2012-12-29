package pk.projektant;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MapManager {

	public FrameLayout mMapArea;
	private FurnitureView mFurnitureShadow;
	private TextView mInfoText;
	private FurnitureView mFurnitureActive;

	private EditText mNewCost, mNewWidth, mNewHeight,mNewName,mWallWidth;
	private Button mNewOk,mWallOk;
	private Dialog mDialog;


	
	public ArrayList<FurnitureView> sFv;
	static private Context ctx;
	public static DrawManager drawManager;
	private ActivityDesigner act;
	private float yInit = 0.0f;
	private float xInit = 0.0f;
	private float yAct = 0.0f;
	private float xAct = 0.0f;

	private Boolean isScaling = false;
	public Boolean isCustomDrawning = false;
	public Boolean isWallDrawning = false;

	ScaleGestureDetector scaleGestureDetector;
	
	public static int mOffsetXBefore = 0;
	public static int mOffsetYBefore = 0;
	
	

	public static int onScreenX = 0;
	public static int onScreenY = 0;
	public static int width = 600;
	public static int heigth = 300;

	// dwuklik---------------------
	private long tapStart = 0;
	private long tapDuration = 0;
	private int tapCount = 0;
	private FurnitureView tapFurniture = null;

	// ----------------------------

	public static int tPX(int p) {
		return Integer.valueOf((int) ((p - drawManager.mOffsetX) / drawManager.mScale));
	}

	public static int tPY(int p) {
		return Integer.valueOf((int) ((p - drawManager.mOffsetY) / drawManager.mScale));
	}

	MapManager(FrameLayout mapArea, Context context, ActivityDesigner a,ArrayList<FurnitureView> l) {
		sFv = new ArrayList<FurnitureView>(l.size());
		for(int i=0;i<l.size();i++) sFv.add(l.get(i).clone());

		act = a;
		this.mMapArea = mapArea;
		scaleGestureDetector = new ScaleGestureDetector(context,
				new simpleOnScaleGestureListener());
		drawManager = new DrawManager(context, sFv,1.0f);
		mapArea.addView(drawManager);
		mInfoText = (TextView) act.findViewById(R.id.info_text);
		invalidate();
		mFurnitureShadow = null;
		ctx = context;

		mapArea.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				width = mMapArea.getWidth();
				heigth = mMapArea.getHeight();
				scaleGestureDetector.onTouchEvent(event);
				if (isScaling) {
				
					return false;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.d("click", "down");
					
					long time = System.currentTimeMillis() - tapStart;
					Log.d("click", "Time: "+String.valueOf(time));
					if(time>1500){
					
						tapCount=0;
					}
					
					tapStart = System.currentTimeMillis();
					tapCount++;
					invalidate();
					mOffsetXBefore = drawManager.mOffsetX;
					mOffsetYBefore = drawManager.mOffsetY;
					xInit = event.getX();
					yInit = event.getY();
					xAct= event.getX();
					yAct=event.getY();
				}

				// DWUKLIK
				// --------------------------------------------------------------------
				if (event.getAction() == MotionEvent.ACTION_UP) {
					
					long time = System.currentTimeMillis() - tapStart;
					tapDuration = tapDuration + time;
					Log.d("click", "up "+String.valueOf(time)+" count:"+String.valueOf(tapCount));
					if (tapCount == 2) {
					
						if (time <= 130) {
							Log.d("click", "double!");
							tapFurniture = findFurniture((int) xInit,
									(int) yInit);
							if (tapFurniture != null){
								Log.d("click", "double in!");
							 CharSequence[] items = {
										"Obróæ", "Usuñ" , "Informacje"};
									
								if(tapFurniture.isWall) items = new CharSequence[] {"Obróæ", "Usuñ" };
								AlertDialog.Builder builder = new AlertDialog.Builder(
										ctx);
								builder.setTitle("Wybierz opcjê");
								builder.setItems(items,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int item) {
												if (item == 3) {
													showProperties(tapFurniture.reference);
												} else if (item == 0) {
													rotate(tapFurniture);
												} else if (item ==1) {
													sFv.remove(tapFurniture);
													invalidate();
												}

											}
										});
								AlertDialog alert = builder.create();
								alert.show();
							}
						}
						
						tapDuration = 0;
						return false;
					}
					if(tapCount>1) tapCount=0;
				}
				// ---------------------------------------------------------------------------------------

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					drawManager.mOffsetX = mOffsetXBefore - (int) ((xInit - event.getX()));
					drawManager.mOffsetY = mOffsetYBefore - (int) ((yInit - event.getY()));
					xAct= event.getX();
					yAct=event.getY();
					invalidate();
				}

				return false;
			}

		});

		mapArea.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View view) {
				int diff = (int) (Math.abs(xAct-xInit) + Math.abs(yAct-yInit));
				if(isScaling||diff>30) return false;
				FurnitureView f = findFurniture((int) xInit, (int) yInit);
				if (isCustomDrawning||isWallDrawning&&f==null) // mebel customowy
				{
					Vibrator vibe = (Vibrator) act
							.getSystemService(Activity.VIBRATOR_SERVICE);
					vibe.vibrate(100);
					if(isCustomDrawning) User.dragType = "custom";
					else User.dragType = "wall";
					ClipData data = ClipData.newPlainText("type", "map");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
					view.startDrag(data, shadowBuilder, null, 0);

				} 
				else // niesciana
				{
					// znajdz prostok¹t
					
					if (f == null)
						Log.d("CLICK", "null");
					else {
						User.dragType = "move";
						ClipData data = ClipData.newPlainText("type", "map");
						DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
						view.startDrag(data, shadowBuilder, f, 0);
						mFurnitureActive = f;
						f.isMoved = true;
						Vibrator vibe = (Vibrator) act
								.getSystemService(Activity.VIBRATOR_SERVICE);
						vibe.vibrate(100);
					}
				}

				return false;
			}
		});

	}

	public void invalidate() {
		drawManager.invalidate();
		mInfoText.setText("X: " + String.valueOf(drawManager.mOffsetX) + " Y: "
				+ String.valueOf(drawManager.mOffsetY));
	}

	public void centerView() {
		drawManager.mScale = 1.0f;
		drawManager.mOffsetX = 0;
		drawManager.mOffsetY = 0;
		invalidate();

	}

	static void showProperties(Furniture f) {

		Dialog dialog = new Dialog(ctx);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.l_view_dialog_properties);

		TextView text = (TextView) dialog.findViewById(R.id.properties_text);
		text.setMovementMethod(new ScrollingMovementMethod());
		text.setText(f.mDescription);

		TextView cost = (TextView) dialog.findViewById(R.id.properties_cost);
		cost.setText(String.valueOf(f.mPrice));

		TextView room = (TextView) dialog.findViewById(R.id.properties_room);
		room.setText(String.valueOf(f.mRoom));

		TextView type = (TextView) dialog.findViewById(R.id.properties_type);
		type.setText(String.valueOf(f.mCathegory));

		TextView title = (TextView) dialog.findViewById(R.id.properties_title);
		title.setText(String.valueOf(f.mName));

		ImageView imageControll = (ImageView) dialog
				.findViewById(R.id.properties_image);

		int imageResource = ctx.getResources().getIdentifier(f.mId, "drawable",
				ctx.getPackageName());
		if (imageResource != 0) {
			Drawable image = ctx.getResources().getDrawable(imageResource);
			imageControll.setImageDrawable(image);
		}

		dialog.show();

	}

	private FurnitureView findFurniture(int x, int y) {
		for (int i = 0; i < sFv.size(); i++) {
			if (pInRect(sFv.get(i).getRect(true), x, y,sFv.get(i).isWall))
				return sFv.get(i);
		}
		return null;
	}

	private void rotate(FurnitureView f) {
		if (isRectangleValid(f, f.getRotatedRect())) {
			f.rotate();
			invalidate();
		} else {
			Toast.makeText(ctx, "Brak miejsca do obrotu", Toast.LENGTH_SHORT).show();
		}
	}

	static public Boolean pInRect(Rect r, int x, int y, Boolean wall) {
		int n = wall ? 5 : 0;
		if (x >= r.left-n && x <= r.right+n && y >= r.top-n && y <= r.bottom+n) {
			return true;
		}
		return false;
	}

	public void addFurniture(FurnitureView f) {
		sFv.add(f);
		f.isMoved = true;
		mFurnitureActive = f;

	}

	public void moveFurniture(FurnitureView f, float x, float y) {
		// x-=f.getWidth()*mScale*0.5f;
		// y-=f.getHeigth()*mScale*0.5f;

		x = tPX((int) x);
		y = tPY((int) y);

		mMapArea.invalidate();

		Boolean valid = true;
		if ((isCustomDrawning && User.dragType == "custom")) {
			valid = isRectangleValid(f, f.getResizedRect(x, y,false));
		}
		else if(isWallDrawning && User.dragType == "wall"){
			valid = isRectangleValid(f, f.getResizedRect(x, y,true));
		}
		else {
			x-= 0.5*f.getWidth();
			y-=0.5*f.getHeigth();
			valid = isMoveValid(f, x, y);
		}
		if (mFurnitureShadow == null) {
			if (valid) {
				if (isCustomDrawning && User.dragType == "custom")
					f.resize(x, y,false);
				else if (isWallDrawning && User.dragType == "wall")
					f.resize(x, y,true);
				else
					f.move(x, y, true);
				invalidate();
			} else {
				Log.d("INVALID!", "INVALID!");
				mFurnitureShadow = f.getShadow();
				sFv.add(mFurnitureShadow);
			}
		} else {
			if (!valid) {

				if (isCustomDrawning && User.dragType == "custom")
					mFurnitureShadow.resize(x, y,false);
				else if (isWallDrawning && User.dragType == "wall")
					mFurnitureShadow.resize(x, y,true);
				else
					mFurnitureShadow.move(x, y, true);
				invalidate();
			} else {
				Log.d("VALID!", "VALID!");
				invalidate();
				sFv.remove(mFurnitureShadow);
				invalidate();
				mFurnitureShadow = null;

				if (isCustomDrawning && User.dragType == "custom")
					f.resize(x, y,false);
				else if (isWallDrawning && User.dragType == "wall")
					f.resize(x, y,true);
				else
					f.move(x, y, true);

				invalidate();
			}
		}
	}

	private Boolean isMoveValid(FurnitureView f, float x, float y) {
		Rect newPosition = new Rect(f.getRect(false));
		newPosition.offsetTo((int) x, (int) y);
		return isRectangleValid(f, newPosition);
	}

	public void removeView(FurnitureView f) {
		drop(f);
		sFv.remove(f);
		invalidate();
	}

	private Boolean isRectangleValid(FurnitureView f, Rect newPosition) {
		for (int i = 0; i < sFv.size(); i++) {

			if (sFv.get(i).equals(f)
					|| (mFurnitureShadow != null && sFv.get(i).equals(
							mFurnitureShadow)))
				continue;
			if (((FurnitureView) sFv.get(i)).getRect(false).intersect(
					newPosition)) {
				return false;
			}
		}
		return true;
	}

	public void dialogCustom(FurnitureView f) {
		mDialog = new Dialog(ctx);
		mDialog.setContentView(R.layout.l_view_dialog_furniture);
		mDialog.setTitle("Mebel Niestandardowy");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		
		mNewCost = (EditText)mDialog.findViewById(R.id.new_cost);
		mNewWidth = (EditText)mDialog.findViewById(R.id.new_width);
		mNewHeight = (EditText)mDialog.findViewById(R.id.new_heigth);
		mNewOk = (Button)mDialog.findViewById(R.id.new_ok);
		mNewName= (EditText)mDialog.findViewById(R.id.new_name);
		mFurnitureActive = f;
	
		mNewWidth.setText(String.valueOf(f.getWidth()));
		mNewHeight.setText(String.valueOf(f.getHeigth()));
		mDialog.show();
		
		mNewOk.setOnClickListener(new View.OnClickListener() {		
	    	   public void onClick(View arg0) {
	    		  
	    		  	if(mNewWidth.getText().length()==0 || mNewHeight.getText().length()==0 || mNewName.getText().length()==0  || mNewCost.getText().length()==0  ){
	    		  		Toast.makeText(ctx, "Pozostawiono Puste Pola", Toast.LENGTH_SHORT).show();
	    		  		return;
	    		  	}
	    		  	int width = Integer.valueOf(mNewWidth.getText().toString());
	    		  	int height = Integer.valueOf(mNewHeight.getText().toString());
	    		  	int cost = Integer.valueOf(mNewCost.getText().toString());
	    		  	if(width<1 || height<1){
	    		  		Toast.makeText(ctx, "Rozmiar nie mo¿e byæ mniejszy od 1", Toast.LENGTH_SHORT).show();
	    		  		return;
	    		  	}
	    		  	Rect tmp = mFurnitureActive.getRect(false);
	    		  	tmp.right = tmp.left+width;
	    		  	tmp.bottom = tmp.top+height;
	    		  	if(!isRectangleValid(mFurnitureActive, tmp)){
	    		  		Toast.makeText(ctx, "Nieprawid³owy rozmiar (kolizja z innym meblem)", Toast.LENGTH_LONG).show();
	    		  		return;
	    		  	}
	    		  	mFurnitureActive.setRect(tmp);
	    		  	mFurnitureActive.reference = new Furniture(mNewName.getText().toString(),Float.valueOf(cost),width,height);
	    			Toast.makeText(ctx, "ok", Toast.LENGTH_LONG).show();
	    			mFurnitureActive.isMoved = false;
	    			mFurnitureActive = null;
	    			mDialog.dismiss();
	    			invalidate();
	    	   }
	    	});
	}
	
	public void dialogWall(FurnitureView f) {
		mDialog = new Dialog(ctx);
		mDialog.setContentView(R.layout.l_view_dialog_wall);
		mDialog.setTitle("Nowa Œciana");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		
		mWallWidth = (EditText)mDialog.findViewById(R.id.wall_length);	
		mWallOk = (Button)mDialog.findViewById(R.id.new_wall_ok);
		
		mFurnitureActive = f;
		Boolean horizontal = false;
		if(f.getWidth()>f.getHeigth())horizontal =true; 
		if(horizontal){
			mWallWidth.setText(String.valueOf(f.getWidth()));	
		}
		else{
			mWallWidth.setText(String.valueOf(f.getHeigth()));	
		}
		
	
		mDialog.show();
		
		mWallOk.setOnClickListener(new View.OnClickListener() {		
	    	   public void onClick(View arg0) {
	    		  
	    		  	if(mWallWidth.getText().length()==0  ){
	    		  		Toast.makeText(ctx, "Nie wpisano d³ugoœci", Toast.LENGTH_SHORT).show();
	    		  		return;
	    		  	}
	    		  	int width = Integer.valueOf(mWallWidth.getText().toString());
	    		  	
	    		  	if(width<1){
	    		  		Toast.makeText(ctx, "Dlugoœæ nie mo¿e byæ mniejsza od 1", Toast.LENGTH_SHORT).show();
	    		  		return;
	    		  	}
	    		  	
	    		  	Boolean horizontal = false;
	    			if(mFurnitureActive.getWidth()>mFurnitureActive.getHeigth())horizontal =true; 
	    			
	    		  	Rect tmp = mFurnitureActive.getRect(false);
	    		  	if(horizontal) tmp.right = tmp.left+width;
	    		  	else tmp.bottom = tmp.top+width;
	    		  	if(!isRectangleValid(mFurnitureActive, tmp)){
	    		  		Toast.makeText(ctx, "Nieprawid³owy rozmiar (kolizja z innym meblem)", Toast.LENGTH_LONG).show();
	    		  		return;
	    		  	}
	    		  	mFurnitureActive.setRect(tmp);
	    		  	mFurnitureActive.reference = null;
	    		  	mFurnitureActive.isWall=true;
	    			mFurnitureActive.isMoved = false;
	    			mFurnitureActive = null;
	    			act.hideKeyboard((View)mWallOk);
	    			mDialog.dismiss();
	    			invalidate();
	    	   }
	    	});
	}
	
	public void drop(FurnitureView f) {
		
		
		if (mFurnitureShadow != null) {
			sFv.remove(mFurnitureShadow);
			mFurnitureShadow = null;

		}
		
		if(User.dragType == "custom"){
			dialogCustom(f);
		}
		else if(User.dragType == "wall"){
			dialogWall(f);
		}	
		else if (mFurnitureActive != null) {
			mFurnitureActive.isMoved = false;
			mFurnitureActive = null;
		}
		
		invalidate();
	}

	public void clear() {
		mFurnitureShadow = null;
		sFv.clear();
		invalidate();
	}

	public class simpleOnScaleGestureListener extends
			SimpleOnScaleGestureListener {
		float mStartScale = 1.0f;

		public boolean onScale(ScaleGestureDetector detector) {

			float factor = detector.getScaleFactor();
			if (mStartScale * factor < 0.2 || mStartScale * factor > 5)
				return false;

			float scaleChange = drawManager.mScale;

			drawManager.mScale = mStartScale * factor;
			scaleChange = drawManager.mScale - scaleChange;

			drawManager.mOffsetX -= (int) ((int) (mMapArea.getWidth() * 0.5) * scaleChange);
			drawManager.mOffsetY -= (int) ((int) (mMapArea.getHeight() * 0.5) * scaleChange);
			invalidate();

			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mStartScale = drawManager.mScale;
			isScaling = true;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			isScaling = false;
		}

	}

}
