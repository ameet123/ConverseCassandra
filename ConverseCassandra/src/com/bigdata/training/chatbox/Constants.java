package com.bigdata.training.chatbox;

import java.awt.Dimension;

public class Constants {
	/**
	 * width of the conversation window
	 */
	public static final int WINDOW_WIDTH = 1000;
	/**
	 * Height of the conversation window
	 */
	public static final int WINDOW_HEIGHT = 500;
	/**
	 * each speaker pane HEIGHT
	 */
	public static final int ONE_PANE_HEIGHT = (int) (0.85*WINDOW_HEIGHT);
	/**
	 * each speaker pane width
	 */
	public static final int ONE_PANE_WIDTH = (int) (0.48*WINDOW_WIDTH);
	/**
	 * Preferred dimension for each message Pane
	 */
	public static final Dimension PANE_DIMENSION = new Dimension(ONE_PANE_WIDTH, ONE_PANE_HEIGHT);
	/**
	 * dimensions for the label of each pane window
	 */
	public static final Dimension PANE_LABEL_DIMENSION = new Dimension(ONE_PANE_WIDTH, 20);
	
}
