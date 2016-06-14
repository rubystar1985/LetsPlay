package com.example.letsplay;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Point;

public class GameData {
	
	private ArrayList<Point> stepsPlayer1 = new ArrayList<Point>();
	private ArrayList<Point> stepsPlayer2 = new ArrayList<Point>();
	private Bitmap bmpPlayer1, bmpPlayer2;
	private boolean turn;
	
	public Bitmap getBmpPlayer1() {
		return this.bmpPlayer1;
	}
	
	public Bitmap getBmpPlayer2() {
		return this.bmpPlayer2;
	}
	
	public void setBmpPlayer1(Bitmap bmp) {
		this.bmpPlayer1 = bmp;
	}
	
	public void setBmpPlayer2(Bitmap bmp) {
		this.bmpPlayer2 = bmp;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Point> getStepsPlayer2() {
		return (ArrayList<Point>) stepsPlayer2.clone();
	}
	
	public void addStepPlayer2(int x, int y) {
		this.stepsPlayer2.add(new Point(x,y));
	}
	
	public void addStepPlayer1(int x, int y) {
		this.stepsPlayer1.add(new Point(x,y));
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Point> getStepsPlayer1() {
		return (ArrayList<Point>) stepsPlayer1.clone();
	}
	
	public GameData getInstance() {
		return this;
	}

	public void removeBmpImages() {
		this.bmpPlayer1 = null;
		this.bmpPlayer2 = null;
	}

	public void clearPlayerSteps() {
		this.stepsPlayer1 = new ArrayList<Point>();
		this.stepsPlayer2 = new ArrayList<Point>();
	}
	
	public Players getWinner() {
		if (isWinner(this.stepsPlayer1)) {
			return Players.PLAYER_1;
		} else if (isWinner(this.stepsPlayer2)) {
			return Players.PLAYER_2;
		} else {
			return null;
		}
	}

	private boolean isWinner(ArrayList<Point> stepsPlayer12) {
		int diagonal = 0;
		int diagonalSecond = 0;
		int vertical[] = new int[3];
		int horizontal[] = new int[3];
		for (Point pt : stepsPlayer12) { 
			if (pt.x == pt.y) {
				diagonal++;
			}
			
			if (pt.x == 2 - pt.y) {
				diagonalSecond++;
			}
			
			vertical[pt.x]++;
			horizontal[pt.y]++;
		}
		
		for (int i : vertical) {
			if (i == 3) {
				return true;
			}
		}
		
		for (int i : horizontal) {
			if (i == 3) {
				return true;
			}
		}
		
		if ((diagonal == 3) || (diagonalSecond == 3)) {
			return true;
		}
		
		return false;
	}

	public boolean getTurn() {
		return turn;
	}
	
	public void turnSwitch() {
		if (this.turn) {
			this.turn = false;
		} else {
			this.turn = true;
		}
	}
}
