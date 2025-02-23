package net.onelitefeather.pandorascluster.api.utils;

public final class NumberUtil {

    private NumberUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static int floor(Double number) {
        final int floor = number.intValue();
        return floor == number ? floor : floor - (int) (Double.doubleToRawLongBits(number) >>> 63);
    }

    public static int locToBlock(double loc) {
        return floor(loc);

    }
}
