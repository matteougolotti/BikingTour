package it.polito.bikingtour;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.polito.adapter.LazyAdapter;
import it.polito.model.Location;
import it.polito.model.RequestListener;
import it.polito.utils.JSONParser;
import it.polito.utils.JSONThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class InfoFragment extends Fragment implements
	GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
	private MapFragment mapFragment;
    private GoogleMap map;
    private LocationClient mLocationClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private it.polito.model.Location locOrigin, locDestination;
    private View rootView;
    private TabHost tabHost;
	private TabSpec specMap, specSupport, specInfo;
	private ArrayList<Location> places;
	private ImageView chart;
	private TextView difficulty;
	private ListView listPlaces;
	private String origin, destination;
	private List<String> locationsPlaces = null;
	private ProgressBar mProgressChart, mProgressList = null;
    
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
	        MapFragment fragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapfragment);
	        if (fragment != null) getFragmentManager().beginTransaction().remove(fragment).commit();

	    } catch (IllegalStateException e) {
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.tabhostinfo);
        tabHost.setup();
        
        specMap = tabHost.newTabSpec("Map");
        specMap.setContent(R.id.tabmap);
        specMap.setIndicator("Map");
        
        specInfo = tabHost.newTabSpec("Info");
        specInfo.setContent(R.id.tabInfo);
        specInfo.setIndicator("Info");
        
        specSupport = tabHost.newTabSpec("Support");
        specSupport.setContent(R.id.tabSupport);
        specSupport.setIndicator("Support");
        
        tabHost.addTab(specMap);
        tabHost.addTab(specInfo);
        tabHost.addTab(specSupport);
        
        listPlaces = (ListView) rootView.findViewById(R.id.listviewPlaces);
        chart = (ImageView) rootView.findViewById(R.id.iv_chart);
        difficulty = (TextView) rootView.findViewById(R.id.textdifficulty);
        mProgressChart = (ProgressBar) rootView.findViewById(R.id.progressBar_chart);
        mProgressList = (ProgressBar) rootView.findViewById(R.id.progressBar_listView);
        mProgressChart.setVisibility(View.VISIBLE);
        mProgressList.setVisibility(View.VISIBLE);
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);      
	    
	    Bundle bundle = this.getArguments();
	    if (bundle != null) {
	    	origin = bundle.getString("origin");
            destination = bundle.getString("destination");
	    }
	    
	    locOrigin = new it.polito.model.Location(origin);
        locDestination = new it.polito.model.Location(destination);
        mLocationClient = new LocationClient(getActivity(), this, this);
        mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapfragment));
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
        
        // Making the request to the geocoding api
        String requestOrigin = makeURLGeocodingRequest(locOrigin.getName()); // Request to the origin point
        
        JSONThread jsonThread = new JSONThread(requestOrigin);
        jsonThread.setRequestListener(new RequestListener() {
			
			@Override
			public void postResponse(String result) {
				setLatLng(result, "origin");
				requestGeocodingDest(); // Request to the destination point
			}
		});
        jsonThread.execute();
    }
	
	public void requestGeocodingDest() {
		String requestDestination = makeURLGeocodingRequest(locDestination.getName());
        
        JSONThread jsonThread = new JSONThread(requestDestination);
        jsonThread.setRequestListener(new RequestListener() {
			
			@Override
			public void postResponse(String result) {
				setLatLng(result, "destination");
				addingMarkers(); // Adding markers and updating camera
				requestDirections();  // Drawing route
				requestElevation(); // Creating elevation graph and setting difficulty of the tour
			}
		});
        jsonThread.execute();
	}
	
	public void addingMarkers() {
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
	}

	public void requestDirections() {
		String request = makeURLRequest(Double.toString(locOrigin.getLat()),
                Double.toString(locOrigin.getLon()), 
                Double.toString(locDestination.getLat()), 
                Double.toString(locDestination.getLon()));
        
        JSONThread jsonThread = new JSONThread(request);
        jsonThread.setRequestListener(new RequestListener() {
			
			@Override
			public void postResponse(String result) {
				drawDirections(result); // Drawing route
				setDuration(result); // Setting the duration of the tour
				try {
					setPlaces(result); // Setting the places that will be part of the logistical support
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
        jsonThread.execute();
	}
	
	public void requestElevation() {
		String requestElevation;
		try {
			requestElevation = makeURLElevationsRequest(Double.toString(locOrigin.getLat()), 
					Double.toString(locOrigin.getLon()), 
					Double.toString(locDestination.getLat()), 
					Double.toString(locDestination.getLon()));
			
			JSONThread elevationJsonThread = new JSONThread(requestElevation);
	        elevationJsonThread.setRequestListener(new RequestListener() {
				@Override
				public void postResponse(String result) {
					try {
						createGraphAndSetDifficulty(result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			});
	        elevationJsonThread.execute();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	//TODO if not needed delete this code
	/*public class JSONThread extends AsyncTask<Void, Void, String> {
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
    }*/
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	mProgressChart.setVisibility(View.GONE);
	        bmImage.setImageBitmap(result);
	    }
	}
	
	public void setLatLng(String result, String type) {
		try {
			JSONObject json = new JSONObject(result);
			JSONArray resultsArray = json.getJSONArray("results");
			JSONObject firstPosition = resultsArray.getJSONObject(0);
			JSONObject geometry = firstPosition.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");
			if (type.equals("origin")) {
				locOrigin.setLat(location.getDouble("lat"));
				locOrigin.setLon(location.getDouble("lng"));
			} else {
				locDestination.setLat(location.getDouble("lat"));
				locDestination.setLon(location.getDouble("lng"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String makeURLGeocodingRequest(String address) {
		StringBuilder url = null;
        try {
        	url =  new StringBuilder();
            url.append("http://maps.googleapis.com/maps/api/geocode/json");
            url.append("?address=");
            url.append(URLEncoder.encode(address, "utf8"));
            url.append("&sensor=false");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return url.toString();
    }
	
	public String makeURLElevationsRequest(String srclat, String srclng, String destlat, String destlng) throws UnsupportedEncodingException {
		StringBuilder url =  new StringBuilder();
		String specialCharacter = URLEncoder.encode("|", "UTF-8");
        url.append("http://maps.googleapis.com/maps/api/elevation/json");
        url.append("?path=");
        url.append(srclat);
        url.append(",");
        url.append(srclng);
        url.append(specialCharacter);
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

	public String makeURLPlacesRequest(String srclat, String srclng) throws UnsupportedEncodingException {
		StringBuilder url =  new StringBuilder();
		String specialCharacter = URLEncoder.encode("|", "UTF-8");
        url.append("https://maps.googleapis.com/maps/api/place/search/json");
        url.append("?location=");
        url.append(srclat);
        url.append(",");
        url.append(srclng);
        url.append("&radius=200");
        url.append("&rankby=prominence");
        url.append("&types=food" + specialCharacter + "bank" + specialCharacter + "atm" + specialCharacter +
        		"bicycle_store" + specialCharacter + "campground" + specialCharacter + "hospital" + specialCharacter +
        		"pharmacy" + specialCharacter + "police" + specialCharacter + "restaurant");
        url.append("&sensor=false&key=AIzaSyCm3iIOz7qAvsC0MmdBtItspW6WH74Mcqc"); // browser key
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
	
	public void createGraphAndSetDifficulty(String result) throws UnsupportedEncodingException {
		try {
			JSONObject json = new JSONObject(result);
			JSONArray elevationArray = json.getJSONArray("results");
			ArrayList<String> elevations = new ArrayList<String>();
			Double sum = 0.0;
			
			for (int i = 0; i < elevationArray.length(); i++) {
				JSONObject jsonObject = elevationArray.getJSONObject(i);
				String elevation = jsonObject.getString("elevation");
				sum = sum + Double.parseDouble(elevation);
				elevations.add(elevation);
			}
			
			Double elevationsAverage = sum/(elevations.size() - 1);
			setDifficulty(elevationsAverage);
			
			String start = String.valueOf(searchMinValue(elevations));
			String end = String.valueOf(searchMaxValue(elevations));
			String url = makeUrlChart(elevations, start, end);
			
			new DownloadImageTask(chart).execute(url);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setDifficulty(Double average) {
		String sDifficulty = "Difficulty of the tour: ";
		SpannableString spanDifficulty = new SpannableString(sDifficulty);
		spanDifficulty.setSpan(new StyleSpan(Typeface.BOLD), 0, spanDifficulty.length(), 0);
		
		if (average < 300) {
			difficulty.setText(spanDifficulty + "Easy");
		} else if (300 <= average && average < 500) {
			difficulty.setText(spanDifficulty + "Medium");
		} else {
			difficulty.setText(spanDifficulty + "Hard");
		}
	}
	
	public String makeUrlChart(ArrayList<String> elevations, String start, String end) throws UnsupportedEncodingException {
		StringBuilder url =  new StringBuilder();
		String specialCharacter = URLEncoder.encode("|", "UTF-8");
		ArrayList<String> parametrizedArray = getParametrizedArray(elevations);
        url.append("http://chart.apis.google.com/chart");
        url.append("?cht=lc");
        url.append("&chs=400x250");
        url.append("&chco=FF0000");
        url.append("&chxt=y&chxr=0,");
        url.append(start + ",");
        url.append(end);
        url.append("&chdl=elevation");
        url.append("&chtt=Elevation+Chart");
        url.append("&chts=000000,24");
        url.append("&chd=t:");
        for (String elev : parametrizedArray) {
			url.append(elev + ",");
		}
        url.deleteCharAt(url.length() - 1);
        url.append(specialCharacter);
        url.append("0");
        url.append(specialCharacter);
        url.append("0");
        url.append(specialCharacter);
        url.append("0");
        return url.toString();
	}
	
	public ArrayList<String> getParametrizedArray(ArrayList<String> elevations) {
		ArrayList<String> parametrizedArray = new ArrayList<String>();
		Double minValue = searchMinValue(elevations);
		Double maxValue = searchMaxValue(elevations);
		
		for (String elevation : elevations) {
			Double parametrizedValue = (100 * 
					(Double.parseDouble(elevation) - minValue))/(maxValue - minValue);
			parametrizedArray.add(String.valueOf(parametrizedValue));
		}
		return parametrizedArray;
	}
	
	public Double searchMaxValue(ArrayList<String> elevations) {
		Double bigger = Double.parseDouble(elevations.get(0));
		for (String elevation : elevations) {
			if (Double.parseDouble(elevation) > bigger) {
				bigger = Double.parseDouble(elevation);
			}
		}
		
		return bigger;
	}
	
	public Double searchMinValue(ArrayList<String> elevations) {
		Double smaller = Double.parseDouble(elevations.get(0));
		for (String elevation : elevations) {
			if (Double.parseDouble(elevation) < smaller) {
				smaller = Double.parseDouble(elevation);
			}
		}
		
		return smaller;
	}
	
	public void setPlaces(String result) throws UnsupportedEncodingException {
		try {
			JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
			JSONArray steps = legs.getJSONArray("steps");
			
			String location1_start = steps.getJSONObject(0).getString("start_location");
			String location2_end = steps.getJSONObject(steps.length()/3).getString("end_location");
			String location3_start = steps.getJSONObject(2*(steps.length())/3).getString("start_location");
			String location4_end = steps.getJSONObject(steps.length() - 1).getString("end_location");
			
			locationsPlaces = new ArrayList<String>();
			places = new ArrayList<Location>();
			
			location1_start = location1_start.replaceAll("[^0-9.,]+","");
			locationsPlaces.addAll(Arrays.asList(location1_start.split(",")));
			
			location2_end = location2_end.replaceAll("[^0-9.,]+","");
			locationsPlaces.addAll(Arrays.asList(location2_end.split(",")));
			
			location3_start = location3_start.replaceAll("[^0-9.,]+","");
			locationsPlaces.addAll(Arrays.asList(location3_start.split(",")));
			
			location4_end = location4_end.replaceAll("[^0-9.,]+","");
			locationsPlaces.addAll(Arrays.asList(location4_end.split(",")));
			
			String request1 = makeURLPlacesRequest(locationsPlaces.get(1), locationsPlaces.get(0));
			JSONThread placesJsonThread1 = new JSONThread(request1);
			placesJsonThread1.setRequestListener(new RequestListener() {
				
				@Override
				public void postResponse(String result) { 
					setLogisticalSupport(result);
					requestPoint2();
				}
			});
	        placesJsonThread1.execute();
	        
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void requestPoint2() {
		try {
			String request2 = makeURLPlacesRequest(locationsPlaces.get(3), locationsPlaces.get(2));
			JSONThread placesJsonThread2 = new JSONThread(request2);
			placesJsonThread2.setRequestListener(new RequestListener() {
				
				@Override
				public void postResponse(String result) { 
					setLogisticalSupport(result);
					requestPoint3();
				}
			});
			placesJsonThread2.execute();
		} catch (Exception e) {
		}
	}
	
	public void requestPoint3() {
		try {
			String request3 = makeURLPlacesRequest(locationsPlaces.get(5), locationsPlaces.get(4));
			JSONThread placesJsonThread3 = new JSONThread(request3);
			placesJsonThread3.setRequestListener(new RequestListener() {
				
				@Override
				public void postResponse(String result) { 
					setLogisticalSupport(result);
					requestPoint4();
				}
			});
			placesJsonThread3.execute();
		} catch (Exception e) {
		}
	}
	
	public void requestPoint4() {
		try {
			String request4 = makeURLPlacesRequest(locationsPlaces.get(7), locationsPlaces.get(6));
			JSONThread placesJsonThread4 = new JSONThread(request4);
			placesJsonThread4.setRequestListener(new RequestListener() {
				
				@Override
				public void postResponse(String result) { 
					setLogisticalSupport(result);
					listPlaces.setAdapter(new LazyAdapter(getActivity(), places));
					mProgressList.setVisibility(View.GONE);
			        listPlaces.setOnItemClickListener(new OnItemClickListener() {
			        	 
						@Override
						public void onItemClick(AdapterView<?> a, View v, int position, long id) {
							Object o = listPlaces.getItemAtPosition(position);
							Location newsData = (Location) o;
							Toast.makeText(getActivity(), "Selected :" + " " + newsData,
									Toast.LENGTH_LONG).show();
						}
			 
					});
				}
			});
			placesJsonThread4.execute();
		} catch (Exception e) {
		}
	}
	
	public void setLogisticalSupport(String result) {
		try {
			JSONObject json = new JSONObject(result);
			JSONArray resultsArray = json.getJSONArray("results");
			
			for (int i = 0; i <= 10; i++) {
				JSONObject object = resultsArray.getJSONObject(i);
				Location place = new Location(object.getString("name"));
				place.setAddress(object.getString("vicinity"));
				JSONObject geometry = object.getJSONObject("geometry");
				JSONObject location = geometry.getJSONObject("location");
				place.setLat(location.getDouble("lat"));
				place.setLon(location.getDouble("lng"));
				place.setUrlImage(object.getString("icon"));
				places.add(place);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setDuration(String result) { 
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
			
			TextView textDuration = (TextView) getActivity().findViewById(R.id.textduration);
			String sDuration = "Estimated duration of the tour: ";
			SpannableString spanDuration = new SpannableString(sDuration);
			spanDuration.setSpan(new StyleSpan(Typeface.BOLD), 0, spanDuration.length(), 0);
			textDuration.setText(spanDuration + text);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
                map.addPolyline(new PolylineOptions()
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
