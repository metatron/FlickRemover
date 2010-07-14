package jp.arrow.angelforest.flickremover;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class FlickRemoverLogic {
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static final int MARGIN = 5;
	
	private static FlickRemoverLogic logic;
	private static Context context;
	
	private ArrayList<ArrayList<FlickCharacter>> characterList = new ArrayList<ArrayList<FlickCharacter>>();
	private int rowCharNum;
	private int colCharNum;
	
	public static FlickRemoverLogic getInstance(Context context) {
		if(logic == null) {
			logic = new FlickRemoverLogic(context);
			Point scrnWH = detectScreenWidthHeight();
			SCREEN_WIDTH = scrnWH.x;
			SCREEN_HEIGHT = scrnWH.y;
		}
		return logic;
	}
	
	private FlickRemoverLogic(Context context) {
		FlickRemoverLogic.context = context;
	}
	
	public static Point detectScreenWidthHeight() {
		Display disp =
			((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).
			getDefaultDisplay();
		return new Point(disp.getWidth(), disp.getHeight());
	}
	
	public void initFlickCharacters() {
		//get how many on row
		rowCharNum = (SCREEN_WIDTH/FlickCharacter.CHAR_WIDTH) - 1;
		colCharNum = (SCREEN_HEIGHT/FlickCharacter.CHAR_HEIGHT) - 1;
		
		for(int col = 0; col < colCharNum; col++) {
			ArrayList<FlickCharacter> rowCharList = new ArrayList<FlickCharacter>();
			for(int row = 0; row < rowCharNum; row++) {
				int x = (FlickCharacter.CHAR_WIDTH*row) + MARGIN;
				int y = FlickCharacter.CHAR_HEIGHT*col;
//				Log.e(null, "col: " + col + ", row: " + row + ": x: " + x + ", y: " + y);
				rowCharList.add(new FlickCharacter(x, y));
			}//col
			
			//add to row
			characterList.add(rowCharList);
		}//row
	}

	public ArrayList<ArrayList<FlickCharacter>> getCharacterList() {
		return characterList;
	}

	public int getRowCharNum() {
		return rowCharNum;
	}

	public int getColCharNum() {
		return colCharNum;
	}

}
