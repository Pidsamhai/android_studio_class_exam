package com.example.giftshop;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompressBitmapTask extends AsyncTask<Void, Void, Byte[]> {

    private static String TAG = "CompressBitmapTask";

    private Uri mUri;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private byte[] file_data;
    private Exception exception;


    public interface onSuccessListener {
        void onSuccess(byte[] img_bitmap);
    }

    public interface onFailureListener {
        void onFailure(Exception e);
    }

    onSuccessListener onsuccessListener = null;
    onFailureListener onfailureListener = null;

    public CompressBitmapTask(Context context, Uri uri, onSuccessListener onsuccessListener) {
        this.onsuccessListener = onsuccessListener;
        this.mUri = uri;
        this.mContext = context;
    }
    public CompressBitmapTask(Context context, Uri uri, onSuccessListener onsuccessListener,onFailureListener onfailureListener) {
        this.onsuccessListener = onsuccessListener;
        this.onfailureListener = onfailureListener;
        this.mUri = uri;
        this.mContext = context;
    }

    @Override
    protected Byte[] doInBackground(Void... voids) {
        Bitmap bitmap = null;
        double size = 0.00;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mUri);
            ContentResolver c = mContext.getContentResolver();
            InputStream is = c.openInputStream(mUri);
            size = is.available() / 1024;
            //Log.e(TAG, "File Size before raw bitmap " + bitmap.getByteCount());
            Log.i(TAG, "File Size before compress " + String.format(size >= 1024 ? "%.2f MB" : "%.2f KB", size >= 1024 ? size / 1024 : size));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error Cant get File Size ");
            this.exception = e;
            return null;
        }

        if (size >= 1024 && size <= 1536) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        }

        this.file_data = baos.toByteArray();

        return null;
    }

    @Override
    protected void onPostExecute(Byte[] aByte) {
        if(exception != null){
            onfailureListener.onFailure(exception);
            return;
        }
        int file_size = file_data.length / 1024;
        Log.i(TAG, String.format(file_size >= 1024 ? "File Size after compress %d MB" : "File Size after compress %d KB", file_size >= 1024 ? file_size / 1024 : file_size));
        onsuccessListener.onSuccess(file_data);
    }
}
