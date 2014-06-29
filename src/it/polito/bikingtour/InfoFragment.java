package it.polito.bikingtour;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class InfoFragment extends Fragment {
	public InfoFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            String origin = bundle.getString("origin");
            String destination = bundle.getString("destination");
        }
        return rootView;
    }
}
