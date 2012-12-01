package pk.projektant;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FurnitureListAdapter extends BaseAdapter{
	
	private int resource;
	private Context context;
	private LayoutInflater inflater;
	public FurnitureListAdapter(Context ctx, int textViewResourceId, List<Furniture> objects) {
		super();
		 
		resource = textViewResourceId;
        inflater = LayoutInflater.from( ctx );
        context=ctx;
	}
	
	public View getView ( int position, View convertView, ViewGroup parent ) {
		
		
		Furniture furniture = Tokenizer.sFurnitures.get(position);
		FurnitureListCache viewCache;
		
		if (convertView == null) {
		//Log.e("VIEW", String.valueOf(position)+ " NULL");
			convertView = (RelativeLayout) inflater.inflate(resource, null);
			viewCache = new FurnitureListCache(convertView);
			convertView.setTag(viewCache);
		} else {
			//Log.e("VIEW",String.valueOf(position)+ " OK");
			convertView = (RelativeLayout) convertView;
			viewCache = (FurnitureListCache) convertView.getTag();
		}
		
		TextView txtName = viewCache.getTextName(resource);
		txtName.setText(furniture.mName);	
		TextView txtCost = viewCache.getTextCost(resource);
		txtCost.setText(Float.toString(furniture.mPrice));		 
		ImageView imageCity =  viewCache.getImageView(furniture.mId, context);
		
		int imageResource = context.getResources().getIdentifier(furniture.mId, "drawable", context.getPackageName());
		if(imageResource!=0)
		{
			Drawable image = context.getResources().getDrawable(imageResource);
	        imageCity.setImageDrawable(image);	
		}


		
		return convertView;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}
