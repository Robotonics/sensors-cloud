package com.sorin.cloudcog.xivelypull;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.sorin.cloudcog.R;



public class History extends Activity{

	static ProgressDialog myProgressDialog = null;
	String fID;
	int size;
	
	String data = "";
	String[] tags;

	String[] DID;
	
	int width;
	int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xivelypull_history);

		

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		
		/*SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

		String fID = settings.getString("feed", "0");

		int size = Integer.parseInt(settings.getString("size", "0"));
		String data = "";
		final String[] tags = new String[size];

		String[] DID = new String[size];

		if(!fID.equals("0")) {


			for(int i=0;i<size;i++) {
				DID[i] = settings.getString("D"+i, ""+i);
				tags[i] = settings.getString("Tag"+i, "unknown");
				data +=DID[i]+" --> "+tags[i]+": "+ settings.getString(""+i, "0")+"\r\n";


			}

			Display display = getWindowManager().getDefaultDisplay(); 
			int width = display.getWidth();
			int height = display.getHeight();

			Gallery g = (Gallery) findViewById(R.id.widget66);
			g.setAdapter(new ImageAdapter(History.this, size, fID, DID, width, height));

			g.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v, int position, long id) {
					Toast.makeText(History.this, "" + tags[position], Toast.LENGTH_SHORT).show();
				}
			});
		}*/
		

		Runnable myRunnable= new Runnable(){
			@Override
			public void run(){
				Looper.prepare();
				SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

				fID = settings.getString("feed", "0");

				size = Integer.parseInt(settings.getString("size", "0"));
				String data = "";
				tags = new String[size];

				DID = new String[size];

				if(!fID.equals("0")) {


					for(int i=0;i<size;i++) {
						DID[i] = settings.getString("D"+i, ""+i);
						tags[i] = settings.getString("Tag"+i, "unknown");
						data +=DID[i]+" --> "+tags[i]+": "+ settings.getString(""+i, "0")+"\r\n";


					}

					Display display = getWindowManager().getDefaultDisplay(); 
					width = display.getWidth();
					height = display.getHeight();
					
					handler.sendEmptyMessage(0);

					
				}
				//myProgressDialog.dismiss();
			}
		};

		//myProgressDialog = ProgressDialog.show(History.this,"PachubeViewer", "fetching data...", true);
		
		Thread thread =  new Thread(null, myRunnable, "FetchData");
		thread.start();



	}
	
	
	private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
        	Gallery g = (Gallery) findViewById(R.id.widget66);
			g.setAdapter(new ImageAdapter(History.this, size, fID, DID, width, height));

			g.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v, int position, long id) {
					Toast.makeText(History.this, "" + tags[position], Toast.LENGTH_SHORT).show();
				}
			});
			//History.myProgressDialog.dismiss();
        }
   };


}


class ImageAdapter extends BaseAdapter {
	/** The parent context */
	private Context myContext;
	int count;
	String feed;
	int mGalleryItemBackground;
	String[] DID;
	int width, height;

	/** All images to be displayed.
	 * Put some images to project-folder:
	 * '/res/drawable/uvw.xyz' .*/

	/** Simple Constructor saving the 'parent' context. */
	public ImageAdapter(Context c, int count, String feed, String[] DID, int width, int height) { 
		this.myContext = c; 
		this.count = count;
		this.feed = feed;

		this.width = width;
		this.height = height;

		this.DID = DID;

		TypedArray a = c.obtainStyledAttributes(R.styleable.HelloGallery);
		mGalleryItemBackground = a.getResourceId(R.styleable.HelloGallery_android_galleryItemBackground, 0);
		a.recycle();
	}

	/** Returns the amount of images we have defined. */
	public int getCount() { 
		//return this.myImageIds.length;
		return count; 

	}

	/* Use the array-Positions as unique IDs */
	public Object getItem(int position) { return position; }
	public long getItemId(int position) { return position; }

	/** Returns a new ImageView to
	 * be displayed, depending on
	 * the position passed. */
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(this.myContext);

		//i.setImageResource(this.myImageIds[position]);

		Drawable image = ImageOperations("http://api.pachube.com/v2/feeds/"+feed+"/datastreams/"+DID[position]+".png?width=750&height=240&colour=F15A24&duration=24hours&detailed_grid=true&show_axis_labels=true&timezone=UTC");
		i.setImageDrawable(image);

		/* Image should be scaled as width/height are set. */
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		/* Set the Width/Height of the ImageView. */
		i.setLayoutParams(new Gallery.LayoutParams(width-20, height-80));
		i.setBackgroundResource(mGalleryItemBackground);

		return i;
	}

	/** Returns the size (0.0f to 1.0f) of the views
	 * depending on the 'offset' to the center. */
	public float getScale(boolean focused, int offset) {
		/* Formula: 1 / (2 ^ offset) */
		return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
	}

	private Drawable ImageOperations(String url) {
		try {

			SharedPreferences settings = myContext.getSharedPreferences("MyPrefsFile", 0);
			final String username = settings.getString("username", "0");
			final String password = settings.getString("password", "0");

			Authenticator.setDefault(new Authenticator(){
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username,password.toCharArray());
				}});	        
			HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
			c.setUseCaches(false);
			c.connect();

			InputStream is = c.getInputStream();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}