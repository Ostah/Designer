package pk.projektant;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ActivityLogin extends Activity {

	Button btn = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
   
       super.onCreate(savedInstanceState);
       setContentView(R.layout.l_view_login); 
       Tokenizer.open(getResources().openRawResource(R.raw.data));
       btn = (Button) findViewById(R.id.login_button_go);
       btn.setOnClickListener(new View.OnClickListener() {		
    	   public void onClick(View arg0) {
    		   loginSucces();			
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
}
