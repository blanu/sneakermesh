package net.blanu.sneakermesh;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddPhotoActivity extends SneakermeshActivity {
	private Uri imageUri;
	private static final int TAKE_PICTURE=0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.add_photo);        
        
        final Button button = (Button) findViewById(R.id.takephoto);
        button.setOnClickListener(new ClickListener());
    }
    
    private class ClickListener implements View.OnClickListener
    {
        public void onClick(View v) {
        	try
        	{
        		takePhoto(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }    	
    }
		
	public void takePhoto(View view) {
	    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    File photo = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),  "Pic.jpg");
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
	    imageUri = Uri.fromFile(photo);
	    startActivityForResult(intent, TAKE_PICTURE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	    case TAKE_PICTURE:
	    	log("take picture");
	        if (resultCode == Activity.RESULT_OK) {
	        	log("resultCode: "+resultCode);
	            Uri selectedImage = imageUri;
	            log("uri: "+selectedImage);
	            getContentResolver().notifyChange(selectedImage, null);
	            ImageView imageView = (ImageView) findViewById(R.id.imageview);
	            log("imageView: "+imageView);
	            ContentResolver cr = getContentResolver();
	            Bitmap bitmap;
	            try {
	                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
	                imageView.setImageBitmap(bitmap);
	                Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
	            } catch (Exception e) {
	                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
	                Log.e("Camera", e.toString());
	            }
	        }
	    }
	}
}
