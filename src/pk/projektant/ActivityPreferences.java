package pk.projektant;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.view.Menu;

public class ActivityPreferences extends  PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}


}
