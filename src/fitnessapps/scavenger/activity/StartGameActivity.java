package fitnessapps.scavenger.activity;

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
    }
    
    public void startGame(View view) {
    	Intent levelOne = new Intent(this, LevelActivity.class);
    	startActivity(levelOne);
    }
    
    @Override
	 public void onBackPressed() {
		 moveTaskToBack(true);
	 }
}