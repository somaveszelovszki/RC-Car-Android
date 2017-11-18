package veszelovszki.soma.rc_car.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.01.24.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = SettingsFragment.class.getCanonicalName();

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.preferences);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        this.update();
    }

    public void update() {
        //setupControlTypeList();
        setupDriveModeList();
    }

    private void setupControlTypeList() {

        ListPreference listPref = (ListPreference)findPreference(getString(R.string.pref_control_type));

        List<String> entries = new ArrayList<>();
        List<String> entryValues = new ArrayList<>();

        for (Utils.ControlType type : Utils.ControlType.values()) {
            entries.add(type.getName());
            entryValues.add(type.getId().toString());
        }

        listPref.setEntries(entries.toArray(new String[entries.size()]));
        listPref.setEntryValues(entryValues.toArray(new String[entryValues.size()]));
        listPref.setDefaultValue(Utils.ControlType.STEERING_WHEEL.getId().toString());
    }

    private void setupDriveModeList() {

        ListPreference listPref = (ListPreference)findPreference(getString(R.string.pref_drive_mode));

        List<String> entries = new ArrayList<>();
        List<String> entryValues = new ArrayList<>();

        for (Utils.DriveMode type : Utils.DriveMode.values()) {
            entries.add(type.getName());
            entryValues.add(type.getId().toString());
        }

        listPref.setEntries(entries.toArray(new String[entries.size()]));
        listPref.setEntryValues(entryValues.toArray(new String[entryValues.size()]));
        listPref.setDefaultValue(Utils.DriveMode.FREE_DRIVE.getId().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(
                (SharedPreferences.OnSharedPreferenceChangeListener) getActivity());
    }

    @Override
    public void onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(
                (SharedPreferences.OnSharedPreferenceChangeListener) getActivity());

        super.onPause();
    }
}
