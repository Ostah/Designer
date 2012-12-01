package pk.projektant;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FurnitureListCache {
	private View baseView;
	private TextView textName;
	private TextView textCost;
	private ImageView imageCity;

	public FurnitureListCache(View baseView) {
		this.baseView = baseView;
	}

	public View getViewBase() {
		return baseView;
	}

	public TextView getTextName(int resource) {
		if (textName == null) {
			textName = (TextView) baseView.findViewById(R.id.list_element_name);
		}
		return textName;
	}
//	public TextView getTextDescription(int resource) {
//		if (textDescription == null) {
//			textDescription = (TextView) baseView.findViewById(R.id.list_element_description);
//		}
//		return textDescription;
//	}
	public TextView getTextCost(int resource) {
		if (textCost == null) {
			textCost = (TextView) baseView.findViewById(R.id.list_element_cost);
		}
		return textCost;
	}
	public ImageView getImageView(String uri, Context context) {
		if (imageCity == null) {		
			imageCity = (ImageView) baseView.findViewById(R.id.list_element_image);
		}
		return imageCity;
	}

}
