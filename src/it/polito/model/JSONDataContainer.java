package it.polito.model;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public abstract class JSONDataContainer {
	protected Context context;
	protected String fileName;
	
	protected String readJsonFromFile() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().
                    open(this.fileName)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	protected void writeJsonToFile(String data){
		try{
			FileOutputStream fOut = context.openFileOutput(fileName,  Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.close();
		}catch(IOException e){
			Log.d("JSONDataContainer", "Error writing to JSON file " + e.getMessage());
			System.exit(1);
		}
	}
	
}
