package pk.projektant;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public final class User {
	 
    // nale¿y zwróciæ uwagê na u¿ycie s³owa kluczowego volatile
    private static volatile User instance = null;
    private static String id;
    private static String name;
    private static String surname;
    private static String password;
    private static String registrationDate;
    private static String email;
    private static String role;
    private static Boolean remember=false;
    
    public static String dragType="";
    
    ArrayList<Project> mProjects;
    Project mActiveProject;
    
    static SharedPreferences pref ;
    static Editor editor ;
    static Context ctx;
    
    public static User get(Context cont) {
    	ctx = cont;
    	Log.d("omg", "tralala");
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                	pref = ctx.getSharedPreferences("UserSession",0); // 0 - for private mode
                	editor = pref.edit();
                    instance = new User();
                }
            }
        }
        if(!isUserSet()){
        	load();
        }
        return instance;
    }
 
    public void set(String id, String name, String surname, String password, String registration, String email, String role, Boolean remember){
    	User.id=id;
    	User.name=name;
    	User.surname=surname;
    	User.password=password;
    	User.registrationDate=registration;
    	User.email=email;
    	User.role=role;
    	User.remember = remember;
    	save();
    }
    
    
    static public void clear()
    {
    	id="";
    	name="";
    	surname="";
    	password="";
    	registrationDate="";
    	email="";
    	role="";
    	remember = false;
    	editor.clear();
    	editor.commit();
    }
    
    static public void save()
    {
    	if(editor==null) return;
    	editor.clear();
    	editor.putString("id", id);
    	editor.putString("name", name);
    	editor.putString("surname", surname);
    	editor.putString("password", password);
    	editor.putString("email", email);
    	editor.putString("role", role);
    	editor.putString("registrationDate", registrationDate);
    	editor.putBoolean("remember", remember);
    	editor.commit();
    }
    static public void load()
    {
    	if(pref==null) return;
    	name=pref.getString("name" ,"");
    	surname=pref.getString("surname", "");
    	password=pref.getString("password", "");
    	email=pref.getString("email", "");
    	role=pref.getString("role", "");
    	registrationDate=pref.getString("registrationDate", "");
    	remember =pref.getBoolean("remember", false);
    }
    static public Boolean isUserSet(){
    	return(email != null && !email.equalsIgnoreCase("null") && email != "" );
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
    	//Log.d("shared", Uname);
    	
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		User.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		User.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		User.surname = surname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		User.password = password;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		User.registrationDate = registrationDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		User.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		User.role = role;
	}

	public Boolean getRemember() {
		return remember;
	}

	public void setRemember(Boolean remember) {
		User.remember = remember;
	}
    
    
 
}