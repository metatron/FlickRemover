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
	public static final int DRAW_STAT_POS_Y = 30;
	private Paint paint = new Paint();
	
	private Context context;

	private Canvas actualCanvas;
	private Bitmap bitmap;
	

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

			int colNum = FlickRemoverLogic.getInstance(context).getColCharNum();
			int rowNum = FlickRemoverLogic.getInstance(context).getRowCharNum();
			
			for(int row=0; row<rowNum; row++) {
				ArrayList<FlickCharacter> rowCharacterList = FlickRemoverLogic.getInstance(context).getCharacterList().get(row);
				for(int col=0; col<colNum; col++) {
//					Log.e(null, "col: " + col + ", row: " + row + ": x: " + rowCharacterList.get(col).getX() + ", y: " + rowCharacterList.get(col).getY());
					rowCharacterList.get(col).draw(canvas);
				}
			}
			
			Thread.sleep(REFRESH_RATE);
		}
	}
	
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
    	}
    	else if(event.getAction() == MotionEvent.ACTION_MOVE) {
    	}
    	else if(event.getAction() == MotionEvent.ACTION_UP) {
    	}
    	
		return true;
	}

}
