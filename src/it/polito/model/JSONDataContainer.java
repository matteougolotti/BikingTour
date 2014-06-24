package it.polito.model;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public abstract class JSONDataContainer {
	protected static Context context;
	
	protected String readJsonFromFile(String fileName) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().
                    open(fileName)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("JSONDataContainer", e.getMessage());
            System.exit(1);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("JSONDataContainer", e.getMessage());
            }
        }
        return sb.toString();
    }
	
	protected void writeJsonToFile(String data, String fileName){
		try{
			FileOutputStream fOut = context.openFileOutput(fileName,  Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.close();
		}catch(IOException e){
			Log.d("JSONDataContainer", e.getMessage());
			System.exit(1);
		}
	}
	
}