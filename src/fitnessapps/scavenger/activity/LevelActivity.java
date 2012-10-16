package fitnessapps.scavenger.activity;




import android.os.Bundle;
import android.view.WindowManager.LayoutParams;

import fitnessapps.scavenger.components.Level;
import fitnessapps.scavenger.data.GlobalState;

public class LevelActivity extends Level {
	
	private long levelDurationMili;
	private int tasksToComplete;

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLevelFeatures(GlobalState.level_number);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGameTimer(levelDurationMili);
        setTasksToComplete(tasksToComplete);
    }
    
    public void initLevelFeatures(int level) {
    	switch(level) {
    	case 1:
    		levelDurationMili = 90000;
    		tasksToComplete = 2;
    		break;
    	case 2:
    		levelDurationMili = 61000;
    		tasksToComplete = 4;
    		break;
    	case 3:
    		levelDurationMili = 91000;
    		tasksToComplete = 6;
    		break;
    	case 4:
    		levelDurationMili = 110000;
    		tasksToComplete = 8;
    		break;
    	case 5:
    		levelDurationMili = 131000;
    		tasksToComplete = 10;
    		break;
    	case 6:
    		levelDurationMili = 181000;
    		tasksToComplete = 12;
    		break;
    	case 7:
    		levelDurationMili = 211000;
    		tasksToComplete = 14;
    		break;
    	case 8:
    		levelDurationMili = 241000;
    		tasksToComplete = 16;
    		break;
    	case 9:
    		levelDurationMili = 271000;
    		tasksToComplete = 18;
    		break;
    	case 10:
    		levelDurationMili = 301000;
    		tasksToComplete = 20;
    		break;
    	}
    }
    
    
    

}
