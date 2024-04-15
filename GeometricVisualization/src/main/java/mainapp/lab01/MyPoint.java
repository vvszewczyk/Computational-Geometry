package mainapp.lab01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        return String.format("Punkt [x=%.2f, y=%.2f]", x, y);
    }

    public double getX()
    {
        return this.x;
    }
    public double getY()
    {
        return this.y;
    }

    public double calcDistance(MyPoint p)
    {
        return Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2));
    }
}