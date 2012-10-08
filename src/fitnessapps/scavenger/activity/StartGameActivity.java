package fitnessapps.scavenger.activity;

import fitnessapps.scavenger.data.GlobalState;
import fitnessapps.scavenger.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartGameActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        GlobalState.level_number = 1;
		GlobalState.score = 0;
    }
    
    public void goToLevelOne(View view) {
    	Intent levelOne = new Intent(this, LevelActivity.class);
    	startActivity(levelOne);
    }
    
    @Override
	 public void onBackPressed() {
		 moveTaskToBack(true);
	 }
}