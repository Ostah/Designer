package pk.projektant;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;

public class ActivityLogin extends Activity {

	Button btn = null;
	Button skip = null;
	Button register = null;
	String userData ="";
	Boolean connectionError = false;
	CheckBox remember = null;
	Boolean isRemembered=false;
	TextView txt_username=null;
	TextView txt_password=null;
	Context ctx;
	
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    	StrictMode.setThreadPolicy(policy);
    	
    	ctx = this;
       super.onCreate(savedInstanceState);
       setContentView(R.layout.l_view_login); 
       Tokenizer.open(getResources().openRawResource(R.raw.data));
       btn = (Button) findViewById(R.id.login_button_go);
       register = (Button) findViewById(R.id.login_button_register);
       remember = (CheckBox) findViewById(R.id.login_checkbox);
       skip = (Button) findViewById(R.id.login_skip);
       txt_username = (TextView)  findViewById(R.id.login_username);
       txt_password = (TextView)  findViewById(R.id.login_password);
       User.get(ctx);
       RelativeLayout aa = (RelativeLayout)findViewById(R.id.login_layout);
       aa.setOnTouchListener(new OnTouchListener()
       {
    	    public boolean onTouch(View view, MotionEvent ev)
    	    {
    	        hideKeyboard(view);
    	        return false;
    	    }
    	});
   
       
     
	if(User.isUserSet()){
    	   if(User.get(ctx).getRemember())
    	   {
    		   isRemembered = true;
        	   ThreadLogin task = new ThreadLogin();
    		   task.execute();	 
    	   }
    	   else{
    		   isRemembered=false;
    		   User.clear();
    	   }
    	 
       }
       
       btn.setOnClickListener(new View.OnClickListener() {		
    	   public void onClick(View arg0) {
    		   ThreadLogin task = new ThreadLogin();
    		   task.execute();			
    	   }
    	});
       
       skip.setOnClickListener(new View.OnClickListener() {		
    	   public void onClick(View arg0) {
    		   loginSucces();	
    	   }
    	});
       
       register.setOnClickListener(new View.OnClickListener() {		
    	   public void onClick(View arg0) {
    		   goRegister();		 		
    	   }
    	});
       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.l_view_login, menu);
        return true;
    }
    
    public void loginSucces()
    {
    	//Intent prefIntent = new Intent(this,ActivityDesigner.class);
    	Intent prefIntent = new Intent(this,ActivityProjects.class);
         this.startActivity(prefIntent);
    }
    public void goRegister()
    {
    	 Intent prefIntent = new Intent(this,ActivityRegister.class);
         this.startActivity(prefIntent);
    }
    
    public void parseUserJSON(String data){
    	if(data=="") return;
    	
    	try 
    	{
			JSONObject jObject = new JSONObject(data);
			User.get(ctx).set(jObject.getString("id"), jObject.getString("name"), jObject.getString("surname"), jObject.getString("password"), Long.valueOf(jObject.getString("registrationDate")), jObject.getString("email"), jObject.getString("role"),remember.isChecked());
			User.save();
    	} 
    	catch (JSONException e) 
    	{
    		  connectionError = true;
			  Log.d("JSONException", e.getLocalizedMessage());
		}
    }
    
    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    public String httpLogin() {
        // Create a new HttpClient and Post Header
    	userData="";
    	connectionError = false;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://designercms.herokuapp.com/logowanie/");

        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            if(isRemembered)
            {
                nameValuePairs.add(new BasicNameValuePair("username", User.get(ctx).getEmail()));
                nameValuePairs.add(new BasicNameValuePair("password", User.get(ctx).getPassword()));
        	
            }
            else
            {
                nameValuePairs.add(new BasicNameValuePair("username", txt_username.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("password", AeSimpleSHA1.SHA1( txt_password.getText().toString())));	
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
          
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                userData =  EntityUtils.toString(httpEntity);             
            }         
            else {
            	connectionError = true;
            }
        } 
        catch (ClientProtocolException e) {
            Log.d("ClientProtocolException", e.getLocalizedMessage());
            connectionError = true;
        } 
        catch (IOException e) {
        	connectionError = true;
        	 Log.d("IOException", e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
        	connectionError = true;
       	 Log.d("SHA1 Error", e.getLocalizedMessage());
			e.printStackTrace();
		}
        return null;
    }
  
    
   private class ThreadLogin extends AsyncTask<Void, Void, Void> {
		private ProgressDialog Dialog;

		protected void onPreExecute() {
			Dialog = new ProgressDialog(ctx);
			Dialog.setMessage("Autoryzowanie U¿ytkownika");
			Dialog.setCanceledOnTouchOutside(false);
			Dialog.setCancelable(false);
			Dialog.show();
		}

		protected Void doInBackground(Void... arg0) {
			httpLogin();
			parseUserJSON(userData);
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();	
			if(userData=="" || connectionError) 
			{
				Toast.makeText(ctx,"B³¹d Po³¹czenia", Toast.LENGTH_SHORT).show();
			}
			else if(User.get(ctx).getEmail()=="null")
			{
				Toast.makeText(ctx,"Z³a nazwa u¿ytkownika lub has³o", Toast.LENGTH_SHORT).show();	
			}
			else
			{
				Toast.makeText(ctx,"Logowanie zakoñczone", Toast.LENGTH_SHORT).show();	
				 loginSucces();
			}
			
    	}
		
	}
}
