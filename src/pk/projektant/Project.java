package pk.projektant;

import java.util.ArrayList;

public class Project {
	String mName;
	String mId;
	long mDate;
	ArrayList<FurnitureView> mFurnitures;
	
	Project(String name, long date, ArrayList<FurnitureView> f){
		mName=name;
		mDate=date;
		mFurnitures=f;
		
	}
	
	Project(String name, long date){
		mName=name;
		mDate=date;
		mFurnitures=new ArrayList<FurnitureView>() ;
		
	}
	
	public String toString(){
		return mName;
	}
	public int getCost()
	{
		int c = 0;
		for(int i=0;i<mFurnitures.size();i++){
			if(mFurnitures.get(i).reference != null) c+=mFurnitures.get(i).reference.mPrice;
		}
		return c;
	}
	
}
