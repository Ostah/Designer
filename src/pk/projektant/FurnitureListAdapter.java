package pk.projektant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FurnitureListAdapter extends ArrayAdapter<Furniture>{
	
	private int resource;
	private Context context;
	private LayoutInflater inflater;
	public List<Furniture> data;
	private List<Furniture> original;
	private List<Furniture> filtered;
	public Boolean notifyChanged=false;
	SharedPreferences sharedPrefs=null;
	public FurnitureListAdapter(Context ctx, int textViewResourceId, List<Furniture> objects) {
		super(ctx, textViewResourceId,objects);
		 
		resource = textViewResourceId;
        inflater = LayoutInflater.from( ctx );
        context=ctx;
        data = objects;
        
        original = new ArrayList<Furniture>(objects);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);

	}
	
	public View getView ( int position, View convertView, ViewGroup parent ) {
		
		
		Furniture furniture = data.get(position);
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
	
		
		if(sharedPrefs.getBoolean("show_furnitures", true))
		{
			ImageView imageCity =  viewCache.getImageView(furniture.mId, context);
			int imageResource = context.getResources().getIdentifier(furniture.mId, "drawable", context.getPackageName());
			if(imageResource!=0)
			{
				Drawable image = context.getResources().getDrawable(imageResource);
		        imageCity.setImageDrawable(image);	
			}

		}
		else{
			Log.d("preferences", "false");
		}
		

		
		return convertView;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Furniture getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void filter(String a){
		filtered = new ArrayList<Furniture>();
		for(int i=0;i<original.size();i++){
			if(original.get(i).mName.toLowerCase().contains(a.toLowerCase())){
				filtered.add(original.get(i));
			}
		}
		data = filtered;
	      Log.d("FILTER SIZE", String.valueOf(filtered.size()));
		this.notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
	    super.notifyDataSetChanged();
	    notifyChanged = true;
	}

}
