package com.prashantsolanki.cochack;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Prashant on 4/27/2015.
 */
public class Utils {
    public static void addToCache(String data, String tag) {
        //    try {
        Context ctx = AppController.getContext();
        FileOutputStream outputStream;
        try {
            outputStream = ctx.openFileOutput(tag, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("INTERNAL STORAGE WRITE", "ERROR: " + e.getMessage());
        }
           /* ctx.getFilesDir()
            if(!new File(Constant.fullPath).exists())
                new File(Constant.fullPath).mkdir();
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
                    new File(Constant.fullPath, fileName)));
            out.writeUTF(data);
            out.flush();
            out.close();
            Log.d("saveToExternalStorage()", "saved");
        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
        }*/

    }

    public static String getCache(String tag) {
        /*ObjectInputStream in;
        JSONObject json = null;
        String respString;
        try {
            in = new ObjectInputStream(new FileInputStream(new File(
                    Constant.fullPath, fileName)));
            respString = in.readUTF();
            json = new JSONObject(respString);
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;*/
        //}
        Context ctx = AppController.getContext();
        FileInputStream fin;
        int c;
        String temp = "";
        try {
            fin = ctx.openFileInput(tag);
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
//string temp contains all the data of the file.
            fin.close();
            return temp;

        } catch (Exception e) {
            Log.d("INTERNAL STORAGE READ","ERROR: "+e.getMessage());
            return null;
        }
    }

}
