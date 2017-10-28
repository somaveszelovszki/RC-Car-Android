package veszelovszki.soma.rc_car.utils;


/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class ByteArray {
    private byte[] mValue;

    public ByteArray(Integer length){
        mValue = new byte[length];
    }

    public ByteArray(byte[] value) {
        setValue(value);
    }

    public byte[] getValue() {
        return mValue;
    }

    public void setValue(byte[] value) {
        mValue = value;
    }

    public byte get(int index) {
        return mValue[index];
    }

    public void set(int index, byte value) {
        mValue[index] = value;
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

    public ByteArray subArray(int startIndex, int length) {
        ByteArray result = new ByteArray(length);
        for (int i = 0; i < length; ++i)
            result.set(i, this.get(startIndex + i));
        return result;
    }

    public void shiftBytesLeft(int byteShift) {
        for (int i = mValue.length - 1; i >= byteShift; --i)
            mValue[i] = mValue[i - byteShift];

        for (int i = byteShift - 1; i >= 0; --i)
            mValue[i] = (byte)0;
    }

    public void shiftBytesRight(int byteShift) {
        for (int i = 0; i < mValue.length - byteShift; ++i)
            mValue[i] = mValue[i + byteShift];

        for (int i = byteShift; i < mValue.length; ++i)
            mValue[i] = (byte)0;
    }

    public Integer indexOf(Integer value) {
        Integer index = -1;
        for (Integer i = 0; index == -1 && i < mValue.length - 4; ++i)
            if (Utils.bytesToInt(mValue, i) == value)
                index = i;
        return index;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("[ ");
        for (int i = 0; i < mValue.length - 1; ++i) {
            builder.append(mValue[i]);
            builder.append(", ");
        }
        builder.append(mValue[mValue.length - 1]);
        builder.append(" ]");
        return builder.toString();
    }
}
