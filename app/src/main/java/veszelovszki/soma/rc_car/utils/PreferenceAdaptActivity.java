package veszelovszki.soma.rc_car.utils;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Locale;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.adapter.NavigationDrawerListAdapter;

/**
 * Parent class for all activities in the project.
 * Adapts to preference changes (e.g. language).
 * Contains default settings and elements (e.g. default options menu).
 *
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016.07.21.
 */
public abstract class PreferenceAdaptActivity extends AppCompatActivity {

    private Boolean mIsNavigationDrawerEnabled = false;

    protected DrawerLayout mNavigationDrawerLayout = null;
    protected ListView mNavigationDrawerListView = null;

    protected PrefManager mPrefManager;
    //protected DatabaseManager mDbManager;

    protected String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefManager = new PrefManager(this);

        this.checkPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        // initializes navigation navigation_drawer
        if (mIsNavigationDrawerEnabled) {
            mNavigationDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mNavigationDrawerListView = (ListView) mNavigationDrawerLayout.findViewById(android.R.id.list);

            mNavigationDrawerListView.setAdapter(new NavigationDrawerListAdapter(this));
            mNavigationDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NavigationDrawerListItem item = (NavigationDrawerListItem) mNavigationDrawerListView.getAdapter().getItem(position);

                    onDrawerItemClick(item);
                }
            });
        }

        this.checkForFirstRun();

    }

    public void setNavigationDrawerEnabled(Boolean isNavigationDrawerEnabled) {
        mIsNavigationDrawerEnabled = isNavigationDrawerEnabled;
    }

    public Boolean isNavigationDrawerEnabled() {
        return mIsNavigationDrawerEnabled;
    }

    /**
     * Handles navigation navigation_drawer item click.
     * @param item
     */
    public void onDrawerItemClick(NavigationDrawerListItem item) {
        item.getCallback().onEvent();
    }

    public void closeDrawer() {
        if (isNavigationDrawerEnabled())
            mNavigationDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    protected void handleIntent(Intent intent){}

    /**
     * Checks if activity is running for the first time, and - if yes - calls method onFirstRun().
     */
    protected void checkForFirstRun() {

        PrefManager.PREFERENCE firstRunPref = this.getFirstRunPreference();

        // reads value from preferences
        if ((Boolean) mPrefManager.readPref(firstRunPref)) {
            // it's the first run of the activity
            onFirstRun();

            // write false value to preferences
            // -> from now on it is not the first time for the activity to run
            mPrefManager.writePref(firstRunPref, false);
        }
    }

    /**
     * Checks for permissions - called during onCreate().
     */
    protected abstract void checkPermissions();

    /**
     * Method is called when activity runs for the first time - must be overwritten by every descendant.
     */
    public abstract void onFirstRun();

    public abstract PrefManager.PREFERENCE getFirstRunPreference();

    /**
     * Handles preference event.
     * @param event preference event
     */
    public void onPreferenceEvent(ActivityManager.PREF_EVENT event) {
        switch (event) {
            case LANGUAGE_CHANGED:
                // if language changed, sets default locale
                // and recreates activity
                this.setLocaleFromPrefs();
                this.recreate();

                break;
            default:
                // do nothing with other events
        }
    }

    /**
     * Sets locale from preferences.
     */
    protected void setLocaleFromPrefs() {

        // gets app language iso code from preferences and sets it as locale
        setLocale(getAppLanguageIsoCodeFromPrefs());
    }

    /**
     * Gets app language from preferences - returns default value if preference is not present.
     * @return language iso code
     */
    public String getAppLanguageIsoCodeFromPrefs() {
        return (String)mPrefManager.readPref(PrefManager.PREFERENCE.APP_LANGUAGE);
    }

    /**
     * Sets locale.
     * @param iso639_1_Code language iso-639-1 code
     */
    protected void setLocale(String iso639_1_Code) {
        Locale locale = new Locale(iso639_1_Code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy(){

        ActivityManager.getInstance().remove(this);

        super.onDestroy();
    }

    protected View getContentView() {
        return findViewById(android.R.id.content);
    }

    /**
     * Starts new activity or (if new activity is the same as current) handles extras.
     * @param newActivityClass
     * @param <T>
     */
    public <T extends Activity> void startActivity(Class<T> newActivityClass) {
        startActivity(newActivityClass, null);
    }

    public <T extends Activity> void startActivity(Class<T> newActivityClass, Bundle extras) {

        this.closeDrawer();

        Intent intent;

        // if new activity class is the same as the current, recreates activity
        if (this.getClass().equals(newActivityClass)) {

            if (extras != null) {
                intent = this.getIntent();
                if (intent != null) {
                    if (intent.getExtras() != null)
                        intent.getExtras().clear();

                    intent.putExtras(extras);

                    this.handleIntent(intent);
                }
            }

        } else {
            intent = new Intent(this, newActivityClass);
            this.startActivity(intent);
        }
    }

    public <T extends Activity> void startActivityAndFinish(Class<T> newActivityClass) {
        startActivity(newActivityClass);
        this.finish();
    }

    public <T extends Activity> void startActivityAndFinish(Class<T> newActivityClass, Bundle extras) {
        startActivity(newActivityClass, extras);
        this.finish();
    }
}