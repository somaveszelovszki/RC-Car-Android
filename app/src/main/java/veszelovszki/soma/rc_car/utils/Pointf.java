package veszelovszki.soma.rc_car.utils;

import android.graphics.PointF;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.27.
 */

public class Pointf extends PointF {
    public static final Pointf ORIGO = new Pointf(0.0f, 0.0f);

    public Pointf() {
        super();
    }
    public Pointf(float x, float y) {
        super(x, y);
    }

    public Pointf add(Pointf other) {
        return new Pointf(x + other.x, y + other.y);
    }

    public Pointf sub(Pointf other) {
        return new Pointf(x - other.x, y - other.y);
    }

    public ByteArray toByteArray(){
        ByteArray result = new ByteArray(2);

        // maps x and y [cm] coordinates to fit into a byte
        result.set(0, (byte) Utils.incarcerate((int) (x * 128 / Config.ULTRA_MAX_DISTANCE), -128, 127));
        result.set(1, (byte) Utils.incarcerate((int) (y * 128 / Config.ULTRA_MAX_DISTANCE), -128, 127));

        return result;
    }

    public static Pointf fromByteArray(ByteArray bytes){
        // maps coordinates to [cm] values
        return new Pointf(
                ((int) bytes.get(0)) * Config.ULTRA_MAX_DISTANCE / 128.0f,
                ((int) bytes.get(1)) * Config.ULTRA_MAX_DISTANCE / 128.0f
        );
    }

    public Pointf toDisplayPoint(int w, int h) {
        int ratio = max(w, h) / 2;
        return new Pointf(
                w / 2 + x * ratio / Config.ULTRA_MAX_DISTANCE,
                h / 2 - y * ratio / Config.ULTRA_MAX_DISTANCE);
    }
}
