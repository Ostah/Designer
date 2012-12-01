package pk.projektant;

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
	}
	
}
