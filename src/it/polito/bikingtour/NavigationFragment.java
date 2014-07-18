package it.polito.bikingtour;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.polito.bikingtour.InfoFragment.ErrorDialogFragment;
import it.polito.model.RequestListener;
import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import it.polito.utils.JSONThread;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class NavigationFragment extends Fragment implements
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private View rootView;
	private Tour tour;
	private LocationClient mLocationClient;
	private MapFragment mapFragment;
	private GoogleMap map;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	public void onCreate(Bundle savedInstanceState) {
	    setRetainInstance(true); 
	    super.onCreate(savedInstanceState);   
	    ToursContainer toursContainer = ToursContainer.newInstance(getActivity());
	    RoutesContainer routesContainer = RoutesContainer.newInstance(getActivity());
	    Route route = routesContainer.getRoute(getArguments().getLong("routeId"));
	    toursContainer.CreateNewTour(route);
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    try {
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
		LatLng latLng = new LatLng(tour.getRoute().getOrigin().getLat(), tour.getRoute().getOrigin().getLon());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        map.animateCamera(cameraUpdate);
        
        BitmapDescriptor srcIcon = BitmapDescriptorFactory.fromResource(R.drawable.srcmarker);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(tour.getRoute().getOrigin().getLat(), tour.getRoute().getOrigin().getLon()))
                .title(tour.getRoute().getOrigin().getName())
                .icon(srcIcon));
        
        BitmapDescriptor destIcon = BitmapDescriptorFactory.fromResource(R.drawable.destmarker);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(tour.getRoute().getDestination().getLat(), tour.getRoute().getDestination().getLon()))
                .title(tour.getRoute().getDestination().getName())
                .icon(destIcon));
	}
	
	private void requestDirections() {
		String request = makeURLRequest(Double.toString(tour.getRoute().getOrigin().getLat()),
                Double.toString(tour.getRoute().getOrigin().getLon()), 
                Double.toString(tour.getRoute().getDestination().getLat()), 
                Double.toString(tour.getRoute().getDestination().getLon()));
        
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
                        .width(10)
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
	
}
