package pk.projektant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public final class User {
	 
    // nale¿y zwróciæ uwagê na u¿ycie s³owa kluczowego volatile
    private static volatile User instance = null;
    private String id;
    private String name;
    private String surname;
    private String password;
    private String registrationDate;
    private String email;
    private String role;
    private Boolean remember;
    
    public static Boolean newFurniture=true;
    
    static SharedPreferences pref ;
    static Editor editor ;
    static Context ctx;
    
    public static User get(Context cont) {
    	ctx = cont;
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                	pref = ctx.getSharedPreferences("UserSession",0); // 0 - for private mode
                	editor = pref.edit();
                    instance = new User();
                }
            }
        }
        return instance;
    }
 
    public void set(String id, String name, String surname, String password, String registration, String email, String role, Boolean remember){
    	this.id=id;
    	this.name=name;
    	this.surname=surname;
    	this.password=password;
    	this.registrationDate=registration;
    	this.email=email;
    	this.role=registration;
    	this.remember = remember;
    	save();
    }
    
    public void save()
    {
    	editor.clear();
    	editor.putString("name", name);
    	editor.putString("surname", surname);
    	editor.putString("password", password);
    	editor.putString("email", email);
    	editor.putString("role", role);
    	editor.putString("registrationDate", registrationDate);
    	editor.putBoolean("remember", remember);
    	editor.commit();
    }
    public void load()
    {
    	
    }
    public Boolean isUserSet(){
    	return(email != null && email!="null");
    }
    // ¿eby unikn¹æ automatycznego tworzenia domyœlnego, publicznego, bezargumentowego konstruktora
    private User() {
    	
    	
    	id="";
    	name="";
    	surname="";
    	password="";
    	registrationDate="";
    	email="";
    	role="";
    	remember = false;
    	
    	
    	String Uname = pref.getString("name",null);
    	if(name != null)
    	{
    		
    	}
    	//Log.d("shared", Uname);
    	
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getRemember() {
		return remember;
	}

	public void setRemember(Boolean remember) {
		this.remember = remember;
	}
    
    
 
}