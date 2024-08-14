package org.cg.advancingfont;


public class MyPoint
{
    double x;
    double y;

    MyPoint(double X, double Y)
    {
        this.x = X;
        this.y = Y;
    }

    @Override
    public String toString() {
        return String.format("Punkt (%.2f, %.2f)", x, y);
    }

    public double getX()
    {
        return this.x;
    }
    public double getY()
    {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double calcDistance(MyPoint p)
    {
        return Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2));
    }
}