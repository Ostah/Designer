package pk.projektant;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Furniture {

	public
		
	String 	mId;
	String 	mName;
	String 	mDescription;
	String 	mCathegory;
	String 	mRoom;
	String 	mImageLink;
	float	mPrice;
	float 	mX;
	float 	mY;
	float	mZ;	
	int color=0;
	Furniture(String id, String name, String description, String cathegory, String room, String image, float price, float x, float y, float z)
	{
		mId = id;
		mName = name;
		mDescription = description;
		mCathegory = cathegory;
		mRoom = room;
		mImageLink = image;
		mPrice = price;
		mX = 1.5f*x;
		mY = 1.5f*y;
		mZ = z;
	 	if(cathegory.equalsIgnoreCase("Ló¿ka")) color = 1;
	 	else if(cathegory.equalsIgnoreCase("Szafy")) color = 2;
	 	else if(cathegory.equalsIgnoreCase("Komody")) color = 3;
	 	else if(cathegory.equalsIgnoreCase("Agd")) color = 4;
	 	else if(cathegory.equalsIgnoreCase("Sto³y")) color = 5;
	 	else if(cathegory.equalsIgnoreCase("Sofy")) color = 6;
	 	else if(cathegory.equalsIgnoreCase("Krzes³a")) color = 7;
	 	
	}
	
}
