package jp.arrow.angelforest.flickremover;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;

public class FlickCharacter {
	public static final int CHAR_WIDTH = 20;
	public static final int CHAR_HEIGHT = 20;
	
	private float x;
	private float y;
	private Rect rect;
	
	private Paint paint = new Paint();
	
	public FlickCharacter(float x, float y) {
		this.x = x;
		this.y = y;
		
		rect = new Rect((int)x, (int)y, (int)(x+CHAR_WIDTH), (int)(y+CHAR_HEIGHT));
		
		paint.setColor(Color.CYAN);
	}
	
	public void draw(Canvas canvas) {
		canvas.drawCircle(x, x, 20, paint);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
}
