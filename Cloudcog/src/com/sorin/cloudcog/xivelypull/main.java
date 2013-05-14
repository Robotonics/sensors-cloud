package com.sorin.cloudcog.xivelypull;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.sorin.cloudcog.R;

public class main extends TabActivity {
	TabHost tabHost;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources();
        Bundle extras = getIntent().getExtras(); 
        String feed  = extras.getString("feed");
        boolean live = extras.getBoolean("live");
        
        tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        
        
        
     // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FeedData.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        if(live) {
        	spec = tabHost.newTabSpec("Feed").setIndicator("Feed #"+feed, res.getDrawable(R.drawable.live)).setContent(intent);
        	tabHost.addTab(spec);
        }
        
        else {
        	spec = tabHost.newTabSpec("Feed").setIndicator("Feed #"+feed, res.getDrawable(R.drawable.frozen)).setContent(intent);
        	tabHost.addTab(spec);
        }
        
        
        intent = new Intent().setClass(this, History.class);
        spec = tabHost.newTabSpec("Data History Graph").setIndicator("Data History Graph",
                          res.getDrawable(R.drawable.history))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        
        
        intent = new Intent().setClass(this, Map.class);
        spec = tabHost.newTabSpec("Feed Map Location").setIndicator("Feed Map Location",
                          res.getDrawable(R.drawable.google_maps_icon))
                      .setContent(intent);
        tabHost.addTab(spec);

    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println(tabHost.getCurrentTabTag());
        //if(tabHost.getCurrentTab()==1 || tabHost.getCurrentTab()==3)
        //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    
    @Override 
    protected void onRestoreInstanceState(Bundle savedInstanceState) { 
      super.onRestoreInstanceState(savedInstanceState); 
      System.out.println(tabHost.getCurrentTab());
      System.out.println(tabHost.getCurrentTabTag());
      //if(tabHost.getCurrentTab()==1 || tabHost.getCurrentTab()==3)
      	//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    
   /* public InputStream getJSONData(String url){
        
    	InputStream data = null;
    	try {
        Authenticator.setDefault(new Authenticator(){
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication("harisdmac","asterias".toCharArray());
		    }});
		HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
		c.setUseCaches(false);
		c.connect();
		
		data = c.getInputStream();
    	} catch (Exception e) {
            e.printStackTrace();
        }
        
        return data;
    }
    
    
    public void runJSONParser(){
        try{
        Log.i("MY INFO", "Json Parser started..");
        Gson gson = new Gson();
        Reader r = new InputStreamReader(getJSONData("http://api.pachube.com/v2/feeds/28602.json"));
        
        EEML objs = gson.fromJson(r, EEML.class);
        Log.i("MY INFO", ""+objs.getDescription());
        Log.i("MY INFO", ""+objs.getLocation().getDomain());
        
        Log.i("MY INFO", ""+objs.getStatus());
        Log.i("MY INFO", ""+objs.getUpdated());
        Log.i("MY INFO", ""+objs.getDatastreams().size());
        for(datastream dt : objs.getDatastreams()){
            Log.i("TRENDS", dt.getCurrent_Value() + " - " + dt.getUnits().symbol);
        }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }*/
}