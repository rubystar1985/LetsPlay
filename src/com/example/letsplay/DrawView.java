package com.example.letsplay;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class DrawView extends View implements UtilConst{
	private GameData data ;
	private Paint paint = new Paint();
	private Canvas canvas = null;
	private int canvasHeight;
	private int canvasWidth;
	Bitmap bmp;
	float bufferX, bufferY;
	public DrawView(Context context) {
		super(context);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(15);
	}
	
	public void setBitmap(Bitmap bmp) {
		this.bmp = bmp;
	}

	public void addPlayerStep1(int posX, int posY) {
		data.addStepPlayer1(posX, posY);
	}
	
	public ArrayList<Point> getPlayer1Steps() {
		return data.getStepsPlayer1();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		this.canvasHeight = canvas.getHeight();
		this.canvasWidth = canvas.getWidth();
		drawCells();
		if (this.data != null) {
			for (Point point : data.getStepsPlayer1()) {
				drawStepPlayer1(point.x, point.y);
			}
			
			for (Point point : data.getStepsPlayer2()) {
				drawStepPlayer2(point.x, point.y);
			}
		}

		super.onDraw(canvas);
	}

	
	private void drawStepPlayer1(int posX, int posY) {
		Bitmap bmp = this.data.getBmpPlayer1();
		if (bmp == null) {
			drawLinePaint(0 + (canvasWidth / 3 * posX), 0 + (canvasHeight / 3 * posY), canvasWidth / 3 + (canvasWidth / 3 * posX), canvasHeight / 3 + (canvasHeight / 3 * posY));
			drawLinePaint(canvasWidth / 3 + (canvasWidth / 3 * (posX)), 0 + (canvasHeight / 3 * posY), 0 + (canvasWidth / 3 * (posX)), canvasHeight / 3 + (canvasHeight / 3 * posY));
		} else {
			Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
			Rect dst = new Rect(0 + (canvasWidth/3 * posX), 0 + (canvasHeight/3 * posY), canvasWidth/3 + (canvasWidth/3 * posX), canvasHeight/3 + (canvasHeight/3 * posY));
			canvas.drawBitmap(bmp, src, dst, paint);
		}
	}

	private void drawStepPlayer2(int posX, int posY) {
		Bitmap bmp = this.data.getBmpPlayer2();
		if (bmp == null) {
			Rect dst = new Rect(0 + (canvasWidth / 3 * posX), 0 + (canvasHeight / 3 * posY), canvasWidth / 3 + (canvasWidth / 3 * posX), canvasHeight / 3 + (canvasHeight / 3 * posY));
			RectF rectf = new RectF(dst);
			drawRoundPaint(rectf, ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS);
		} else {
			Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
			Rect dst = new Rect(0 + (canvasWidth / 3 * posX), 0 + (canvasHeight / 3 * posY), canvasWidth / 3 + (canvasWidth / 3 * posX), canvasHeight / 3 + (canvasHeight / 3 * posY));
			canvas.drawBitmap(bmp, src, dst, paint);
		}
	}
	
	private void drawCells() {
		drawLinePaint(0, 0, 				canvasWidth, 0);
		drawLinePaint(0, canvasHeight / 3, 	canvasWidth, canvasHeight / 3);
		drawLinePaint(0, canvasHeight / 3 * 2, 	canvasWidth, canvasHeight / 3 * 2);
		drawLinePaint(0, canvasHeight - 3, 	canvasWidth, canvasHeight - 3);
		drawLinePaint(0, 				0, 	0, 				canvasHeight);
		drawLinePaint(canvasWidth / 3, 	0, 	canvasWidth / 3, 	canvasHeight);
		drawLinePaint(canvasWidth / 3 * 2, 	0,  canvasWidth / 3 * 2,	canvasHeight);
		drawLinePaint(canvasWidth, 		0,  canvasWidth, 	canvasHeight);
	}
	
	private void drawLinePaint(float startX, float startY, float stopX, float stopY) {
		this.canvas.drawLine(startX, startY, stopX, stopY, this.paint);
	}
	
	private void drawRoundPaint(RectF rect, float startX, float startY, float stopX, float stopY) {
		this.canvas.drawRoundRect(rect, startX, startY, paint);
	}
	
	public void drawLine(float x, float y) {
		bufferX = x;
		bufferY = y;
		invalidate();
	}
	
	public void drawLineCanvas(float x, float y) {
		canvas.drawLine(0, 0, x, y, paint);
	}

	public void setData(GameData data) {
		this.data = data;
	}
}
