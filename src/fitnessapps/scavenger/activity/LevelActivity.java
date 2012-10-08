package fitnessapps.scavenger.activity;




import android.os.Bundle;

import fitnessapps.scavenger.components.Level;

public class LevelActivity extends Level {
	
	private long levelDurationMili;

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initGameTimer(levelDurationMili);
        //levelStart();
    }
    
    public void initLevelFeatures(int level) {
    	switch(level) {
    	case 1:
    		levelDurationMili = 5000;
    		break;
    	case 2:
    		
    		break;
    	case 3:
    		
    		break;
    	}
    }
    
    
    

}
