package pk.projektant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
    	 Intent prefIntent = new Intent(this,ActivityDesigner.class);
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
			User.get(ctx).set(jObject.getString("id"), jObject.getString("name"), jObject.getString("surname"), jObject.getString("password"), jObject.getString("registrationDate"), jObject.getString("email"), jObject.getString("role"),remember.isChecked());
			User.save();
    	} 
    	catch (JSONException e) 
    	{
    		  connectionError = true;
			  Log.d("JSONException", e.getLocalizedMessage());
		}
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
			Dialog.setMessage("Fetching User");
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
				Toast.makeText(ctx,"Connection or Server Error", Toast.LENGTH_SHORT).show();
			}
			else if(User.get(ctx).getEmail()=="null")
			{
				Toast.makeText(ctx,"Wrong Username or Password", Toast.LENGTH_SHORT).show();	
			}
			else
			{
				Toast.makeText(ctx,"Login Succes", Toast.LENGTH_SHORT).show();	
				 loginSucces();
			}
			
    	}
		
	}
}
