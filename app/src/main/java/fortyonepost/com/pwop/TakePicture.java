package fortyonepost.com.pwop;//Created by DimasTheDriver Mar/2011 - Part of "Android: take a picture without displaying a preview" post. Available at: http://www.41post.com/?p=3794 .

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class TakePicture extends Activity implements SurfaceHolder.Callback
{
	public final static String APP_PATH_SD_CARD = "/pwp/";

	//a variable to store a reference to the Image View at the main.xml file
	private ImageView iv_image;
	//a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView sv;
    
    //a bitmap to display the captured image
	private Bitmap bmp;
	
	//Camera variables
	//a surface holder
	private SurfaceHolder sHolder;  
	//a variable to control the camera
	private Camera mCamera;
	//the camera parameters
	private Parameters parameters;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //get the Image View at the main.xml file
        iv_image = (ImageView) findViewById(R.id.imageView);
        
        //get the Surface View at the main.xml file
        sv = (SurfaceView) findViewById(R.id.surfaceView);
        
        //Get a surface
        sHolder = sv.getHolder();
        
        //add the callback interface methods defined below as the Surface View callbacks
        sHolder.addCallback(this);
        
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) 
	{
		 //get camera parameters
		 parameters = mCamera.getParameters();
		 
		 //set camera parameters
	     mCamera.setParameters(parameters);
	     mCamera.startPreview();
	     
	     //sets what code should be executed after the picture is taken
	     Camera.PictureCallback mCall = new Camera.PictureCallback() 
	     {
	    	 @Override
	    	 public void onPictureTaken(byte[] data, Camera camera) 
	    	 {
	    		 //decode the data obtained by the camera into a Bitmap
	    		 bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
	    		 //set the iv_image
	    		// iv_image.setImageBitmap(bmp);

				 String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath();

				 try {
					 File dir = new File(fullPath);
					 if (!dir.exists()) {
						 dir.mkdirs();
					 }

					 OutputStream fOut = null;
					 File file = new File(fullPath, "img.jpg");
					 file.createNewFile();
					 fOut = new FileOutputStream(file);

					 Bitmap bmp_m = bmp.createScaledBitmap(bmp, 320,
							 240, false);
// 100 means no compression, the lower you go, the stronger the compression
					 bmp_m.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
					 fOut.flush();
					 fOut.close();



				 } catch (Exception e) {
					 Log.e("saveToExternalStorage()", e.getMessage());
					 				 }


	    	 }


	     };
	     
	     mCamera.takePicture(null, null, mCall);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		// The Surface has been created, acquire the camera and tell it where
        // to draw the preview.
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
           
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
	}



	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		//stop the preview
		mCamera.stopPreview();
		//release the camera
        mCamera.release();
        //unbind the camera from this object
        mCamera = null;
	}  
}