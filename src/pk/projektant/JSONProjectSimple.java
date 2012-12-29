package pk.projektant;
import java.util.ArrayList;

import com.google.gson.Gson;
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
		
		ArrayList<FurnitureView> toReturn = new ArrayList<FurnitureView>();
		if(mFurnitures.isEmpty()) return toReturn;
	
		Gson gson = new Gson();
		JSONFurniture[] furnitures = gson.fromJson(mFurnitures, JSONFurniture[].class);
		JSONWall[] walls = gson.fromJson(mWalls, JSONWall[].class);
		 
		toReturn.ensureCapacity(furnitures.length+walls.length);
		for(int i=0;i<furnitures.length;i++){
			toReturn.add(new FurnitureView(furnitures[i]));
		}
		for(int i=0;i<walls.length;i++){
			toReturn.add(new FurnitureView(walls[i]));
		}


		return toReturn;
	}
	
	JSONProjectSimple(Project p){
		mTitle = p.mName;
		mDescription = p.mDescription;
		
		mDate1 = String.valueOf(p.mDateCreation);
		mDate2=  String.valueOf(p.mDateUpdate);
		mId = p.mId;
		mWalls = wallsJSONFromFurnitures(p.mFurnitures);
		mFurnitures = furnituresJSONFromFurnitures(p.mFurnitures);
	}
	
	static String furnituresJSONFromFurnitures(ArrayList<FurnitureView> f)
	{
		ArrayList<JSONFurniture> furnitures = new ArrayList<JSONFurniture>();
		for(int i=0;i<f.size();i++){
			if(f.get(i).isWall) continue;
			furnitures.add(new JSONFurniture(f.get(i)));
		}
		Gson gson = new Gson();
		String str =  gson.toJson(furnitures);
		return str;
	}
	static String  wallsJSONFromFurnitures(ArrayList<FurnitureView> f)
	{
		ArrayList<JSONWall> walls = new ArrayList<JSONWall>();
		for(int i=0;i<f.size();i++){
			if(!f.get(i).isWall) continue;
			walls.add(new JSONWall(f.get(i)));
		}
		Gson gson = new Gson();
		String str =   gson.toJson(walls);
		return str;
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
