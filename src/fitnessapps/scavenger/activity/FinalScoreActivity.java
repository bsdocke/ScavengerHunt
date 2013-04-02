package fitnessapps.scavenger.activity;

import java.util.List;

import fitnessapps.scavenger.data.ColorEnum;
import fitnessapps.scavenger.data.GlobalState;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class FinalScoreActivity extends Activity {

	// Views
	private ListView colorListView;
	private ListView scoreListView;
	int titleId;
	int scoreId;
	// Color
	private List<ColorEnum> colorList;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalscore);
        colorList = GlobalState.randColorList;
        
        //populateScores(colorList);
    }
    
    private void populateScores(List<ColorEnum> list) {
    	// TO-DO
    }
    
    @Override
    public void onBackPressed() {
    	Intent restart = new Intent(this, StartGameActivity.class);
		startActivity(restart);
	}
}
