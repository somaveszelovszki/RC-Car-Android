package veszelovszki.soma.rc_car.common;


import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Message object describes msg data - code and value.
 * Codes match Arduino project's msg codes.
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class ByteArray {
    private byte[] mValue;

    public ByteArray(){}

    public ByteArray(byte[] value) {
        setValue(value);
    }

    public byte[] getValue() {
        return mValue;
    }

    public void setValue(byte[] value) {
        mValue = value;
    }

    public static ByteArray fromInteger(int value) {
        return new ByteArray(Utils.intToBytes(value));
    }

    public static ByteArray fromFloat(float value) {
        return new ByteArray(Utils.floatToBytes(value));
    }

    public int asInteger() {
        return Utils.bytesToInt(mValue);
    }

    public float asFloat() {
        return Utils.bytesToFloat(mValue);
    }

    public ByteArray concat(ByteArray other) {
        return new ByteArray(Utils.concatByteArrays(mValue, other.mValue));
    }
}
