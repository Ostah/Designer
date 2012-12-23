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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MapManager {

	public FrameLayout mMapArea;
	private FurnitureView mFurnitureShadow;
	private TextView mInfoText;
	private FurnitureView mFurnitureActive;

	private ArrayList<FurnitureView> sFv;
	static private Context ctx;
	private DrawManager drawManager;
	private Activity act;
	private float yInit = 0.0f;
	private float xInit = 0.0f;

	private Boolean isScaling = false;
	public Boolean isWallDrawning = false;

	private float xDown = 0.0f;
	private float yDown = 0.0f;
	ScaleGestureDetector scaleGestureDetector;
	public static float mScale = 1.0f;
	public static int mOffsetXBefore = 0;
	public static int mOffsetYBefore = 0;

	public static int mOffsetX = 0;
	public static int mOffsetY = 0;

	public static int onScreenX = 0;
	public static int onScreenY = 0;
	public static int width = 0;
	public static int heigth = 0;

	// dwuklik---------------------
	private long tapStart = 0;
	private long tapDuration = 0;
	private int tapCount = 0;
	private FurnitureView tapFurniture = null;

	// ----------------------------

	public static int tPX(int p) {
		return new Integer((int) ((p - mOffsetX) / mScale));
	}

	public static int tPY(int p) {
		return new Integer((int) ((p - mOffsetY) / mScale));
	}

	MapManager(FrameLayout mapArea, Context context, Activity a) {
		sFv = new ArrayList<FurnitureView>();
		act = a;
		this.mMapArea = mapArea;
		scaleGestureDetector = new ScaleGestureDetector(context,
				new simpleOnScaleGestureListener());
		drawManager = new DrawManager(context, sFv);
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
					tapStart = System.currentTimeMillis();
					tapCount++;
					invalidate();
					mOffsetXBefore = mOffsetX;
					mOffsetYBefore = mOffsetY;
					xInit = event.getX();
					yInit = event.getY();
				
				}

				// DWUKLIK
				// --------------------------------------------------------------------
				if (event.getAction() == MotionEvent.ACTION_UP) {
			
					long time = System.currentTimeMillis() - tapStart;
					tapDuration = tapDuration + time;
					if (tapCount == 2) {
					
						if (time <= 90) {
							tapFurniture = findFurniture((int) xInit,
									(int) yInit);
							if (tapFurniture != null){
								final CharSequence[] items = { "Informacje",
										"Obróæ", "Usuñ" };

								AlertDialog.Builder builder = new AlertDialog.Builder(
										ctx);
								builder.setTitle("Wybierz opcjê");
								builder.setItems(items,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int item) {
												if (item == 0) {
													showProperties(tapFurniture.reference);
												} else if (item == 1) {
													rotate(tapFurniture);
												} else if (item == 2) {
													sFv.remove(tapFurniture);
													invalidate();
												}

											}
										});
								AlertDialog alert = builder.create();
								alert.show();
							}
						}
						tapCount = 0;
						tapDuration = 0;
						return false;
					}
				}
				// ---------------------------------------------------------------------------------------

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					mOffsetX = mOffsetXBefore - (int) ((xInit - event.getX()));
					mOffsetY = mOffsetYBefore - (int) ((yInit - event.getY()));
					invalidate();
				}

				return false;
			}

		});

		mapArea.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View view) {
				if(isScaling) return false;
				if (isWallDrawning) // sciana
				{
					Vibrator vibe = (Vibrator) act
							.getSystemService(Activity.VIBRATOR_SERVICE);
					vibe.vibrate(100);
					User.dragType = "wall";
					ClipData data = ClipData.newPlainText("type", "map");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
					view.startDrag(data, shadowBuilder, null, 0);

				} else // niesciana
				{
					// znajdz prostok¹t
					FurnitureView f = findFurniture((int) xInit, (int) yInit);
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
		mInfoText.setText("X: " + String.valueOf(mOffsetX) + " Y: "
				+ String.valueOf(mOffsetY));
	}

	public void centerView() {
		mScale = 1.0f;
		mOffsetX = 0;
		mOffsetY = 0;
		invalidate();

	}

	static void showProperties(Furniture f) {

		Dialog dialog = new Dialog(ctx);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.l_view_properties);

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
			if (pInRect(sFv.get(i).getRect(true), x, y))
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

	static public Boolean pInRect(Rect r, int x, int y) {
		if (x >= r.left && x <= r.right && y >= r.top && y <= r.bottom) {
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
		if (isWallDrawning && User.dragType == "wall") {
			valid = isRectangleValid(f, f.getResizedRect(x, y));
		} else {
			valid = isMoveValid(f, x, y);
		}
		if (mFurnitureShadow == null) {
			if (valid) {
				if (isWallDrawning && User.dragType == "wall")
					f.resize(x, y);
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

				if (isWallDrawning && User.dragType == "wall")
					mFurnitureShadow.resize(x, y);
				else
					mFurnitureShadow.move(x, y, true);
				invalidate();
			} else {
				Log.d("VALID!", "VALID!");
				invalidate();
				sFv.remove(mFurnitureShadow);
				invalidate();
				mFurnitureShadow = null;

				if (isWallDrawning && User.dragType == "wall")
					f.resize(x, y);
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
		drop();
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

	public void drop() {
		if (mFurnitureShadow != null) {
			sFv.remove(mFurnitureShadow);
			mFurnitureShadow = null;

		}
		if (mFurnitureActive != null) {
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

			float scaleChange = mScale;

			mScale = mStartScale * factor;
			scaleChange = mScale - scaleChange;

			mOffsetX -= (int) ((int) (mMapArea.getWidth() * 0.5) * scaleChange);
			mOffsetY -= (int) ((int) (mMapArea.getHeight() * 0.5) * scaleChange);
			invalidate();

			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mStartScale = mScale;
			isScaling = true;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			isScaling = false;
		}

	}

}
