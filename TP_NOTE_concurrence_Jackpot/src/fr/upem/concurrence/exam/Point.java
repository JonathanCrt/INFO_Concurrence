package fr.upem.concurrence.exam;

public class Point {
	private int x;
	private int y;
	private final Object lock = new Object();

	public Point(int x, int y) {
		synchronized (lock) {
			this.x = x;
			this.y = y;
		}
	}

	public boolean isOrigin() {
		synchronized (lock) {
			return x == 0 && y == 0;
		}
		
	}

	public void addX(int dx) {
		synchronized (lock) {
			x = x + dx;
		}
	}

	public void addY(int dy) {
		synchronized (lock) {
			y = y + dy;
		}
	}

	public void addXY(int dx, int dy) {
		synchronized (lock) {
			x = x + dx;
			y = y + dy;
		}
	}

	/*
	 * public int getX() { synchronized (lock) { return x; } }
	 * 
	 * public int getY() { synchronized (lock) { return y; } }
	 */

	@Override
	public String toString() {
		synchronized (lock) {
			return "(" + x + "," + y + ")";
		}
	}

	public void display() {
		synchronized (lock) {
			if (this.x != this.y) {
				System.out.println("C'est étrange, non?");
			}
		}
	}

	public static void main(String[] args) {
		Point p = new Point(0, 0);
		var lmainLock = new Object();
		new Thread(() -> {
			for (;;) {
				p.addXY(1, 1);
			}
		}).start();

		new Thread(() -> {
			for (;;) {
					p.addXY(-1, -1);
			}
		}).start();

		for (;;) {
			p.display();
			p.displayOrigin();
		}
	}

	private void displayOrigin() {
		synchronized (lock) {
			if (x == 0 && y == 0) {
				System.out.println("Retour à la case départ : " + this);
			}
		}
	}
}
