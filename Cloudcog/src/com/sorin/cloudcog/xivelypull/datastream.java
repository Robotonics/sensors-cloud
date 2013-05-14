package com.sorin.cloudcog.xivelypull;

import java.util.List;

public class datastream {
	private String[] tags;
	
	private Unit unit;
	
	private String current_value;
	private String id;
	private String max_value;
	private String min_value;
	
	public String[] getTags() {
		return tags;
	}
	
	public Unit getUnits() {
		return unit;
	}
	
	
	public String getCurrent_Value() {
		return current_value;
	}
	
	public String getMax_Value() {
		return max_value;
	}
	
	public String getMin_Value() {
		return min_value;
	}
	
	public String getID() {
		return id;
	}

}

