package jp.arrow.angelforest.flickremover;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.test.IsolatedContext;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

public class FlickCharacter {
    public static final int CHAR_WIDTH = 65;
    public static final int CHAR_HEIGHT = 65;

    public static final int STATUS_INIT = 0;
    public static final int STATUS_FLICK_START = 1;
    public static final int STATUS_FLICKED_CORRECT = 2;
    public static final int STATUS_FLICKED_WRONG = 3;

    private float origin_x;
    private float origin_y;
    private float x;
    private float y;
    private Rect rect;

    //velocity after flicked
    private float vx;
    private float vy;

    private Paint paint = new Paint();
    private int color;

    private int status = STATUS_INIT;

    public FlickCharacter(float x, float y) {
        this.x = x;
        this.y = y;
        origin_x = x;
        origin_y = y;

        rect = new Rect((int)x, (int)y, (int)(x+CHAR_WIDTH), (int)(y+CHAR_HEIGHT));

        double rand = Math.random();

        if(rand < 0.25d) {
            color = Color.RED;
        }
        else if(rand < 0.50d) {
            color = Color.BLUE;
        }
        else if(rand < 0.75d) {
            color = Color.GREEN;
        }
        else {
            color = Color.YELLOW;
        }
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    public void move() {
        //flicked, move character
        if(status == FlickCharacter.STATUS_FLICK_START) {
            x += vx;
            y += vy;
            rect.left = (int)x;
            rect.top = (int)y;
            rect.right = (int)(x+CHAR_WIDTH);
            rect.bottom = (int)(y+CHAR_HEIGHT);
        }
        //if it is wrongly flicked
        else if(status == FlickCharacter.STATUS_FLICKED_WRONG) {
            setInitPos();
        }

        //check if this is flicked to corrent scrn
        status = checkIsFlickedToCorrectScrn();

    }

    public int checkIsFlickedToCorrectScrn() {
        //top
        if(rect.centerY() < -FlickCharacter.CHAR_HEIGHT/2) {
            //check
            if(color == FlickRemoverLogic.scrnColorMap.get(0)) {
//                Log.e(null, "top flicked");
                return STATUS_FLICKED_CORRECT;
            }
            else {
//                Log.e(null, "top flicked wrong");
                return STATUS_FLICKED_WRONG;
            }
        }
        //right
        else if(rect.centerX() > FlickRemoverLogic.SCREEN_WIDTH+FlickCharacter.CHAR_WIDTH/2) {
            //check
            if(color == FlickRemoverLogic.scrnColorMap.get(1)) {
//                Log.e(null, "right flicked");
                return STATUS_FLICKED_CORRECT;
            }
            else {
//                Log.e(null, "right flicked wrong");
                return STATUS_FLICKED_WRONG;
            }
        }
        //bottom
        else if(rect.centerY() > FlickRemoverLogic.SCREEN_HEIGHT+FlickCharacter.CHAR_HEIGHT/2) {
            //check
            if(color == FlickRemoverLogic.scrnColorMap.get(2)) {
//                Log.e(null, "bottom flicked");
                return STATUS_FLICKED_CORRECT;
            }
            else {
//                Log.e(null, "bottom flicked wrong");
                return STATUS_FLICKED_WRONG;
            }
        }
        //left
        else if(rect.centerX() < -FlickCharacter.CHAR_WIDTH/2) {
            //check
            if(color == FlickRemoverLogic.scrnColorMap.get(3)) {
//                Log.e(null, "left flicked");
                return STATUS_FLICKED_CORRECT;
            }
            else {
//                Log.e(null, "left flicked wrong");
                return STATUS_FLICKED_WRONG;
            }
        }

        return STATUS_FLICK_START;
    }

    /**
     * When STATUS_FLICKED_WRONG, this is called.
     *
     */
    public void setInitPos() {
        this.x = origin_x;
        this.y = origin_y;
        rect.left = (int)x;
        rect.top = (int)y;
        rect.right = (int)(x+CHAR_WIDTH);
        rect.bottom = (int)(y+CHAR_HEIGHT);

        vx = 0;
        vy = 0;
        status = STATUS_INIT;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

}
