package fr.nicolas.wispy;

import fr.nicolas.wispy.panels.GamePanel;

public class Runner implements Runnable {

	private boolean isRunning = false;
	private final GamePanel gamePanel;
	private int maxFps = 80;
	private long waitTime = 4;

	public Runner(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		start();
	}

	private void start() {
		isRunning = true;
		new Thread(this).start();
	}

	public void run() {
		while (isRunning) {
			long startTime, differenceTime;
			maxFps = 1000 / maxFps;

			while (isRunning) {
				startTime = System.nanoTime();

				gamePanel.refresh();
				gamePanel.repaint();
				
				differenceTime = System.nanoTime() - startTime;
				waitTime = Math.max(maxFps - differenceTime / 1000000, 4);

				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public long getWaitTime() {
		return waitTime;
	}
	
}
