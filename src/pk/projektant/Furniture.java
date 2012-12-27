package pk.projektant;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Furniture {

	public
		
	String 	mId="";
	String 	mName="";
	String 	mDescription="Mebel Stworzony Przez U¿ytkownika";
	String 	mCathegory="";
	String 	mRoom="";
	String 	mImageLink="";
	float	mPrice=0;
	float 	mX=0;
	float 	mY=0;
	float	mZ=0;	
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
		mX = x;
		mY = y;
		mZ = z;
	 	if(cathegory.equalsIgnoreCase("Ló¿ka")) color = 1;
	 	else if(cathegory.equalsIgnoreCase("Szafy")) color = 2;
	 	else if(cathegory.equalsIgnoreCase("Komody")) color = 3;
	 	else if(cathegory.equalsIgnoreCase("Agd")) color = 4;
	 	else if(cathegory.equalsIgnoreCase("Sto³y")) color = 5;
	 	else if(cathegory.equalsIgnoreCase("Sofy")) color = 6;
	 	else if(cathegory.equalsIgnoreCase("Krzes³a")) color = 7;
	 	
	}
	
	Furniture(String name, float cost, int x, int y)
	{
		mName = name;
		mPrice = cost;
		mX=x;
		mY=y;
		
	}
	@Override
	public String toString(){
		return mName;
	}
	
}
