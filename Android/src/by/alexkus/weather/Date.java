package by.alexkus.weather;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

public class Date extends TextView {

	Calendar mCalendar;
	private final static String mFormat = "EEEE, MMMM d";

	private Runnable mTicker;
	private Handler mHandler;
	
	private boolean mTickerStopped = false;
	
	public Date(Context context) {
		super(context);
		initDate(context);
	}

	public Date(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDate(context);
	}
	
	private void initDate(Context context) {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
	}

	public void updateDate()
	{
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		setText(DateFormat.format(mFormat, mCalendar));
		invalidate();
	}
	
	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();
		mHandler = new Handler();

		mTicker = new Runnable() {
			public void run() {
				if (mTickerStopped) return;
					updateDate();
					long now = SystemClock.uptimeMillis();
					long updatePeriod = 60 * 60 * 1000; // 1 hour
					long next = now + (updatePeriod - now % updatePeriod);
					mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
    }
}
   