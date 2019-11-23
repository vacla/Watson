package eu.minemania.watson.db;

import eu.minemania.watson.render.OverlayRenderer;

public class Annotation {
	protected String _text;
	protected int _x;
	protected int _y;
	protected int _z;
	
	public Annotation(int x, int y, int z, String text) {
		_text = text;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public String getText() {
		return _text;
	}
	
	public int getX() {
		return _x;
	}
	
	public int getY() {
		return _y;
	}
	
	public int getZ() {
		return _z;
	}
	
	public void draw(double dx, double dy, double dz) {
		OverlayRenderer.drawBillboard(getX() - dx, getY() - dy, getZ() - dz, 0.03, getText());
	}
}
