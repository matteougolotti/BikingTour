package it.polito.bikingtour;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.polito.bikingtour.InfoFragment.ErrorDialogFragment;
import it.polito.model.RequestListener;
import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import it.polito.utils.JSONThread;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class NavigationFragment extends Fragment implements
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private View rootView;
	private Tour currentTour;
	private LocationClient mLocationClient;
	private MapFragment mapFragment;
	private GoogleMap map;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private ImageButton buttonPicture, buttonVideo, buttonHelp;
	private Chronometer chronometer;
	private Date date;
	private LocationManager locationManager;
	private Circle userIcon;
	
	public void onCreate(Bundle savedInstanceState) {
	    setRetainInstance(true); 
	    super.onCreate(savedInstanceState);   
	    ToursContainer toursContainer = ToursContainer.newInstance(getActivity());
	    this.currentTour = toursContainer.getCurrentTour();
	    this.date = new Date();
	    String context = Context.LOCATION_SERVICE; 
	    this.locationManager = (LocationManager)getActivity().getSystemService(context); 
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    try {
	    	this.currentTour.setTourDuration(date.getTime() - currentTour.getTourDate());
	    	chronometer.stop();
	        MapFragment fragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.navigation_mapfragment);
	        if (fragment != null) 
	        	getFragmentManager().beginTransaction().remove(fragment).commit();
	    } catch (IllegalStateException e) {
	    	Log.d("NavigationFragment.onDestroyView", e.getMessage());
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        buttonPicture = (ImageButton) rootView.findViewById(R.id.navigation_button_picture);
        buttonPicture.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new PictureFragment()).commit();	
			}
        });
        buttonVideo = (ImageButton) rootView.findViewById(R.id.navigation_button_video);
        buttonVideo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
					.replace(R.id.frame_container,  new VideoFragment()).commit();
			}
        });
        
        buttonHelp = (ImageButton) rootView.findViewById(R.id.navigation_button_help);
        buttonHelp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				NavigationHelpDialog helpDialog = NavigationHelpDialog.newInstance();
				helpDialog.show(getFragmentManager(), null);
			}
        });
        
        chronometer = (Chronometer) rootView.findViewById(R.id.navigation_chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime() - date.getTime() + currentTour.getTourDate());
        chronometer.start();
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);      
	    
        mLocationClient = new LocationClient(getActivity(), this, this);
        mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.navigation_mapfragment));
        map = mapFragment.getMap();
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
		if(mLocationClient.isConnected())
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
        
        addingMarkers();
		requestDirections();
		
		Criteria crta = new Criteria(); 
		crta.setAccuracy(Criteria.ACCURACY_FINE); 
		crta.setAltitudeRequired(false); 
		crta.setBearingRequired(false); 
		crta.setCostAllowed(true); 
		crta.setPowerRequirement(Criteria.POWER_LOW); 
		String provider = locationManager.getBestProvider(crta, true);
		
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.fillColor(Color.RED);
		circleOptions.radius(2 * this.getScaledPolylineWidth());
 
		Location location = locationManager.getLastKnownLocation(provider);
		circleOptions.center(new LatLng(location.getLatitude(), location.getLongitude()));
		this.userIcon = map.addCircle(circleOptions);
		locationManager.requestLocationUpdates(provider, 1000, 0, locationListener); 
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
	
	private void addingMarkers() {
		LatLng latLng = new LatLng(currentTour.getRoute().getOrigin().getLat(), currentTour.getRoute().getOrigin().getLon());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        map.animateCamera(cameraUpdate);
        
        BitmapDescriptor srcIcon = BitmapDescriptorFactory.fromResource(R.drawable.srcmarker);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(currentTour.getRoute().getOrigin().getLat(), currentTour.getRoute().getOrigin().getLon()))
                .title(currentTour.getRoute().getOrigin().getName())
                .icon(srcIcon));
        
        BitmapDescriptor destIcon = BitmapDescriptorFactory.fromResource(R.drawable.destmarker);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(currentTour.getRoute().getDestination().getLat(), currentTour.getRoute().getDestination().getLon()))
                .title(currentTour.getRoute().getDestination().getName())
                .icon(destIcon));
	}
	
	private void requestDirections() {
		String request = makeURLRequest(Double.toString(currentTour.getRoute().getOrigin().getLat()),
                Double.toString(currentTour.getRoute().getOrigin().getLon()), 
                Double.toString(currentTour.getRoute().getDestination().getLat()), 
                Double.toString(currentTour.getRoute().getDestination().getLon()));
        
        JSONThread jsonThread = new JSONThread(request);
        jsonThread.setRequestListener(new RequestListener() {
			
			@Override
			public void postResponse(String result) {
				drawDirections(result);
				setDuration(result);
				/*try {
					setPlaces(result);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}*/
			}
		});
        jsonThread.execute();
	}
	
	private String makeURLRequest(String srclat, String srclng, String destlat, String destlng) {
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
	
	private void drawDirections(String result) {
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
                map.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(getScaledPolylineWidth())
                        .visible(true)
                        .color(Color.BLUE).geodesic(true));
            }
        }
        catch (JSONException e) {
        	Log.d("NavigationFragment.drawDirections", e.getMessage());
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
	
	private void setDuration(String result) { 
		try {
			String text = null;
			JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
			JSONObject duration = legs.getJSONObject("duration");
			
			for (int i = 0; i < duration.length(); i++) {
				text = duration.getString("text");
			}
			
			//TODO add some text to show tour duration
			//TextView textDuration = (TextView) getActivity().findViewById(R.id.textduration);
			String sDuration = "Estimated duration of the tour: ";
			SpannableString spanDuration = new SpannableString(sDuration);
			spanDuration.setSpan(new StyleSpan(Typeface.BOLD), 0, spanDuration.length(), 0);
			//textDuration.setText(spanDuration + text);
			
		} catch (JSONException e) {
			Log.d("NavigationFragment.setDuration", e.getMessage());
		}	
	}
	
	private int getScaledPolylineWidth(){
		DisplayMetrics metrics = new DisplayMetrics();
	    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if(metrics.densityDpi == DisplayMetrics.DENSITY_LOW)
			return 5;
		else if(metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM)
			return 8;
		else if(metrics.densityDpi == DisplayMetrics.DENSITY_HIGH)
			return 10;
		else if(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
			return 13;
		else if(metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH)
			return 16;
		else
			return 5;
	}
	
	private void updateUserPositionWithNewLocation(Location location){
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		this.userIcon.setCenter(latLng);
	}
	
	private final LocationListener locationListener = new LocationListener(){ 

		@Override 
		public void onLocationChanged(Location location) { 
			updateUserPositionWithNewLocation(location); 
		} 

		@Override 
		public void onProviderDisabled(String provider) { 
			updateUserPositionWithNewLocation(null); 
		} 

		@Override 
		public void onProviderEnabled(String provider) { 
		} 

		@Override 
		public void onStatusChanged(String provider, int status, Bundle extras) { 
		} 

	}; 

	
}
