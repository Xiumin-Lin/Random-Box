package fr.iut.random_box;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoadImage extends AsyncTask<String, Void, Bitmap> {
    private final ImageView imgView;

    public LoadImage(ImageView imgContent) {
        this.imgView = imgContent;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.d("rb","LoadImage Error" + e.getMessage());
        }
        Log.d("rb", "Image H = " + bitmap.getHeight() + " & W = " + bitmap.getWidth());
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imgView.setImageBitmap(bitmap);
    }
}
