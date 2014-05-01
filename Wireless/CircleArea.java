import java.util.ArrayList;


class CircleArea extends Area {

    // It's a typical Singleton.
    private static CircleArea _instance = null;
    public static CircleArea getArea() {
        if (_instance == null) 
            _instance = new CircleArea();
        return _instance;
    }
    private CircleArea() {
        c = new Circle(WSN.getAreaSizeHalf(), WSN.getAreaSizeHalf());
        applyDivide();
    }

    private Circle c = null;


    /**
     * Check whether point (x, y) in covered in this Circle Area.
     */
    @Override
    public boolean isInside(int x, int y) {
        return c.isInside(x, y, WSN.getAreaSizeHalf());
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
        int qArea = WSN.getAreaSizeHalf() * WSN.getAreaSizeHalf();
        for (int i = 0; i <= WSN.getAreaSize(); i++) {
            int delta = (int)(Math.sqrt(qArea - (c.centerX-i) * (c.centerX-i))) + 1;
            int left = c.centerY - delta;
            int right = c.centerY + delta;
            if (left < 0) left = 0;
            if (right > WSN.getAreaSize()) right = WSN.getAreaSize();

            if (debug) {
                for (int j = 0; j < left; j++)
                    sb.append('.');
            }

            for (int j = left; j <= right; j++) {
                if (isInside(i, j)) {  // If (i, j) is in the current area
                    count++;
                    int kCount = 0, rCount = 0;
                    for (Sensor s: sensors) {
                        if (s.covers(i, j)) { // If (i, j) covered by a sensor
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

                    if (debug) {
                        if (rCount < 10)
                            sb.append(rCount);
                        else
                            sb.append('X');
                    }

                    kCoverage += kCount;
                } else {
                    if (debug) sb.append('.');
                }
            }

            if (debug) {
                for (int j = right + 1; j<= WSN.getAreaSize(); j++)
                    sb.append('.');
                sb.append('\n');
            }
        }
        if (debug) graph = sb.toString();

        if (count == 0)
            return 0;
        else
            return kCoverage * 100 / count;
    }

    @Override
    public String getAreaTypeStr() {
        return "circle";
    }

}


