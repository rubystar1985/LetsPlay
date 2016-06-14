package com.example.letsplay;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

public class MainActivity extends Activity implements UtilConst{
	private MediaPlayer soundFirstStep, soundSecondStep, soundFirstWin, soundSecondWin, soundDrawResult;
	private int screenWidth;
	private int screenHeight;
	
	DrawView drawView;
	GameData data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGameData();
		drawView = new DrawView(this);
		setContentView(drawView);
		createMediaPlayerForEachSound();
		updateScreenSize();
		refreshDataAndRedraw();
	}

	/**
	 * Passes data to drawView and invokes redrawing
	 */
	private void refreshDataAndRedraw() {
		drawView.setData(this.data);
		drawView.invalidate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Bitmap photo = null;
		
		if (resultCode == RESULT_OK) {
			photo = (Bitmap) data.getExtras().get(KEY_CAMERA_DATA);
			if (requestCode == CAMERA_REQUEST_1) {  
				this.data.setBmpPlayer1(photo);
			} else if (requestCode == CAMERA_REQUEST_2) {
				this.data.setBmpPlayer2(photo);
			}
			
			refreshDataAndRedraw();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
	
		final GameData dataToSave = data.getInstance(); 
		return dataToSave;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP) {
        		Point pt = getCellPos(event.getX(), event.getY());
				if (!(data.getStepsPlayer1().contains(pt) || (data.getStepsPlayer2().contains(pt)))) {
					if (!this.data.getTurn()) {
						addStep(Players.PLAYER_1, pt);
					} else { 
						addStep(Players.PLAYER_2, pt);
					}
					checkIfGameOver();
				}
			}
        
		return super.onTouchEvent(event);
	}

	private void checkIfGameOver() {
		Integer soundId = null;
		Players winner = this.data.getWinner();
		if (winner == Players.PLAYER_1) {
			//showDialog("Congrtulations!!!", "Player 1 won!");
			showDialog(CONGRATULATIONS, PLAYER_1_WIN);
			soundId = R.raw.first_cross_win;
		} else if (winner == Players.PLAYER_2) {
			showDialog(CONGRATULATIONS, PLAYER_2_WIN);
			soundId = R.raw.second_zeros_wins;
		} else if ((data.getStepsPlayer1().size() + data.getStepsPlayer2().size()) == TOTAL_NUMBER_OF_CELLS) {
			showDialog(GAME_OVER, DRAW_RESULT);
			soundId = R.raw.draw;
		}

		if (soundId != null) {
			waitOneSec();
			playSound(soundId);
		}
	}

	private void waitOneSec() {
		try {
			Thread.sleep(ONE_SECOND_IN_MILLIS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void addStep(Players player, Point pt) {
		Integer soundId = null;
		if (player == Players.PLAYER_1) {
			this.data.addStepPlayer1(pt.x, pt.y);
			soundId = R.raw.first;
		} else if (player == Players.PLAYER_2) {
			this.data.addStepPlayer2(pt.x, pt.y);
			soundId = R.raw.second;
		} else {
			Log.e(TAG, NULL_PTR_POINT);
		}
		this.data.turnSwitch();
		refreshDataAndRedraw();
		playSound(soundId);
	}

	/**
	 * @param x - screen x
	 * @param y - screen y
	 * @return cells positions by screen coordinates
	 */
	private Point getCellPos(float x, float y) {
		int cellPosX, cellPosY;
		
		int cellWidth = screenWidth / 3;
		int cellHeight = (screenHeight + ACTIVITY_TITLE_HEIGHT) / 3;
		
		if (x <= (cellWidth)) {
			cellPosX = 0;
		} else if (x <= (cellWidth * 2)) {
			cellPosX = 1;
		} else {
			cellPosX = 2;
		}
		
		if (y <= cellHeight) {
			cellPosY = 0;
		} else if (y <= (cellHeight * 2)) {
			cellPosY = 1;
		} else {
			cellPosY = 2;
		}

		return new Point(cellPosX, cellPosY);
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent cameraIntent;
		switch (item.getItemId()) {
		case R.id.take_image1:
			cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
            startActivityForResult(cameraIntent, CAMERA_REQUEST_1); 
			return true;
		case R.id.take_image2:
			cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
            startActivityForResult(cameraIntent, CAMERA_REQUEST_2); 
			return true;
		case R.id.restart_new_photosmenu_item:
			this.data = new GameData();
			refreshDataAndRedraw();
			return true;
		case R.id.restart_same_photos_menu_item:
			restartWithSamePhotos();
			return true;
		case R.id.exit_menu:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showDialog(String title, String message) {
		new AlertDialog.Builder(this)
	    .setTitle(title)
	    .setMessage(message)
	    .setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				restartWithSamePhotos();
			}
		})
	    .setPositiveButton(OK, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	restartWithSamePhotos();
	        }
	     }).show();
	}
	
	private void restartWithSamePhotos() {
		data.clearPlayerSteps();
		refreshDataAndRedraw();
	}
	
	/**
	 * Tries get last saved data or creates empty data if failed
	 */
	@SuppressWarnings("deprecation")
	private void initGameData() {
		data = (GameData) getLastNonConfigurationInstance();
		
		if (data == null) {
			data = new GameData();
		}
	}
	
	/**
	 * Gets screen width and height and updates "screenWidth" and "screenHeight" variables  
	 */
	private void updateScreenSize() {
		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		Rect displaySizeRect = new Rect();
		display.getRectSize(displaySizeRect);
		this.screenWidth = displaySizeRect.right;
		this.screenHeight = displaySizeRect.bottom;
	}

	/**
	 * Creates necessary MediaPlayer objects 
	 */
	private void createMediaPlayerForEachSound() {
		soundFirstStep	= MediaPlayer.create(this, R.raw.first);
		soundSecondStep	= MediaPlayer.create(this, R.raw.second);
		soundFirstWin	= MediaPlayer.create(this, R.raw.first_cross_win);
		soundSecondWin	= MediaPlayer.create(this, R.raw.second_zeros_wins);
		soundDrawResult	= MediaPlayer.create(this, R.raw.draw);
	}

	/**
	 * play sound according its ID
	 * @param soundId
	 */
	private void playSound(int soundId) {
		switch (soundId) {
		case R.raw.first:
			soundFirstStep.start();
			break;
		case R.raw.second:
			soundSecondStep.start();
			break;
		case R.raw.first_cross_win:
			soundFirstWin.start();
			break;
		case R.raw.second_zeros_wins:
			soundSecondWin.start();
			break;
		case R.raw.draw:
			soundDrawResult.start();
			break;
		default:
			break;
		}
	}
}