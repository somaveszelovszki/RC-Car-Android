package veszelovszki.soma.rc_car.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import veszelovszki.soma.rc_car.R;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.01.24.
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener  {

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
    }


    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences mSharedPreferences,
                                          String key) {
        Log.d("Settings", "onSharedPreferenceChanged: " + key);

    }
}
