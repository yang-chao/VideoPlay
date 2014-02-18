package com.test.videoplay;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {

	private SurfaceView mSurfaceView;
//	private MediaPlayer mMediaPlayer;
	private MyVideoView mVideoView;
	private MyMediaController mController;
	private LinearLayout mVideoContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		mMediaPlayer = new MediaPlayer();
		mController = new MyMediaController(this);
		mVideoView = (MyVideoView) findViewById(R.id.videoview);
		mController.setAnchorView(mVideoView);
		mVideoView.setMediaController(mController);
		
//		mVideoContainer = (LinearLayout) findViewById(R.id.main_videoview_contianer);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceView.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
//				play();
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// TODO Auto-generated method stub
				
			}
		});
		playMedia();
		
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
	}
	
//	private void play() {
//		String path = Environment.getExternalStoragePublicDirectory(
//				Environment.DIRECTORY_DCIM).getPath() + File.separator + "Vemento"
//				+ File.separator + "Temp_20140212_161825.mp4";
//		FileInputStream stream = null;
//		try {
//			stream = new FileInputStream(new File(path));
//			mMediaPlayer.reset();
//			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			// 设置需要播放的视频
//			mMediaPlayer.setDataSource(stream.getFD());
//			mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//			// 把视频画面输出到SurfaceView
//			mMediaPlayer.setDisplay(mSurfaceView.getHolder());
//			mMediaPlayer.prepare();
//			// 播放
//			mMediaPlayer.start();
//			stream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("xxx landscape");
			
			mVideoView.setVideoScale(1770, 1080);
			
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			System.out.println("xxx portrait");
			
			mVideoView.setVideoScale(1080, 800);
			
		}
	}
	
	private void playMedia() {
		mVideoView.setVideoURI(Uri.parse("http://110.18.245.9/37ada8e14cbfa12-1392289226-3721189064/data7/flv.bn.netease.com/videolib3/1402/13/EyKrA3653/SD/EyKrA3653-mobile.mp4"));
		mVideoView.start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mMediaPlayer.release();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
