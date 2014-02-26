package com.test.videoplay;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private MyVideoView mVideoView;
	private MyMediaController mController;

	private int mPotraitHeight;
	private int mLandscapeWidth;
	private int mLandscapeHeight;
	
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	private GestureDetector mGestureDetector;
	private AudioManager mAudioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mController = new MyMediaController(this);
		mVideoView = (MyVideoView) findViewById(R.id.videoview);
		mController.setAnchorView(mVideoView);
		mVideoView.setMediaController(mController);

		try {
			playMedia();
		} catch (IOException e) {
			e.printStackTrace();
		}

		findViewById(R.id.sw).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (MainActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else if (MainActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			}
		});

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mLandscapeWidth = dm.heightPixels;
		mLandscapeHeight = dm.widthPixels;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}
		return super.onTouchEvent(event);
	}

	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;
		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float mOldX = e1.getX();
			float mOldY = e1.getY();
			int y = (int) e2.getRawY();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();
			if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5.0)// 左边滑动
				onBrightnessSlide((mOldY - y) / windowHeight);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;
			// 显示
			// mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			// mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;
		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
		// 变更进度条
		// ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		// lp.width = findViewById(R.id.operation_full).getLayoutParams().width
		// * index / mMaxVolume;
		// mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;
			// 显示
			// mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			// mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);
		// ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		// lp.width = (int) (findViewById(R.id.operation_full)
		// .getLayoutParams().width * lpa.screenBrightness);
		// mOperationPercent.setLayoutParams(lp);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		System.out.println("xxx pre-videowidth : " + mVideoView.getWidth());
		System.out.println("xxx pre-videoheight : " + mVideoView.getHeight());
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (mPotraitHeight == 0) {
				mPotraitHeight = mVideoView.getHeight();
			}
			mVideoView.setVideoScale(mLandscapeWidth, mLandscapeHeight);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mVideoView.setVideoScale(mLandscapeHeight, mPotraitHeight);
		}
		System.out.println("xxx after-videowidth : " + mVideoView.getWidth());
		System.out.println("xxx after-videoheight : " + mVideoView.getHeight());
	}

	private void playMedia() throws IOException {
		// mVideoView.setVideoURI(Uri.parse("http://flv.bn.netease.com/videolib3/1208/13/WZHyF2463/WZHyF2463-mobile.mp4"));
		// mVideoView.setVideoURI(Uri.parse("http://flv.bn.netease.com/videolib3/1208/12/hTFnk8972/hTFnk8972-mobile.mp4"));

		// mVideoView.setVideoURI(Uri.parse("file:///assets/WZHyF2463-mobile.mp4"));
		// mVideoView.setVideoURI(Uri.parse("file:///assets/hTFnk8972-mobile.mp4"));
		// mVideoView.setVideoPath("file:///assets/hTFnk8972-mobile.mp4");

		// Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"+
		// R.raw.c);
		// mVideoView.setVideoURI(uri);

		mVideoView.setVideoURI(Uri.parse("http://110.18.245.9/37ada8e14cbfa12-1392289226-3721189064/data7/flv.bn.netease.com/videolib3/1402/13/EyKrA3653/SD/EyKrA3653-mobile.mp4"));
		mVideoView.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
