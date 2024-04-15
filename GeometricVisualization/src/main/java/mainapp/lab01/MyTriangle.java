package mainapp.lab01;

public class MyTriangle
{
    MyPoint w1, w2, w3;
    double side1, side2, side3;

    public MyTriangle(MyPoint w1, MyPoint w2, MyPoint w3)
    {
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;

        double a1 = this.w1.calcDistance(this.w2);
        double a2 = this.w2.calcDistance(this.w3);
        double a3 = this.w3.calcDistance(this.w1);

        if(a1 <= 0 || a2 <= 0 || a3 <= 0)
        {
            throw new IllegalArgumentException("TRIANGLE\nThe sides should be bigger than 0");
        }
        else if(a1 + a2 <= a3 || a1 + a3 <= a2 || a2 + a3 <= a1)
        {
            throw new IllegalArgumentException("TRIANGLE\nThe sides do not satisfy the triangle inequality");
        }
        else
        {
            this.side1 = a1;
            this.side2 = a2;
            this.side3 = a3;
        }
    }


    public static MyTriangle makeTriangle(MyLine l1, MyLine l2, MyLine l3) throws IllegalArgumentException
    {
        MyPoint v1 = l1.CCCP(l1, l2);
        MyPoint v2 = l2.CCCP(l2, l3);
        MyPoint v3 = l3.CCCP(l3, l1);

        if (v1 == null || v2 == null || v3 == null)
        {
            throw new IllegalArgumentException("You cannot form a triangle - one or more lines are parallel.");
        }

        return new MyTriangle(v1, v2, v3);
    }
    public double calcTriangleArea()
    {
        double s = (this.side1 + this.side2 + this.side3) / 2;
        return Math.sqrt(s * (s - this.side1) * (s - this.side2) * (s - this.side3));
    }

    public double roundToNPlaces(double value, int n)
    {
        double scale = Math.pow(10, n);
        return Math.round(value * scale) / scale;
    }


    public void belongsToTriangleAreas(MyPoint p)
    {
        try
        {
            double S = roundToNPlaces(calcTriangleArea(), 4);
            MyTriangle T1 = createTriangleSafely(this.w1, this.w3, p);
            double S1 = roundToNPlaces(T1 != null ? T1.calcTriangleArea() : 0, 4);
            MyTriangle T2 = createTriangleSafely(this.w1, this.w2, p);
            double S2 = roundToNPlaces(T2 != null ? T2.calcTriangleArea() : 0, 4);
            MyTriangle T3 = createTriangleSafely(this.w2, this.w3, p);
            double S3 = roundToNPlaces(T3 != null ? T3.calcTriangleArea() : 0, 4);

            System.out.println("x: " + p.getX() + " y: " + p.getY());
            System.out.println("S: " + S + " SUM: " + (S1 + S2 + S3));

            if (Math.abs(S - (S1 + S2 + S3)) > 1e-2)
            {
                System.out.println("Not belongs");
            }
            else
            {
                System.out.println("Belongs");
            }
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public boolean belongsToTriangleSides(MyPoint p)
    {
        MyLine line1 = new MyLine(w1, w2);
        MyLine line2 = new MyLine(w2, w3);
        MyLine line3 = new MyLine(w3, w1);

        int side1 = line1.whichSide(p);
        int side2 = line2.whichSide(p);
        int side3 = line3.whichSide(p);

        boolean isInsideOrOnEdge =
                (side1 == side2 || side1 == 0 || side2 == 0) &&
                        (side2 == side3 || side2 == 0 || side3 == 0) &&
                        (side1 == side3 || side1 == 0 || side3 == 0);

        return isInsideOrOnEdge;
    }



    private MyTriangle createTriangleSafely(MyPoint p1, MyPoint p2, MyPoint p3)
    {
        try
        {
            return new MyTriangle(p1, p2, p3);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }


}
