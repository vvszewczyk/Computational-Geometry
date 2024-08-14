package org.cg.advancingfont;

public class MyLine
{
    MyPoint start;
    MyPoint end;

    public MyLine(MyPoint start, MyPoint end)
    {
        this.start = start;
        this.end = end;
    }

    public double calcDistance()
    {
        return start.calcDistance(end);
    }


}
