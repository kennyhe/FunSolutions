import java.util.ArrayList;


class SubArea {
    public int x1, y1, x2, y2; // (x1, y1) left upper; (x2, y2) right buttom.

    public SubArea(int x1, int y1, int x2, int y2) {
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
    }

    /**
     * Check whether point (x, y) in covered in this Square Sub Area
     */
    public boolean isInside(int x, int y) {
        return (x >= x1) && (x <= x2) && (y >= y1) && (y <= y2);
    }


    /**
     * Check whether this sub area covers part of an area
     */
    public boolean overlap(Area area) {
        if (area.isInside(x1, y1))
            return true;
        if (area.isInside(x1, y2))
            return true;
        if (area.isInside(x2, y1))
            return true;
        if (area.isInside(x2, y2))
            return true;
        return false;
    }
}


