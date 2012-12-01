package pk.projektant;

public final class User {
	 
    // nale¿y zwróciæ uwagê na u¿ycie s³owa kluczowego volatile
    private static volatile User instance = null;
    public String id;
    public String name;
    public String surname;
    public String password;
    public String registrationDate;
    public String email;
    public String role;
    public Boolean remember;
    
    public static User get() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User();
                }
            }
        }
        return instance;
    }
 
    public void set(String id, String name, String surname, String password, String registration, String email, String role){
    	this.id=id;
    	this.name=name;
    	this.surname=surname;
    	this.password=password;
    	this.registrationDate=registration;
    	this.email=email;
    	this.role=registration;
    }
    
    public void save()
    {
    	
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
    }
    
    
 
}