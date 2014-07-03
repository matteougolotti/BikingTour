package it.polito.bikingtour;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewRouteFragment extends Fragment {
	public NewRouteFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_newroute, container, false);
		final EditText actualLocation = (EditText) rootView.findViewById(R.id.et_actualLocation);
		final EditText destLocation = (EditText) rootView.findViewById(R.id.et_destination);
		
		Button submit = (Button) rootView.findViewById(R.id.bt_submit);
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("origin", actualLocation.getText().toString());
				bundle.putString("destination", destLocation.getText().toString());
				Fragment newFragment = new InfoFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		return rootView;
    }
}
