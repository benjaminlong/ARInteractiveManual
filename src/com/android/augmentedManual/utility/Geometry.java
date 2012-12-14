package com.android.augmentedManual.utility;

// ----------------------------------------------------------------------------
public class Geometry {
	
	private String 	Name;
	private Float[] Rotation = null;
	private Float[] Translation = null;
	private Float[] Scale = null;
	
	// ------------------------------------------------------------------------
	public Geometry() {
		// Init variables
		this.Name = "";
		this.Rotation = new Float[3];
		this.Translation = new Float[3];
		this.Scale = new Float[3];
	}
	
	// ------------------------------------------------------------------------
	public void setGeometryName(String newName) {
		this.Name = newName;
	}
	
	// ------------------------------------------------------------------------
	public String getGeometryName() {
		return this.Name;
	}
	
	// ------------------------------------------------------------------------
	public Boolean setRotation(Float[] newRotation) {
		if (newRotation.length != 3) {
			return false;
		}
		this.Rotation = newRotation;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public Boolean setRotation(Float x, Float y, Float z) {
		this.Rotation[0] = x;
		this.Rotation[1] = y;
		this.Rotation[2] = z;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public Float[] getRotation() {
		return this.Rotation;
	}
	
	// ------------------------------------------------------------------------
	public Boolean setTranslation(Float[] newTranslation) {
		if (newTranslation.length != 3) {
			return false;
		}
		this.Translation = newTranslation;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public Boolean setTranslation(Float x, Float y, Float z) {
		this.Translation[0] = x;
		this.Translation[1] = y;
		this.Translation[2] = z;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public Float[] getTranslation() {
		return this.Translation;
	}
	
	// ------------------------------------------------------------------------
	public Boolean setScale(Float[] newScale) {
		if (newScale.length != 3) {
			return false;
		}
		this.Scale = newScale;
		return true;
	}
	
	public Boolean setScale(Float x, Float y, Float z) {
		this.Scale[0] = x;
		this.Scale[1] = y;
		this.Scale[2] = z;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public Float[] getScale() {
		return this.Scale;
	}
}
