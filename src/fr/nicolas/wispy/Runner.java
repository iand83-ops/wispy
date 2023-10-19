package fr.nicolas.wispy;

import fr.nicolas.wispy.game.Game;

public class Runner implements Runnable {

	private boolean isRunning = false;
	private final Game game;
	private int maxFps = 125;
	private long waitTime = 8;

	public Runner(Game game) {
		this.game = game;
		start();
	}

	private void start() {
		isRunning = true;
		new Thread(this).start();
	}

	public void run() {
		long startTime;
		long delta;
		maxFps = 1000 / maxFps;

		long tickTime = System.nanoTime();
		while (isRunning) {
			startTime = System.nanoTime();

			game.tick((startTime - tickTime) / 1_000_000.0);
			tickTime = System.nanoTime();

			game.getRendererScreen().repaint();

			delta = System.nanoTime() - startTime;
			waitTime = Math.max(maxFps - delta / 1_000_000, 8);

			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public long getWaitTime() {
		return waitTime;
	}
	
}
