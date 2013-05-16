package com.sysfeather.together.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public class FileUtil {
    private static final String TAG = "FileUtil";
    public static String bitmap2base64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
        byte[] b = baos.toByteArray();
        String result = Base64.encodeToString(b, Base64.DEFAULT);
        try {
            baos.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }
    
    public static String genFileName(String ext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss", Locale.TAIWAN);
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + "." + ext;
        return photoFile;
    }
    
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    
    public static Bitmap compressImage(Uri uri, int width, int height) {
        BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
        factoryOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), factoryOptions);
        // 取得原圖的長和寬
        int imageWidth = factoryOptions.outWidth;
        int imageHeight = factoryOptions.outHeight;
        Log.d(TAG, imageWidth + " x " + imageHeight);
        int scaleFactor = Math.min(imageWidth / width, imageHeight / height);
        Log.d(TAG, "scale:" + scaleFactor);
        factoryOptions.inJustDecodeBounds = false;
        factoryOptions.inSampleSize = scaleFactor;
        factoryOptions.inPurgeable = true;              
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), factoryOptions);
        return bitmap;
    }
    
    public static String uploadFile(String sourceFileUri, String serverUri) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        String fileName = sourceFile.getName();
        Log.d(TAG, fileName);
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File Does not exist");
            return "";
        }
            try { // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(serverUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploadd_file", fileName); 
                dos = new DataOutputStream(conn.getOutputStream());
   
                dos.writeBytes(twoHyphens + boundary + lineEnd); 
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
   
                bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
   
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
   
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
               
             while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
             }
   
             // send multipart form data necesssary after file data...
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
   
             // Responses from the server (code and message)
             //serverResponseCode = conn.getResponseCode();
             BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String line;
             while ((line = br.readLine()) != null) {
                 result.append(line).append("\n");
             }
             br.close();
              
             Log.d(TAG, "result : " + result.toString());   
             
             //close the streams //
             fileInputStream.close();
             dos.close();
              
        } catch (MalformedURLException ex) {  
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
        } catch (Exception e) {
            Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
        }     
        return result.toString();  
       }
}
