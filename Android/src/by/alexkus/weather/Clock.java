package by.alexkus.weather;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

public class Clock extends TextView {

	Calendar mCalendar;
	private final static String mFormat = "k:mm";

	private Runnable mTicker;
	private Handler mHandler;
	
	private boolean mTickerStopped = false;

	public Clock(Context context) {
		super(context);
		initClock(context);
	}

	public Clock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}
	
	private void initClock(Context context) {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
	}

	public void updateTime()
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
					mCalendar.setTimeInMillis(System.currentTimeMillis());
					setText(DateFormat.format(mFormat, mCalendar));
					invalidate();
					long now = SystemClock.uptimeMillis();
					long updatePeriod = 1000; // 1 second
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
   