package it.polito.utils;

import it.polito.model.RequestListener;
import android.os.AsyncTask;

public class JSONThread extends AsyncTask<Void, Void, String> {
    private RequestListener requestListener;
    String url;

    public JSONThread(String urlPass){
        this.url = urlPass;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... params) {
        JSONParser jParser = new JSONParser();
        String json = jParser.getJSONFromUrl(url);
        return json;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result != null){
        	requestListener.postResponse(result);
        }
    }
    
    public void setRequestListener(RequestListener requestListener) {
    	this.requestListener = requestListener;
    }
    
}
