package fr.nicolas.wispy;

import fr.nicolas.wispy.game.Game;

public class Runner implements Runnable {

	private final Game game;
	private final int maxFps = 125;
	private long waitTime = 8;

	public Runner(Game game) {
		this.game = game;
		start();
	}

	private void start() {
		new Thread(this).start();
	}

	public void run() {
		int frameTime = 1000 / maxFps;

		long startTime;
		long delta;
		long tickTime = System.nanoTime();
		while (game.isRunning()) {
			startTime = System.nanoTime();

			game.tick((startTime - tickTime) / 1_000_000.0);
			tickTime = System.nanoTime();

			game.getRendererScreen().repaint();

			delta = System.nanoTime() - startTime;
			waitTime = Math.max(frameTime - delta / 1_000_000, 8);

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
