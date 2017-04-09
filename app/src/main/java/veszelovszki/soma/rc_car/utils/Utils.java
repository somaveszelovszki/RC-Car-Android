package veszelovszki.soma.rc_car.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;


/**
 * Contains helper functions that can be used anywhere.
 * This class should not be instantiated, contains only static data.
 *
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016.07.21..
 */
public class Utils {

    public enum ControlType {
        STEERING_WHEEL(1, "Steering wheel"),
        GYROSCOPE(2, "Gyroscope");

        private Integer mId;
        private String mName;

        ControlType(Integer id, String name) {
            mId = id;
            mName = name;
        }

        public Integer getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public static ControlType getById(Integer id) {
            for (ControlType type : ControlType.values()) {
                if (type.getId().equals(id))
                    return type;
            }

            throw new IllegalArgumentException();
        }
    }

    /**
     * Enum values match C++ enum values in Arduino program.
     */
    public enum DriveMode {
        FREE_DRIVE(1, "Free drive"),
        SAFE_DRIVE(2, "Safe drive"),
        AUTOPILOT(3, "Autopilot");

        private Integer mId;
        private String mName;

        DriveMode(Integer id, String name) {
            mId = id;
            mName = name;
        }

        public Integer getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public static DriveMode getById(Integer id) {
            for (DriveMode type : DriveMode.values()) {
                if (type.getId().equals(id))
                    return type;
            }

            throw new IllegalArgumentException();
        }
    }

    public static final String TAG = Utils.class.getCanonicalName();

    // should not be instantiated
    private Utils () {}

    public interface Callback {
        void onEvent();
    }

    /**
     * Sets default locale for application.
     * @param activity
     * @param iso639_1_Code
     */
    public static void setDefaultLocale(Activity activity, String iso639_1_Code) {

        Locale locale = new Locale(iso639_1_Code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());
    }

    public static String getDefaultLocale() {
        return Locale.getDefault().getLanguage();
    }

    public static String getPlaceAssetFolder(Integer placeId) {
        return  "file:///android_asset/" + placeId + "/";
    }

    public static Drawable drawableFromAsset(Context context, String imageUrl) {

        if (imageUrl == null) {
            return null;
        }

        try {
            // get input stream
            InputStream ims = context.getAssets().open(imageUrl);
            // load image as Drawable
            return Drawable.createFromStream(ims, null);
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void copyFileFromAssets(Context context, String source, String destination) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open(source);
            out = new FileOutputStream(new File(destination));
            copyFile(in, out);
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + source, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    /**
     * Return data directory, meaning the /data/data/hu.cartographia.hu directory
     * @return the data directory
     * TODO above API LEVEL 24 it is included in Context
     */
    public static File getDataDir(Context context) {
        return new File(context.getApplicationInfo().dataDir);
    }

    public static File openFolder(String path, Boolean createIfNotExists) {
        File dir = new File(path);

        if (!dir.exists() && createIfNotExists) {
            dir.mkdir();
        }

        return dir;
    }

    public static class Result {

        private Boolean success;
        private String message;

        public Result() {}

        public Result setSuccess(Boolean success) {
            this.success = success;

            return this;
        }

        public Result setMessage(String message) {
            this.message = message;

            return this;
        }

        public Boolean isSuccessful() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }


    public static Drawable resizeDrawable (Context context, Drawable image, Integer sizeX, Integer sizeY) {
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(context.getResources(), bitmapResized);

        return image;
    }

    public static Drawable scaleDrawable (Context context, Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        return resizeDrawable(context, image, sizeX, sizeY);
    }

    public static Integer map(@NonNull Integer value, Integer fromLow, Integer fromHigh, Integer toLow, Integer toHigh) {

        if (value <= fromLow) {
            return toLow;
        }
        if (value >= fromHigh) {
            return toHigh;
        }

        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
    }
}
