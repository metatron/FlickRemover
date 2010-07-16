package jp.arrow.angelforest.flickremover;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FlickRemoverView extends View {
    /**
     * 1 tick = REFRESH_RATE
     */
    public static final int REFRESH_RATE = 100;

    public static final int DRAW_STAT_POS_X = 10;
    public static final int DRAW_STAT_POS_Y = 400;
    private Paint paint = new Paint();

    private static final int SCRN_MARKER_WIDTH = 10;

    private Context context;

    private Canvas actualCanvas;
    private Bitmap bitmap;

    private double timer = 0;


    public FlickRemoverView(Context context) {
        super(context);
        //init logic
        FlickRemoverLogic.getInstance(context);

        FlickRemoverLogic.getInstance(context).initFlickCharacters();

        //init canvas
        bitmap = Bitmap.createBitmap(FlickRemoverLogic.SCREEN_WIDTH, FlickRemoverLogic.SCREEN_HEIGHT, Config.RGB_565);
        actualCanvas = new Canvas(bitmap);

        //start
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    drawOnCanvas(actualCanvas);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    private synchronized void drawOnCanvas(Canvas canvas) throws InterruptedException {
        while(true) {
            //init
            canvas.drawColor(Color.BLACK);

            //draw character
            drawCharacters(canvas);

            //draw scrn marker
            drawScrnColor(canvas);

            //draw text
            drawText(canvas);

            //increment timer
            int gamestatus = FlickRemoverLogic.getInstance(context).getGameStatus();
            if(gamestatus == FlickRemoverLogic.STATUS_STARTED) {
            	//TODO need to do something
                timer += 1.0d/REFRESH_RATE;
            }
            else if(gamestatus == FlickRemoverLogic.STATUS_READY) {
                timer = 0.0d;
            }

            //check for gameover
            if(FlickRemoverLogic.getInstance(context).checkGameFinished()) {
                FlickRemoverLogic.getInstance(context).setGameStatus(FlickRemoverLogic.STATUS_FINISHED);
            }

            Thread.sleep(REFRESH_RATE);
        }
    }


    private void drawText(Canvas canvas) {
        paint.setColor(Color.RED);
        canvas.drawText("Time: " + timer, DRAW_STAT_POS_X, DRAW_STAT_POS_Y, paint);
    }

    //---------------------- draw Characters -----------------------//

    private void drawCharacters(Canvas canvas) {
        if((FlickRemoverLogic.getInstance(context).getGameStatus() == FlickRemoverLogic.STATUS_FINISHED)) {
            return ;
        }

        int colNum = FlickRemoverLogic.getInstance(context).getColCharNum();
        int rowNum = FlickRemoverLogic.getInstance(context).getRowCharNum();

        //draw characters
        for(int row=0; row<rowNum; row++) {
            ArrayList<FlickCharacter> rowCharacterList = FlickRemoverLogic.getInstance(context).getCharacterList().get(row);
            for(int col=0; col<colNum; col++) {
                rowCharacterList.get(col).draw(canvas);
                rowCharacterList.get(col).move();
            }
        }
    }

    //------------------------ draw scrn marker -----------------------//

    private void drawScrnColor(Canvas canvas) {
        if((FlickRemoverLogic.getInstance(context).getGameStatus() == FlickRemoverLogic.STATUS_FINISHED)) {
            return ;
        }

        for(int i=0; i<4; i++) {
            if(FlickRemoverLogic.scrnColorMap.get(i) != null) {
                int color = FlickRemoverLogic.scrnColorMap.get(i);
                switch(i) {
                case 0:
                    drawTop(canvas, color);
                    break;
                case 1:
                    drawRight(canvas, color);
                    break;
                case 2:
                    drawBottom(canvas, color);
                    break;
                case 3:
                    drawLeft(canvas, color);
                    break;
                }

            }
        }
    }

    private void drawTop(Canvas canvas, int color) {
        paint.setColor(color);
        canvas.drawRect(0, 0, FlickRemoverLogic.SCREEN_WIDTH, SCRN_MARKER_WIDTH, paint);
    }
    private void drawRight(Canvas canvas, int color) {
        paint.setColor(color);
        canvas.drawRect(FlickRemoverLogic.SCREEN_WIDTH-SCRN_MARKER_WIDTH, 0, FlickRemoverLogic.SCREEN_WIDTH, FlickRemoverLogic.SCREEN_HEIGHT, paint);
    }
    private void drawBottom(Canvas canvas, int color) {
        paint.setColor(color);
//        Log.e(null, "asdfasdfasdf: " + (canvas.getHeight()-SCRN_MARKER_WIDTH));
        canvas.drawRect(0, canvas.getHeight()-(SCRN_MARKER_WIDTH*6), FlickRemoverLogic.SCREEN_WIDTH, FlickRemoverLogic.SCREEN_HEIGHT, paint);
    }
    private void drawLeft(Canvas canvas, int color) {
        paint.setColor(color);
        canvas.drawRect(0, 0, SCRN_MARKER_WIDTH, FlickRemoverLogic.SCREEN_HEIGHT, paint);
    }

    //-------------------- draw timer -------------------------//

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);

        invalidate();
    }

    /**
     * touch event listener.
     *
     * @param event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //start game
        if(event.getAction() == (MotionEvent.ACTION_DOWN)) {
            FlickRemoverLogic.getInstance(context).onTouchDownAction(event);
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE) {
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            FlickRemoverLogic.getInstance(context).onTouchUpAction(event);
        }

        return true;
    }

}
