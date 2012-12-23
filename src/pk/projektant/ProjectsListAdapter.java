package pk.projektant;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProjectsListAdapter extends BaseAdapter{
	ArrayList<Project> mData;
	Context ctx;
	TextView tekst_w_layoucie = null ;
	
	public ProjectsListAdapter(Context c,ArrayList<Project> d ){
		ctx = c;
		mData = d;
		
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mData.get(arg0);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			 LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   convertView = inflater.inflate(R.layout.l_view_projects_element, parent, false);	
	    }
		     
		   tekst_w_layoucie = (TextView) convertView.findViewById(R.id.list_projects_name);
		   tekst_w_layoucie.setText(mData.get(position).mName);
		    return convertView;
	}

}
