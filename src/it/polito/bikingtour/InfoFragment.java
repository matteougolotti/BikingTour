package it.polito.bikingtour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.polito.model.RequestListener;
import it.polito.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Toast;

public class InfoFragment extends Fragment implements
	GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
	private MapFragment mapFragment;
    private GoogleMap map;
    private LocationClient mLocationClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private it.polito.model.Location locOrigin, locDestination;
    private String urlChart = null;
    
	public InfoFragment() {
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
	    setRetainInstance(true); 
	    super.onCreate(savedInstanceState);     
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    try {
	        MapFragment fragment = (MapFragment) getActivity()
	                                          .getFragmentManager().findFragmentById(
	                                              R.id.mapfragment);
	        if (fragment != null) getFragmentManager().beginTransaction().remove(fragment).commit();

	    } catch (IllegalStateException e) {
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        Bundle bundle = this.getArguments();
        /*if (bundle != null && bundle.containsKey("origin") && bundle.containsKey("destination")) {
            String origin = bundle.getString("origin");
            String destination = bundle.getString("destination");
            
            locOrigin = new it.polito.model.Location(origin);
            locDestination = new it.polito.model.Location(destination);
            
            setLatLong();
            
            //TODO assign valid location to origin and destination based on name.
            
            mLocationClient = new LocationClient(getActivity(), this, this);
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapfragment);
            map = mapFragment.getMap();

        }*/
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);      
	    
	    locOrigin = new it.polito.model.Location("Corso XI Febbraio");
        locDestination = new it.polito.model.Location("Politecnico di Torino");
        
        setLatLong();
        
        //TODO assign valid location to origin and destination based on name.
        
        mLocationClient = new LocationClient(getActivity(), this, this);
        mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapfragment));
        map = mapFragment.getMap();
	    
	    String url = "http://chart.apis.google.com/chart?cht=p3&chs=500x200&chd=e:TNTNTNGa&chts=000000,16&chtt=A+Better+Web&chl=Hello|Hi|anas|Explorer&chco=FF5533,237745,9011D3,335423&chdl=Apple|Mozilla|Google|Microsoft";
	       
	    WebView mCharView = (WebView) getActivity().findViewById(R.id.char_view);
        mCharView.loadUrl(url);
        
        //map = mapFragment.getMap();
	}

	@Override
    public void onStart() {
        super.onStart();
        if(isGooglePlayServicesAvailable()){
            mLocationClient.connect();
        }
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Sorry. Location services are not available to you", Toast.LENGTH_LONG).show();
        }
	}

	@Override
    public void onConnected(Bundle dataBundle) {
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
       
        LatLng latLng = new LatLng(locOrigin.getLat(), locOrigin.getLon());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        map.animateCamera(cameraUpdate);
        
        BitmapDescriptor srcIcon = BitmapDescriptorFactory.fromResource(R.drawable.srcmarker);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(locOrigin.getLat(), locOrigin.getLon()))
                .title(locOrigin.getName())
                .icon(srcIcon));
        
        BitmapDescriptor destIcon = BitmapDescriptorFactory.fromResource(R.drawable.destmarker);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(locDestination.getLat(), locDestination.getLon()))
                .title(locDestination.getName())
                .icon(destIcon));
        
        String request = makeURLRequest(Double.toString(locOrigin.getLat()),
                Double.toString(locOrigin.getLon()), 
                Double.toString(locDestination.getLat()), 
                Double.toString(locDestination.getLon()));
        
        JSONThread jsonThread = new JSONThread(request);
        jsonThread.setRequestListener(new RequestListener() {
			
			@Override
			public void postResponse(String result) {
				drawDirections(result);
			}
		});
        jsonThread.execute();
        
        String requestElevation = makeURLElevationsRequest(Double.toString(locOrigin.getLat()), 
        		Double.toString(locOrigin.getLon()), 
        		Double.toString(locDestination.getLat()), 
        		Double.toString(locDestination.getLon()));
        
//        JSONThread elevationJsonThread = new JSONThread(requestElevation);
//        elevationJsonThread.setRequestListener(new RequestListener() {
//			@Override
//			public void postResponse(String result) {
//				urlChart = result;
//			}
//		});
//        elevationJsonThread.execute();
        
    }

	public class JSONThread extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        private RequestListener requestListener;
        String url;

        public JSONThread(String urlPass){
            this.url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
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
            progressDialog.hide();
            progressDialog.dismiss();
            if(result != null){
            	requestListener.postResponse(result);
            }
        }
        
        public void setRequestListener(RequestListener requestListener) {
        	this.requestListener = requestListener;
        }
    }
	
	public void setLatLong() {
		GeoPoint pointOrigin = convertToLatLong(locOrigin.getName());
		GeoPoint pointDestination = convertToLatLong(locDestination.getName());
		locOrigin.setLat(pointOrigin.getLatitudeE6()/1E6);
		locOrigin.setLon(pointOrigin.getLongitudeE6()/1E6);
		locDestination.setLat(pointDestination.getLatitudeE6()/1E6);
		locDestination.setLon(pointDestination.getLongitudeE6()/1E6);
	}
	
	public GeoPoint convertToLatLong(String address) {
		 Geocoder coder = new Geocoder(getActivity());
		 List<Address> listAddress;
		 try {
			 listAddress = coder.getFromLocationName(address, 5);
			 //TODO handle possible error situation. Method should not return null
		     if (listAddress == null)
		         return null;			
		     if(listAddress.isEmpty())
		    	 return null;			
		     
		     Address location = listAddress.get(0);
		     location.getLatitude();
		     location.getLongitude();

		     GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
		                       (int) (location.getLongitude() * 1E6));
		     return point;
		 } catch (IOException e) {
			 e.printStackTrace();
			 System.exit(0);
		}
		
		return null;
	}
	
	public String makeURLElevationsRequest(String srclat, String srclng, String destlat, String destlng) {
		StringBuilder url =  new StringBuilder();
        url.append("http://maps.googleapis.com/maps/api/elevation/json");
        url.append("?path=");
        url.append(srclat);
        url.append(",");
        url.append(srclng);
        url.append("|");
        url.append(destlat);
        url.append(",");
        url.append(destlng);
        url.append("&samples=7&sensor=true_or_false");
        return url.toString();
	}
	
	public String makeURLRequest(String srclat, String srclng, String destlat, String destlng) {
        StringBuilder url =  new StringBuilder();
        url.append("http://maps.googleapis.com/maps/api/directions/json");
        url.append("?origin=");
        url.append(srclat);
        url.append(",");
        url.append(srclng);
        url.append("&destination=");
        url.append(destlat);
        url.append(",");
        url.append(destlng);
        url.append("&sensor=false&mode=walking&alternatives=true");
        return url.toString();
    }

	
	@Override
	public void onDisconnected() {
		Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
	}
	
	private boolean isGooglePlayServicesAvailable() {
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(), "Location Updates");
            }

            return false;
        }
    }
	
	public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    public void drawDirections(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z < list.size() - 1; z++){
                LatLng src = list.get(z);
                LatLng dest = list.get(z+1);
                Polyline line = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(10)
                        .visible(true)
                        .color(Color.BLUE).geodesic(true));
            }

        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
	
}
