package fitnessapps.scavenger.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fitnessapps.scavenger.activity.R;;

public enum ColorEnum {
	RED("Red", R.drawable.red),
	BLUE("Blue", R.drawable.blue),
	GREEN("Green", R.drawable.green),
	PURPLE("Purple", R.drawable.purple),
	PINK("Pink", R.drawable.pink),
	ORANGE("Orange", R.drawable.orange),
	GREY("Grey", R.drawable.grey),
	BROWN("Brown", R.drawable.brown);
	
	private static final List<ColorEnum> VALUES =
		    Collections.unmodifiableList(Arrays.asList(values()));
		  private static final int SIZE = VALUES.size();
		  private static final Random RANDOM = new Random();
		  private final String stringVersion;
		  private final int resource;
		  
		  public static ColorEnum randomColor()  {
		    return VALUES.get(RANDOM.nextInt(SIZE));
		  }
		  
		  private ColorEnum(String str, int resource){
			  this.stringVersion = str;
			  this.resource = resource;
		  }
		  
		  public String getStringVersion(){
			  return stringVersion;
		  }
		  
		  public int getResource(){
			  return resource;
		  }
}
