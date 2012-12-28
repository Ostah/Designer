package pk.projektant;

import java.util.ArrayList;

public class Project {
	String mName;
	String mDescription="";
	int mId=0;
	long mDateCreation=0;
	long mDateUpdate=0;
	
	ArrayList<FurnitureView> mFurnitures;
	
	Project(String name, long date, ArrayList<FurnitureView> f){
		mName=name;
		mDateCreation=date;
		mFurnitures=f;
		
	}
	
	Project(int id,String name, String description, long dateCreation, long dateUpdate, ArrayList<FurnitureView> f){
		mId=id;
		mName=name;
		mDescription=description;
		mDateCreation=dateCreation;
		mDateUpdate=dateUpdate;
		mFurnitures=f;
		
	}
	
	Project(String name, long date){
		mName=name;
		mDateCreation=date;
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
