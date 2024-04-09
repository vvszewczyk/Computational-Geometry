package mainapp.lab01;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MyLine
{
    private double a;
    private double b;
    private double c;


    public MyLine(MyPoint p1, MyPoint p2) {
        this.a = p2.getY() - p1.getY(); // y2 - y1
        this.b = p1.getX() - p2.getX(); // x1 - x2
        this.c = -this.a * p1.getX() - this.b * p1.getY(); // -a*x1 - b*y1
    }
    public MyLine(double A, double B, double C)
    {
        this.a = A;
        this.b = B;
        this.c = C;
    }
    public double getA()
    {
        return a;
    }

    public double getB()
    {
        return b;
    }

    public double getC()
    {
        return c;
    }


    @Override
    public String toString() {
        return String.format("A: "+this.a+" B: "+this.b+" C: "+this.c);
    }



    public boolean isInLine(MyPoint p)
    {
        double belongs = (this.a * p.getX()) + (this.b * p.getY()) + this.c;
        return Math.abs(belongs)<1e-2;
    }

    public boolean isInSegment(MyPoint p, MyPoint p1, MyPoint p2)
    {
        if(!this.isInLine(p))
        {
            return false;
        }

        //OX
        if ((p.getX() < Math.min(p1.getX(), p2.getX())) || (p.getX() > Math.max(p1.getX(), p2.getX())))
        {
            return false;
        }

        //OY
        if ((p.getY() < Math.min(p1.getY(), p2.getY())) || (p.getY() > Math.max(p1.getY(), p2.getY())))
        {
            return false;
        }

        return true;
    }
    public int whichSide(MyPoint p)
    {
        double side = (this.a * p.getX()) + (this.b * p.getY()) + this.c;

        if (Math.abs(side) < 1e-2)
        {
            return 0;
        }
        else if (side < 0)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
    public MyLine translate(double dx, double dy)
    {
        //ax + by + (c - (a * dx) - (b * dy)) = 0
        double newC = this.c - (this.a * dx) - (this.b * dy);
        return new MyLine(this.a, this.b, newC);
    }
    public MyPoint mirror(MyLine l, MyPoint p)
    {
        double A = l.getA();
        double B = l.getB();
        double C = l.getC();

        // Ax + By + C = 0,
        // -Bx + Ay + (B * px - A * py) = 0

        double D = B * p.getX() - A * p.getY(); //D = (B * px - A * py)

        double denominator = (A * A) + (B * B);

        double xc = (B * D - A * C) / denominator;
        double yc = (A * D + B * C) / -denominator;

        double xp = 2 * xc - p.getX();
        double yp = 2 * yc - p.getY();

        return new MyPoint(xp, yp);
    }

    public MyPoint CCCP(MyLine l1, MyLine l2)
    {
        double A1 = l1.getA();
        double B1 = l1.getB();
        double C1 = l1.getC();
        double A2 = l2.getA();
        double B2 = l2.getB();
        double C2 = l2.getC();

        double W = (A1*B2)-(A2*B1);
        double Wx = (C2*B1)-(C1*B2);
        double Wy = (C1*A2)-(A1*C2);

        if(Math.abs(W)<1e-2)
        {
            System.out.println("The lines are parallel");
            return null;
        }

        double xs = Wx/W;
        double ys = Wy/W;

        MyPoint p = new MyPoint(xs, ys);

        if(l1.isInLine(p) && l2.isInLine(p))
        {
            System.out.println("Belongs, xs = " + xs + ", ys = " + ys);
        }

        return new MyPoint(xs, ys);
    }

    public double pointDistance(MyPoint p)
    {
        double A = this.a;
        double B = this.b;
        double C = this.c;
        double distance = 0;
        double module = Math.abs((A*p.getX()) + (B*p.getY()) + C);
        double denominator = Math.sqrt((A*A) + (B*B));

        if(Math.abs(denominator)<1e-2)
        {
            return distance;
        }

        distance = module/denominator;
        return distance;
    }

    public double calcAngle(MyLine l1, MyLine l2)
    {
        double u = Math.sqrt(Math.pow(l1.a, 2) + Math.pow(l1.b, 2));
        double v = Math.sqrt(Math.pow(l2.a, 2) + Math.pow(l2.b, 2));
        double uv = (l1.a*l2.a)+(l1.b*l2.b);
        return Math.toDegrees(Math.acos(uv/(u*v)));
    }

    private MyPoint findIntersection(double value, boolean isX)
    {
        double x, y;
        if (isX)
        {
            x = value;
            if (Math.abs(b) < 1e-2) return null;
            y = (-a * x - c) / b;
        }
        else
        {
            y = value;
            if (Math.abs(a) < 1e-2) return null;
            x = (-b * y - c) / a;
        }
        return new MyPoint(x, y);
    }

    public List<MyPoint> findEdgePoints(int width, int height) {
        List<MyPoint> edgePoints = new ArrayList<>();

        // Uzyskaj punkty przecięcia dla wszystkich krawędzi
        MyPoint left = findIntersection(0, true);
        MyPoint right = findIntersection(width, true);
        MyPoint top = findIntersection(0, false);
        MyPoint bottom = findIntersection(height, false);

        // Dodaj punkty, jeśli znajdują się one w granicach okna
        if (left != null && left.getY() >= 0 && left.getY() <= height) {
            edgePoints.add(left);
        }
        if (right != null && right.getY() >= 0 && right.getY() <= height) {
            edgePoints.add(right);
        }
        if (top != null && top.getX() >= 0 && top.getX() <= width) {
            edgePoints.add(top);
        }
        if (bottom != null && bottom.getX() >= 0 && bottom.getX() <= width) {
            edgePoints.add(bottom);
        }

        // Usuwanie duplikatów na podstawie współrzędnych
        edgePoints = edgePoints.stream().distinct().collect(Collectors.toList());

        // Jeśli masz więcej niż dwa punkty, wybierz te, które są najbardziej oddalone od siebie
        // Zakładamy, że edgePoints już zawiera wszystkie punkty przecięcia
        if (edgePoints.size() > 2) {
            edgePoints.sort(Comparator.comparingDouble(MyPoint::getX).thenComparingDouble(MyPoint::getY));
            List<MyPoint> uniquePoints = new ArrayList<>();
            MyPoint prevPoint = null;
            for (MyPoint currentPoint : edgePoints) {
                if (prevPoint == null || Math.abs(prevPoint.getX() - currentPoint.getX()) > 0.001 || Math.abs(prevPoint.getY() - currentPoint.getY()) > 0.001) {
                    uniquePoints.add(currentPoint);
                    prevPoint = currentPoint;
                }
            }
            // Jeżeli po usunięciu duplikatów mamy więcej niż 2 punkty, zachowujemy tylko te na końcach listy
            if (uniquePoints.size() > 2) {
                edgePoints.clear();
                edgePoints.add(uniquePoints.get(0));
                edgePoints.add(uniquePoints.get(uniquePoints.size() - 1));
            } else {
                edgePoints = uniquePoints;
            }
        }
        return edgePoints;
    }
}