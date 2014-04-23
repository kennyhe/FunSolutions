import java.util.ArrayList;


class SquareArea extends Area {

    // It's a typical Singleton.
    private static SquareArea _instance = null;
    private SquareArea() {
        applyDivide();
    }
    public static SquareArea getArea() {
        if (_instance == null)
            _instance = new SquareArea();
        return _instance;
    }


    /**
     * Check whether point (x, y) in covered in this Square Area.
     */
    @Override
    public boolean isInside(int x, int y) {
        return (x >= 0) && (x <= WSN.getAreaSize()) && (y >= 0) && (y <= WSN.getAreaSize());
    }


    @Override
    public int getCoveredPercentage() {
        // Prepare debug information
        boolean debug = WSN.getDebugMode();
        StringBuffer sb = null;
        if (debug) {
            sb = new StringBuffer(getDebugResult());
            sb.append('\n');
        }
        int maxKCoverage = WSN.getMaxKCoverage();

        int count = 0, kCoverage = 0;
        for (int i = 0; i <= WSN.getAreaSize(); i++) {
            for (int j = 0; j <= WSN.getAreaSize(); j++) {
                count++;
                int kCount = 0, rCount = 0;
                for (Sensor s: sensors) {
                    if (s.covers(i, j)) { // If (i, j) covered by the sensor
                        rCount++;

                        if (debug) {
                            if (kCount < maxKCoverage)
                                kCount++;
                        } else {
                            kCount++;
                            if (kCount >= maxKCoverage)
                                break;
                        }
                    }
                }
                kCoverage += kCount;

                if (debug) {
                    if (rCount < 10)
                        sb.append(rCount);
                    else
                        sb.append('X');
                }
            }
            if (debug) sb.append('\n');
        }

        if (debug) graph = sb.toString();

        if (count == 0)
            return 0;
        else
            return kCoverage * 100 / count;
    }

    @Override
    public String getAreaTypeStr() {
        return "square";
    }
}


