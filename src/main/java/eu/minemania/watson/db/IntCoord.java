package eu.minemania.watson.db;

public class IntCoord {
	protected int _x;
	protected int _y;
	protected int _z;
	
	public IntCoord() {
		
	}
	public IntCoord(IntCoord coord) {
		setXYZ(coord._x, coord._y, coord._z);
	}
	public IntCoord(int x, int y, int z) {
	    setXYZ(x, y, z);
	}
	public void setXYZ(int x, int y, int z) {
		_x = x;
		_y = y;
		_z = z;
	}
	public void setX(int x) {
		_x = x;
	}
	public int getX() {
		return _x;
	}
	public void setY(int y) {
		_y = y;
	}
	public int getY() {
		return _y;
	}
	public void setZ(int z) {
		_z = z;
	}
	public int getZ() {
		return _z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		} else if (obj != null && obj instanceof IntCoord) {
			IntCoord c = (IntCoord) obj;
			return (c._x == _x && c._y == _y && c._z == _z);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (_y << 24) ^ _x ^ (_z << 15);
	}
	
	@Override
	public String toString() {
		return "(" + _x + "," + _y + "," + _z + ")";
	}
}
