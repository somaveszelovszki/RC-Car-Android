package veszelovszki.soma.rc_car;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import veszelovszki.soma.rc_car.communication.Message;
import veszelovszki.soma.rc_car.fragment.SettingsFragment;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */

public class SettingsActivity extends PreferenceAdaptActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsFragment mSettingsFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // adds article fragment to view
        FragmentManager fragmentManager = getSupportFragmentManager();
        mSettingsFragment = SettingsFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mSettingsFragment, SettingsFragment.TAG).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public PrefManager.PREFERENCE getFirstStartPreference() {
        return PrefManager.PREFERENCE.FIRST_START_SETTINGS;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences mSharedPreferences,
                                          String key) {

        PrefManager.PREFERENCE pref = PrefManager.PREFERENCE.getByKey(key);

        switch (pref) {
            case CONTROL_TYPE:
                // TODO
                break;

            case DRIVE_MODE:
                // TODO
                break;
        }
    }
}
