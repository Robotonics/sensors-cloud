
package com.sorin.cloudcog.car.obd2;

import java.util.Arrays;

import android.app.Application;

public class Obd2Monitor extends Application {
	
	String[] Arraydata= new String[100];
	
	public String getArraydata(int i){
	    return Arraydata[i];
	  }
	
	  public void setArraydata(String s, int i){
	    Arraydata[i] = s;
	  }
	  
	  public void clearArraydata() {
		  
		  Arrays.fill(Arraydata, null);
	  }
	
}

