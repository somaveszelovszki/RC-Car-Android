package veszelovszki.soma.rc_car.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016.08.31.
 */
public class PrefManager {

    private Context mContext;

    private static final String TAG = PrefManager.class.getCanonicalName();

    public PrefManager(Context context) {
        this.mContext = context;
    }

    /**
     * Writes value into shared preferences.
     * @param pref preference to write
     * @param value value to write into preference
     */
    public void writePref(PREFERENCE pref, Object value) {
        SharedPreferences sharedPref = getDefaultSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();

        String prefKey = pref.getKey();
        Class valueClass = pref.getValueClass();

        if (Integer.class.equals(valueClass)) {
            editor.putInt(prefKey, (Integer) value);
        } else if (String.class.equals(valueClass)) {
            editor.putString(prefKey, (String) value);
        } else if (Long.class.equals(valueClass)) {
            editor.putLong(prefKey, (Long) value);
        } else if (Float.class.equals(valueClass)) {
            editor.putFloat(prefKey, (Float) value);
        } else if (Boolean.class.equals(valueClass)) {
            editor.putBoolean(prefKey, (Boolean) value);
        }

        editor.apply();
    }

    public void writePrefDefault(PREFERENCE pref) {
        writePref(pref, pref.defaultValue);
    }

    /**
     * Gets default shared preferences.
     * @return
     */
    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Reads value from shared preferences.
     * @param pref preference to read
     * @return
     */
    public Object readPref(PREFERENCE pref, Object defaultValue) {
        SharedPreferences sharedPrefs = getDefaultSharedPreferences();

        String prefKey = pref.getKey();
        Class valueClass = pref.getValueClass();

        if (Integer.class.equals(valueClass)) {
            return sharedPrefs.getInt(prefKey, (Integer) defaultValue);
        } else if (String.class.equals(valueClass)) {
            return sharedPrefs.getString(prefKey, (String) defaultValue);
        } else if (Long.class.equals(valueClass)) {
            return sharedPrefs.getLong(prefKey, (Long) defaultValue);
        } else if (Float.class.equals(valueClass)) {
            return sharedPrefs.getFloat(prefKey, (Float) defaultValue);
        } else if (Boolean.class.equals(valueClass)) {
            return sharedPrefs.getBoolean(prefKey, (Boolean) defaultValue);
        }

        Log.d(TAG, "valueClass (" + valueClass.getCanonicalName() + ") not found.");

        return null;
    }

    public Object readPref(PREFERENCE pref) {
        return readPref(pref, pref.defaultValue);
    }

    /**
     * Contains a preference with its key, value class and default value.
     */
    public enum PREFERENCE {

        // PREFERENCE KEYS MUST BE UNIQUE!!

        // default app language is English
        APP_LANGUAGE("app_language", String.class, "EN"),
        CAR_MAC_ADDRESS("car_mac_address", String.class, ""),
        CONTROL_TYPE("pref_control_type", Integer.class, Utils.ControlType.STEERING_WHEEL.getId()),
        DRIVE_MODE("pref_drive_mode", Integer.class, Utils.DriveMode.FREE_DRIVE.getId()),


        FIRST_START_CONTROL("first_start_control", Boolean.class, true),
        FIRST_START_PROFILE("first_start_profile", Boolean.class, true),
        FIRST_START_DISPLAY_ENVIRONMENT("first_start_display_environment", Boolean.class, true),
        FIRST_START_SETTINGS("first_start_settings", Boolean.class, true);

        private String key;
        private Class valueClass;
        private Object defaultValue;

        PREFERENCE(String key, Class valueClass, Object defaultValue) {
            this.key = key;
            this.valueClass = valueClass;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public Class getValueClass() {
            return valueClass;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public static PREFERENCE getByKey(String key) {
            for (PREFERENCE pref : PREFERENCE.values()) {
                if (pref.getKey().equals(key)) {
                    return pref;
                }
            }

            throw new IllegalArgumentException();
        }
    }
}
