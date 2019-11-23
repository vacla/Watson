package eu.minemania.watson.db;

import fi.dy.masa.malilib.util.Color4f;

public final class WatsonBlock {
	private String name = "";
	private Color4f color = new Color4f(255,255,0,255);
	private float lineWidth = 3.0f;
	/*private float x1 = 0.0f;
	private float x2 = 1.0f;
	private float y1 = 0.0f;
	private float y2 = 1.0f;
	private float z1 = 0.0f;
	private float z2 = 1.0f;*/
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setColor(Color4f color) {
		this.color = color;
	}
	
	public Color4f getColor() {
		return this.color;
	}
	
	/*public void setBounds(float x1, float y1, float z1, float x2, float y2, float z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public float getX1() {
		return this.x1;
	}
	
	public float getY1() {
		return this.y1;
	}
	
	public float getZ1() {
		return this.z1;
	}
	
	public float getX2() {
		return this.x2;
	}
	
	public float getY2() {
		return this.y2;
	}
	
	public float getZ2() {
		return this.z2;
	}*/
	
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public float getLineWidth() {
		return this.lineWidth;
	}
	//overlayreducedinnersides, schematicoverlayenableoutlines, 
	//schematicoverlayenablesides, schematicoverlaymodeloutline, schematicoverlaymodelsides
	//OVERLAY_REDUCED_INNER_SIDES OK
	//SCHEMATIC_OVERLAY_ENABLE_OUTLINES OK
	//SCHEMATIC_OVERLAY_ENABLE_SIDES OK
	//SCHEMATIC_OVERLAY_MODEL_OUTLINE OK
	//SCHEMATIC_OVERLAY_MODEL_SIDES OK
	//drawBlockModelOutlinesBatched, renderModelQuadOutlines, renderQuadOutlinesBatched
	
	/*
	 * returns a String in chat will only be available for debugging
	 */
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(getName());
		builder.append(" (");
		/*builder.append(getId());
		builder.append(':');
		builder.append(getData());*/
		builder.append(") [");
		builder.append(getColor().r);
		builder.append(',');
		builder.append(getColor().g);
		builder.append(',');
		builder.append(getColor().b);
		builder.append(',');
		builder.append(getColor().a);
		builder.append("] ");
		builder.append(getLineWidth());
		/*builder.append(", (");
		builder.append(getX1());
		builder.append(',');
		builder.append(getY1());
		builder.append(',');
		builder.append(getZ1());
		builder.append(") - (");
		builder.append(getX2());
		builder.append(',');
		builder.append(getY2());
		builder.append(',');
		builder.append(getZ2());*/
		builder.append(')');
		return builder.toString();
	}
	
	@Override
    public int hashCode() {
        return this.name.hashCode();
    }
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}

		if(obj instanceof WatsonBlock) {
		   return ((WatsonBlock)obj).name.equals(this.name);
		}

		return false;
	}
}
