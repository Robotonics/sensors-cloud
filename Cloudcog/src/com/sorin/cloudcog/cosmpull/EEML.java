package com.sorin.cloudcog.cosmpull;

import java.util.List;

public class EEML {
	private String description;
	private Location location;
	private String status;
	private String updated;
	private String website;
	
	private List<datastream> datastreams;
    
    public String getDescription() {
        return description;
    }
    
    
    
    public String getStatus() {
        return status;
    }
    
    public String getUpdated() {
        return updated;
    }
    
    public Location getLocation() {
    	return location;
    }
    
    
    public List<datastream> getDatastreams() {
        return datastreams;
    }
    

}
