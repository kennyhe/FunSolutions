


class Circle {
    public int centerX, centerY;

    public Circle(int cx, int cy) {
        this.centerX = cx;
        this.centerY = cy;
    }

    /**
     * Check whether point (x, y) in covered in this circle.
     */
    public boolean isInside(int x, int y, int range) {
        return ((centerX - x) * (centerX - x) + (y - centerY) * (y - centerY) <= range * range);
    }

}

