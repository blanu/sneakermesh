package net.blanu.sneakermesh;

public class AddPhoto extends SneakermeshActivity {
	private Uri imageUri;

	public void takePhoto(View view) {
	    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
	    intent.putExtra(MediaStore.EXTRA_OUTPUT,
	            Uri.fromFile(photo));
	    imageUri = Uri.fromFile(photo);
	    startActivityForResult(intent, TAKE_PICTURE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	    case TAKE_PICTURE:
	        if (resultCode == Activity.RESULT_OK) {
	            Uri selectedImage = imageUri;
	            getContentResolver().notifyChange(selectedImage, null);
	            ImageView imageView = (ImageView) findViewById(R.id.ImageView);
	            ContentResolver cr = getContentResolver();
	            Bitmap bitmap;
	            try {
	                 bitmap = android.provider.MediaStore.Images.Media
	                 .getBitmap(cr, selectedImage);

	                imageView.setImageBitmap(bitmap);
	                Toast.makeText(this, selectedImage.toString(),
	                        Toast.LENGTH_LONG).show();
	            } catch (Exception e) {
	                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
	                        .show();
	                Log.e("Camera", e.toString());
	            }
	        }
	    }
	}
}
