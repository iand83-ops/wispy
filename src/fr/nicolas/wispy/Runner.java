package fr.nicolas.wispy;

import fr.nicolas.wispy.panels.GamePanel;

public class Runner implements Runnable {

	private boolean isRunning = false;
	private final GamePanel gamePanel;
	private int maxFps = 125;
	private long waitTime = 8;

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

			long tickTime = System.nanoTime();
			while (isRunning) {
				startTime = System.nanoTime();

				gamePanel.tick((startTime - tickTime) / 1_000_000.0);
				tickTime = System.nanoTime();

				gamePanel.repaint();
				
				differenceTime = System.nanoTime() - startTime;
				waitTime = Math.max(maxFps - differenceTime / 1_000_000, 8);

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
