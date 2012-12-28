package pk.projektant;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class JSONProjectSimple {
	
	@SerializedName("id")
	int mId;
	
	@SerializedName("ownerId")
	String mOwnerId;
	
	@SerializedName("title")
	String mTitle;
	
	@SerializedName("projectDescription")
	String mDescription;
	
	@SerializedName("dateOfCreation")
	String mDate1;
	
	@SerializedName("dateOfLastEdit")
	String mDate2;
	
	@SerializedName("dataWalls")
	String mWalls;
	
	@SerializedName("dataObjects")
	String mFurnitures;
	
	ArrayList<FurnitureView> getFurnitures(){
		return new ArrayList<FurnitureView>();
	}
	
	JSONProjectSimple(Project p){
		mTitle = p.mName;
		mDescription = p.mDescription;
		mDate1 = "0"; //TODO
		mDate2= "0";	//TODO
		mId = p.mId;
		mWalls = wallsJSONFromFurnitures(p.mFurnitures);
		mFurnitures = furnituresJSONFromFurnitures(p.mFurnitures);
	}
	
	static String wallsJSONFromFurnitures(ArrayList<FurnitureView> f){
		return ""; //TODO
	}
	static String furnituresJSONFromFurnitures(ArrayList<FurnitureView> f){
		return ""; //TODO
	}
	
	Project toProject(){
		long d1=0,d2=0;
		try{
			d1 = Long.parseLong(mDate1);
			d2 = Long.parseLong(mDate2);	
		}
		catch (NumberFormatException e){
			d1=0;
			d2=0;
		}
		
		return new Project(mId,mTitle,mDescription,d1,d2,getFurnitures());
	}
}
