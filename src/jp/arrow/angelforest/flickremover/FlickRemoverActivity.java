package jp.arrow.angelforest.flickremover;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class FlickRemoverActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FlickRemoverView(this), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }
    
    @Override
    protected void onPause() {
    	System.exit(1);
    }
}