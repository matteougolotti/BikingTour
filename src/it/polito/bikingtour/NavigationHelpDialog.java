package it.polito.bikingtour;

import it.polito.model.HelpFilter;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NavigationHelpDialog extends DialogFragment {
	private View rootView;
	
	public static NavigationHelpDialog newInstance() {
        NavigationHelpDialog dialog = new NavigationHelpDialog();
        return dialog;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		rootView = inflater.inflate(R.layout.navigation_help_dialog, container, false);
		
		CheckBox food = (CheckBox) rootView.findViewById(R.id.navigation_help_food);
		food.setChecked(HelpFilter.food);
		food.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.food = isChecked;
			}
		});
		
		CheckBox bank = (CheckBox) rootView.findViewById(R.id.navigation_help_bank);
		bank.setChecked(HelpFilter.bank);
		bank.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.bank = isChecked;
			}
		});
		
		CheckBox atm = (CheckBox) rootView.findViewById(R.id.navigation_help_atm);
		atm.setChecked(HelpFilter.atm);
		atm.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.atm = isChecked;
			}
		});
		
		CheckBox bicycle_store = (CheckBox) rootView.findViewById(R.id.navigation_help_bicycle_store);
		bicycle_store.setChecked(HelpFilter.bicycle_store);
		bicycle_store.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.bicycle_store = isChecked;
			}
		});
		
		CheckBox campground = (CheckBox) rootView.findViewById(R.id.navigation_help_campground);
		campground.setChecked(HelpFilter.campground);
		campground.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.campground = isChecked;
			}
		});
		
		CheckBox hospital = (CheckBox) rootView.findViewById(R.id.navigation_help_hospital);
		hospital.setChecked(HelpFilter.hospital);
		hospital.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.hospital = isChecked;
			}
		});
		
		CheckBox pharmacy = (CheckBox) rootView.findViewById(R.id.navigation_help_pharmacy);
		pharmacy.setChecked(HelpFilter.pharmacy);
		pharmacy.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.pharmacy = isChecked;
			}
		});
		
		CheckBox police = (CheckBox) rootView.findViewById(R.id.navigation_help_police);
		police.setChecked(HelpFilter.police);
		police.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.police = isChecked;
			}
		});
		
		CheckBox restaurant = (CheckBox) rootView.findViewById(R.id.navigation_help_restaurant);
		restaurant.setChecked(HelpFilter.restaurant);
		restaurant.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					HelpFilter.restaurant = isChecked;
			}
		});
		
		return rootView;
	}
	
}
