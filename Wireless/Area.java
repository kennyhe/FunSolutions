import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

abstract class Area {
    enum AreaType {
        Circle,
        Square
    };

    protected ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    
    // About the sub area
    private int nextSubAreaIndex = 0;
    private ArrayList<SubArea> subAreas = new ArrayList<SubArea>();


    // Debug graph
    protected String graph = "No debug information available. Please make sure run in debug mode and has already checked the coverage.";


    public final ArrayList<Sensor> getSensors() {
        return sensors;
    }


    public final void addSensor(Sensor s) {
        sensors.add(s);
    }


    /**
     * Check whether point (x, y) in covered in this area
     */
    public abstract boolean isInside(int x, int y);


    /**
     * Return the percentage of covered points by the sensors
     * The value will be the floor of a percentage
     * e.g: If the actual coverage percentage is 92.332%, then the return value is 92%.
     */
    public abstract int getCoveredPercentage();


    public abstract String getAreaTypeStr();


    /**
     * Apply the divides of the area per dimension.
     */
    protected void applyDivide() {
        int d = WSN.getDivides();
        int areaSize = WSN.getAreaSize();

        if (d <= 1) {
            subAreas.add(new SubArea(0, 0, areaSize, areaSize));
            return;
        }

        int dist = (areaSize - 1) / d; // The side of a sub area
        int step = dist + 1;
        int startX = 0, startY, endX, endY;
        for (int i = 0; i < d; i++, startX += step) {
            startY = 0;
            if (i < d -1)
                endX = startX + dist;
            else
                endX = areaSize;

            for (int j = 0; j < d; j++, startY += step) {
                if (j < d -1) 
                    endY = startY + dist;
                else
                    endY = areaSize;

                SubArea sa = new SubArea(startX, startY, endX, endY);
                if (sa.overlap(this))
                    subAreas.add(sa);
            }
        }
    }

    /**
     * Return the next sub square area in which we can place the sensor.
     */
    public final SubArea getNextSubArea() {
        SubArea sa = subAreas.get(nextSubAreaIndex);
        nextSubAreaIndex = (nextSubAreaIndex + 1) % (subAreas.size());
        return sa;
    }


    /**
     * Return the text representation of the graph of the WSN for debug use.
     */
    public String getDebugGraph() {
        return graph;
    }


    /**
     * Return the text information for debug.
     */
    public String getDebugResult() {
        return getMsgHeader("debug", 0) + getSensorPositions();
    }

    /**
     * Return the text information as the final result
     */
    public String getResult(int cover, int connect) {
        return getMsgHeader("cover", cover) + getMsgHeader("connect", connect) + getSensorPositions();
    }

    private String getMsgHeader(String msgType, int n) {
        StringBuffer sb = new StringBuffer();
        switch (msgType) {
            case "cover":
            case "connect":
                sb.append("Need ").append(n);
                break;
            case "debug":
            default:
                sb.append("Added ").append(sensors.size());
        }

        sb.append(" sensors ");

        switch (msgType) {
            case "cover":
                sb.append(" to cover ").append(WSN.getMinCoverPercentage()).append("% of ");
                break;
            case "connect":
                sb.append(" to connect all nodes under the ");
                break;
            case "debug":
            default:
                sb.append(" to the ");
        }

        sb.append(getAreaTypeStr()).append(" area with diameter ").append(WSN.getAreaSize()).append('\n');
        return sb.toString();
    }


    private String getSensorPositions() {
        StringBuffer sb = new StringBuffer("The sensor locations are:\n");

        Iterator<Sensor> it = sensors.iterator();
        Sensor s;
        if (it.hasNext()) {
            s = it.next();
            sb.append("(").append(s.getCircle().centerX).append(", ").append(s.getCircle().centerY).append(")");
        }
        while (it.hasNext()) {
            s = it.next();
            sb.append(", (").append(s.getCircle().centerX).append(", ").append(s.getCircle().centerY).append(")");
        }
        sb.append('\n');
        return sb.toString();
    }


    /**
     * Check to make sure all the sensors have been connected, with Broadth First Search algorithm.
     */
    public boolean allSensorsConnected() {
        if (sensors.isEmpty())
            return false;

        ArrayList<Sensor> visited = new ArrayList<Sensor>();
        Queue<Sensor> queue = new LinkedList<Sensor>();

        queue.add(sensors.get(0));
        visited.add(sensors.get(0));
        while ((!queue.isEmpty()) && (visited.size() < sensors.size())) {
            Sensor s = queue.poll();
            for (Sensor s1: sensors) {
                if (! visited.contains(s1)) {
                    if (s.connects(s1)) {
                        queue.add(s1);
                        visited.add(s1);
                    }
                }
            }
        }

        return (visited.size() == sensors.size());   
    }
}

