package pk.projektant;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ActivityRegister extends Activity {

	private TextView name;
	private TextView surname;
	private TextView email;
	private TextView password;
	private Button bRegister;
	private Context ctx;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.l_view_register);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		name = (TextView) findViewById(R.id.register_name);
		surname = (TextView) findViewById(R.id.register_surname);
		email = (TextView) findViewById(R.id.register_email);
		password = (TextView) findViewById(R.id.register_password);
		bRegister = (Button) findViewById(R.id.register_button);
		ctx = this;
		 
		bRegister.setOnClickListener(new View.OnClickListener() {		
	    	   public void onClick(View arg0) {
	    		   goRegister();	
	    	   }
	    	});
	}

	private void goRegister(){
		
		// Poprawnoœæ danych
		if(!isDataFromFormValid()) return;
		
		RestClient client = new RestClient("http://designercms.herokuapp.com/user/");
		client.AddParam("name", name.getText().toString());
		client.AddParam("surname", surname.getText().toString());
		client.AddParam("email", email.getText().toString());
		try {
			client.AddParam("password", AeSimpleSHA1.SHA1(password.getText().toString()));
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		try {
		    client.Execute(RestClient.RequestMethod.POST);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		String response = client.getResponse();
		if(response.contains("NotAllRequiredFields"))
		{
			
		}
		else if(response.contains("EmailAlreadyTaken")){
			Toast.makeText(this, "Podany email jest ju¿ zajêty", Toast.LENGTH_LONG).show();
		}
		else{
			JSONObject jObject;
			try {
				jObject = new JSONObject(response);
			 
			if(jObject.optString("email")!=null&&jObject.optString("email")!=""){
				User.get(ctx).set(jObject.getString("id"), jObject.getString("name"), jObject.getString("surname"), jObject.getString("password"), jObject.getString("registrationDate"), jObject.getString("email"), jObject.getString("role"),false);	
				if(User.isUserSet())
				{
					Toast.makeText(this, "Rejestracja Udana", Toast.LENGTH_LONG).show();
					Intent prefIntent = new Intent(this,ActivityProjects.class);
			         this.startActivity(prefIntent);
				}
			}
			}catch (JSONException e) {
				Toast.makeText(this, "Serwer zwróci³ niepoprawne dane", Toast.LENGTH_LONG).show();
				return;
			}

		}
		Log.d("response", response);
	}
	
	private Boolean isDataFromFormValid(){
		if(name.getText().length()==0){
			Toast.makeText(ctx,"Pole imiê jest puste", Toast.LENGTH_SHORT).show();	
			return false;
		}
		if(surname.getText().length()==0){
			Toast.makeText(ctx,"Pole nazwisko jest puste", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(email.getText().length()==0 ||  !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
			Toast.makeText(ctx,"Pole email jest Ÿle wype³nione", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(password.getText().length()==0){
			Toast.makeText(ctx,"Pole has³o jest puste", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.l_view_register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
