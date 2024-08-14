package org.cg.advancingfont;

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

        System.out.println("Creating triangle: " + w1 + ", " + w2 + ", " + w3 + "\n");

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


    private boolean linesIntersect(MyPoint a, MyPoint b, MyPoint c, MyPoint d) {
        //Implementacja testu przecięcia dwóch odcinków oparta na algorytmie wyznaczania położenia względnego dwóch odcinków
        double d1 = direction(c, d, a);
        double d2 = direction(c, d, b);
        double d3 = direction(a, b, c);
        double d4 = direction(a, b, d);

        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
                ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0)))
        {
            return true;
        }
        return (d1 == 0 && onSegment(c, d, a)) ||
                (d2 == 0 && onSegment(c, d, b)) ||
                (d3 == 0 && onSegment(a, b, c)) ||
                (d4 == 0 && onSegment(a, b, d));
    }

    private double direction(MyPoint pi, MyPoint pj, MyPoint pk)
    {
        //iloczyn wektorowy (pk-pi) x (pj-pi)
        return (pk.x - pi.x) * (pj.y - pi.y) - (pk.y - pi.y) * (pj.x - pi.x);
    }

    private boolean onSegment(MyPoint pi, MyPoint pj, MyPoint pk) {
        //z wyjątkiem wierzchołków
        return pk != pi && pk != pj &&
                Math.min(pi.x, pj.x) < pk.x && pk.x < Math.max(pi.x, pj.x) &&
                Math.min(pi.y, pj.y) < pk.y && pk.y < Math.max(pi.y, pj.y);
    }


    public boolean intersects(MyTriangle other) {
        //sprawdzanie przecięcie dla każdej krawędzi jednego trójkąta z każdą krawędzią drugiego trójkąta
        return linesIntersect(this.w1, this.w2, other.w1, other.w2) ||
                linesIntersect(this.w1, this.w2, other.w2, other.w3) ||
                linesIntersect(this.w1, this.w2, other.w3, other.w1) ||
                linesIntersect(this.w2, this.w3, other.w1, other.w2) ||
                linesIntersect(this.w2, this.w3, other.w2, other.w3) ||
                linesIntersect(this.w2, this.w3, other.w3, other.w1) ||
                linesIntersect(this.w3, this.w1, other.w1, other.w2) ||
                linesIntersect(this.w3, this.w1, other.w2, other.w3) ||
                linesIntersect(this.w3, this.w1, other.w3, other.w1);
    }
}
