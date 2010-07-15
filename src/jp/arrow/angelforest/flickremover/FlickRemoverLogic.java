package jp.arrow.angelforest.flickremover;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

public class FlickRemoverLogic {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static final int MARGIN_TOP = 5;
    public static final int MARGIN_LEFT = 5;
    public static final int MARGIN = 5;

    public static final int STATUS_READY = 0;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_FINISHED = 2;
    private int gameStatus = STATUS_READY;

    private static FlickRemoverLogic logic;
    private static Context context;

    private ArrayList<ArrayList<FlickCharacter>> characterList = new ArrayList<ArrayList<FlickCharacter>>();
    private int rowCharNum;
    private int colCharNum;

    /**
     * scrn color set
     * 0: top
     * 1: right
     * 2: bottom
     * 3: left
     *
     */
    public static HashMap<Integer, Integer> scrnColorMap = new HashMap<Integer, Integer>();

    private FlickCharacter flickingCharacter;

    private float startX;
    private float startY;

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

    /**
     * init game.
     *
     */
    public synchronized void initFlickCharacters() {
        characterList.clear();
        scrnColorMap.clear();

        //get how many on row
        rowCharNum = (SCREEN_HEIGHT/(FlickCharacter.CHAR_HEIGHT+MARGIN))-1;
        colCharNum = (SCREEN_WIDTH/(FlickCharacter.CHAR_WIDTH+MARGIN));

        for(int row = 0; row < rowCharNum; row++) {
            ArrayList<FlickCharacter> rowCharList = new ArrayList<FlickCharacter>();
            for(int col = 0; col < colCharNum; col++) {
                int x = FlickCharacter.CHAR_WIDTH*col;
                if(col > 0) {
                    x += MARGIN*col;
                }

                int y = FlickCharacter.CHAR_HEIGHT*row;
                if(row > 0) {
                    y += MARGIN*row;
                }
                Log.e(null, "col: " + col + ", row: " + row + ": x: " + x + ", y: " + y);
                rowCharList.add(new FlickCharacter(x, y));
            }//col

            //add to row
            characterList.add(rowCharList);
        }//row

        //randomly set the screen
        setScrnColor();
    }

    /**
     * scrn color set
     *
     */
    private void setScrnColor() {
        ArrayList<Integer> setColorList = new ArrayList<Integer>();
        for(int i=0; i<4; i++) {
            do {
                double rand = Math.random();
                if(rand < 0.25d && !setColorList.contains(Color.RED)) {
                    scrnColorMap.put(i, Color.RED);
                    setColorList.add(Color.RED);

                    Log.e(null, "scrn: " + i + ", RED");
                }
                else if(rand < 0.50d && !setColorList.contains(Color.BLUE)) {
                    scrnColorMap.put(i, Color.BLUE);
                    setColorList.add(Color.BLUE);

                    Log.e(null, "scrn: " + i + ", BLUE");
                }
                else if(rand < 0.75d && !setColorList.contains(Color.GREEN)) {
                    scrnColorMap.put(i, Color.GREEN);
                    setColorList.add(Color.GREEN);

                    Log.e(null, "scrn: " + i + ", GREEN");
                }
                else if(!setColorList.contains(Color.YELLOW)) {
                    scrnColorMap.put(i, Color.YELLOW);
                    setColorList.add(Color.YELLOW);

                    Log.e(null, "scrn: " + i + ", YELLOW");
                }
            }
            while(scrnColorMap.get(i) == null);
        }
    }

    public boolean checkGameFinished() {
        boolean isFinished = true;
        for(int row = 0; row < rowCharNum; row++) {
            ArrayList<FlickCharacter> charList = characterList.get(row);
            for(int col = 0; col < colCharNum; col++) {
                FlickCharacter flickChar = charList.get(col);
                if(flickChar.getStatus() == FlickCharacter.STATUS_FLICKED_CORRECT) {
                    isFinished &= true;
                }
                else {
                    return false;
                }
            }//col
        }//row

        return isFinished;
    }

    /**
     * detect Flicking character
     */
    public void onTouchDownAction(MotionEvent event) {
        //
        if(gameStatus == STATUS_READY) {
            gameStatus = STATUS_STARTED;

        }
        else if(gameStatus == STATUS_FINISHED) {
            initFlickCharacters();
            gameStatus = STATUS_READY;
            return ;
        }

        for(int row = 0; row < rowCharNum; row++) {
            ArrayList<FlickCharacter> charList = characterList.get(row);
            for(int col = 0; col < colCharNum; col++) {
                FlickCharacter flickChar = charList.get(col);

                //check if the user finger is touching the character
                if((event.getX() >= flickChar.getX() && event.getX() <= flickChar.getX()+FlickCharacter.CHAR_WIDTH) &&
                        event.getY() >= flickChar.getY() && event.getY() <= flickChar.getY()+FlickCharacter.CHAR_HEIGHT) {
                    //set to be flicked
                    flickingCharacter = flickChar;
//                    Log.e(null, "touched: " + col + ", " + row);
                    break;
                }
            }//col
        }//row

        startX = event.getX();
        startY = event.getY();

    }

    /**
     * UPed after key down
     *
     * @param event
     */
    public void onTouchUpAction(MotionEvent event) {
        //calculate the velocity
        double time = event.getEventTime() - event.getDownTime();
        time /= 100;
//        Log.e(null, "down time: " + time);

//        Log.e(null, "start: " + startX + ", " + startY + " end: " + event.getX() + ", " + event.getY());

        double dist = Math.sqrt(Math.pow(event.getX()-startX, 2) + Math.pow(event.getY()-startY, 2));
        double velocity = dist/time;
//        Log.e(null, "dist: " + dist + "velo: " + velocity);

        double rad = Math.atan2(event.getY()-startY, event.getX()-startX);
//        double rad = Math.atan((event.getY()-startY)/(event.getX()-startX));
        double fdeg = (Math.toDegrees(rad)+360)%360;
        double frad = (rad+2*Math.PI)%(2*Math.PI);
//        Log.e(null, "degree: " + fdeg + ", " + frad);

        double vx = velocity*Math.cos(frad);
        double vy = velocity*Math.sin(frad);
//        Log.e(null, "vxy: " + vx + ", " + vy);

        //set velocity to touched character
        flickingCharacter.setVx((float)vx);
        flickingCharacter.setVy((float)vy);

        //start flick
        if(vx > 0 || vy > 0) {
            flickingCharacter.setStatus(FlickCharacter.STATUS_FLICK_START);
        }
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

    public HashMap<Integer, Integer> getScrnColorMap() {
        return scrnColorMap;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

}
