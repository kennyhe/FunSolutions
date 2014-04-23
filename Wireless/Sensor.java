import java.lang.Math;


class Sensor {
    private Circle c;
    private static int qCover = WSN.getCoverRangeRadius() * WSN.getCoverRangeRadius();
    private static int qComm = WSN.getCommRangeRadius() * WSN.getCommRangeRadius();

    public Sensor(int cx, int cy) {
        this.c = new Circle(cx, cy);
    }


    public Circle getCircle() {
        return c;
    }


    /**
     * Check whether point (x, y) in covered in this sensor.
     */
    public boolean covers(int x, int y) {

        return c.isInside(x, y, WSN.getCoverRangeRadius());
    }


    /**
     * Check whether this sensor can communicate with another sensor s.
     */
    public boolean connects(Sensor s) {
        Circle c1 = s.getCircle();
        return ((c.centerX - c1.centerX) * (c.centerX - c1.centerX) + (c.centerY - c1.centerY) * (c.centerY - c1.centerY) <= qComm);
    }
    
    /**
     * Check whether this sensor overlaps with another sensor s in coverage range.
     */
    public boolean hasCoverOverlap(Sensor s) {
        Circle c1 = s.getCircle();
        return ((c.centerX - c1.centerX) * (c.centerX - c1.centerX) + (c.centerY - c1.centerY) * (c.centerY - c1.centerY) <= qCover);
    }


    /**
     * Factory method: Create a sensor in the specified area, and return it.
     */
    public static Sensor getSensor(Area area) {
        int x, y;
        // Try to get a random point which both in the area and the subArea.
        // (for the circle area, a point inside the sub area may not be in the area).
        SubArea sa = area.getNextSubArea();
        do {
            x = (int)(Math.random() * WSN.getAreaSize());
            y = (int)(Math.random() * WSN.getAreaSize());
        } while (! (sa.isInside(x, y) && area.isInside(x, y)));

        Sensor s = new Sensor(x, y);
        area.addSensor(s);
        return s;
    }
        
}

