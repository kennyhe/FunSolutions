
class WSN {
    // Parameters and getters, shared in global scope.
    private static int MAX_K_COVERAGE = 2;
    private static boolean DEBUG_MODE = false;
    private static int AREA_SIZE = 64;
    private static int COVER_RANGE_RADIUS = 16/2;
    private static int COMM_RANGE_RADIUS = 32/2;
    private static int MIN_COVER_PERCENTAGE = 100;
    private static int DIVIDES = 3;

    public static int getMaxKCoverage() {return MAX_K_COVERAGE; }
    public static boolean getDebugMode() {return DEBUG_MODE; }
    public static int getAreaSize() { return AREA_SIZE; }
    public static int getAreaSizeHalf() {return AREA_SIZE / 2; }
    public static int getCoverRangeRadius() {return COVER_RANGE_RADIUS; }
    public static int getCommRangeRadius() { return COMM_RANGE_RADIUS; }
    public static int getMinCoverPercentage() { return MIN_COVER_PERCENTAGE; }
    public static int getDivides() { return DIVIDES; }


    // Make it a Singleton.
    private static WSN _instance = null;

    /** 
     * Get the existing WSN
     * If no WSN instance, create a default one with all default settings
     */
    public static WSN getWSN() {
        if (_instance == null) {
            System.out.println("Created a square area.");
            _instance = new WSN(Area.AreaType.Square);
        }
        return _instance;
    }


    /** Create a WSN without parameters except the type (Circle or Square) */
    public static WSN getWSN(Area.AreaType t) {
        if (_instance == null) {
            _instance = new WSN(t);
        } else {
            System.out.println("A WSN already exists, will use that one, and no new WSN created!");
        }
        return _instance;
    }


    // The whole area;
    private Area area = null;

    private WSN(Area.AreaType t) {
        initArea(t);
    }


    private void initArea(Area.AreaType t) {
        switch (t) {
            case Circle:
                area = CircleArea.getArea();
                break;
            case Square:
                area = SquareArea.getArea();
                break;
            default:
                area = SquareArea.getArea();
        }
    }


    private static int getInt(String s) {
        int x = -1;
        try {
            x = Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        return x;
    }

    /** Create a WSN with arguments */
    public static WSN getWSN(String args[]) {
        // Construct the area based on the arguments.
        Area.AreaType t = Area.AreaType.Circle;  // Default is round area.

        if (_instance == null) {
            // TParse the parameters and update them in the global settings.
            int pos = 0, n = 0;
            boolean parseSucc = false;

        noParse:
            while (pos < args.length) {
                String arg = args[pos];

                switch (arg) {
                    case "-h":
                        System.out.println("Print help here!");
                        break noParse;
                    case "-s":
                        t = Area.AreaType.Square;
                        break;
                    case "-d":
                        DEBUG_MODE = true;
                        break;
                    case "-a":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-a\" param!");
                            break noParse;
                        }
                        n = getInt(args[pos]);
                        if (n <= 1) {
                            System.out.println("Invalid area size: -a " + args[pos]);
                            break noParse;
                        }
                        AREA_SIZE = n;
                        break;
                    case "-r":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-r\" param!");
                            break noParse;
                        }
                        n = getInt(args[pos]);
                        if (n <= 1) {
                            System.out.println("Invalid sensing range: -r " + args[pos]);
                            break noParse;
                        }
                        COVER_RANGE_RADIUS = n/2;
                        break;
                    case "-c":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-c\" param!");
                            break noParse;
                        }
                        n = getInt(args[pos]);
                        if (n <= 1) {
                            System.out.println("Invalid communication range: -c " + args[pos]);
                            break noParse;
                        }
                        COMM_RANGE_RADIUS = n/2;
                        break;
                    case "-n":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-n\" param!");
                            break noParse;
                        }
                        n = getInt(args[pos]);
                        if ((n <= 0) || (n > 10)) {
                            System.out.println("Invalid area dimension divide param: -n " + args[pos]);
                            break noParse;
                        }
                        DIVIDES = n;
                        break;
                    case "-p":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-p\" param!");
                            break noParse;
                        }
                        n = Integer.parseInt(args[pos]);
                        if ((n <= 0) || (n > 100)) {
                            System.out.println("Invalid cover percentage: -p " + args[pos]);
                            break noParse;
                        }
                        MIN_COVER_PERCENTAGE = n;
                        break;
                    case "-k":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-a\" param!");
                            break noParse;
                        }
                        n = Integer.parseInt(args[pos]);
                        if (n <= 0) {
                            System.out.println("Invalid area size: -a " + args[pos]);
                            break noParse;
                        }
                        MAX_K_COVERAGE = n;
                        break;
                    case "-f":
                    case "-m":
                        if (++pos > args.length) {
                            System.out.println("Missed number after the \"-f\" or \"-m\" param!");
                            break noParse;
                        }
                        n = Integer.parseInt(args[pos]);
                        if (n < 0) {
                            System.out.println("Invalid area size: -f or -m " + args[pos]);
                            break noParse;
                        }
                        System.out.println("The parameter -m, -f not implemented."); 
                        break;
                    default:
                        System.out.println("Unrecognizable parameter: " + arg);
                        break noParse;
                }

                pos++;
                if (pos >= args.length)
                    parseSucc = true;
            }

            if (parseSucc) {  // parse params success
                // Create the area.
                _instance = new WSN(t);
            } else {
                _instance = null;
            }
        } else {
            System.out.println("A WSN already exists, will use that one, and no new WSN created!");
        }
        return _instance;
    }

    public void run() {
        int p = 0, sCount = 0;
        int cover = -1, connect = -1;
        do {
            Sensor s = Sensor.getSensor(area);
            sCount++;
            p = area.getCoveredPercentage();
            if (DEBUG_MODE)
                System.out.println("\n" + area.getDebugGraph() + "\n\n");
            if (cover < 0) {
                if (p > MIN_COVER_PERCENTAGE) cover = sCount;
            }

            if (cover > 0) {
                if (area.allSensorsConnected()) connect = sCount;
            }
        } while ((cover < 0) || (connect < 0));
        System.out.println(area.getResult(cover, connect));
    }

    public static void main(String args[]) {
        WSN wsn = WSN.getWSN(args);
        if (wsn != null)
            wsn.run();
    }
}
