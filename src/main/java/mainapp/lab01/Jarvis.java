package mainapp.lab01;
import java.util.ArrayList;
import java.util.List;

public class Jarvis
{
    public static List<MyPoint> findHull(List<MyPoint> points)
    {
        if (points.size() < 3)
        {
            return null;
        }

        ArrayList<MyPoint> hull = new ArrayList<>();
        int maxLeft = 0;
        for (int i = 1; i < points.size(); i++)
        {
            if (points.get(i).x < points.get(maxLeft).x)
            {
                maxLeft = i;
            }
        }

        int p = maxLeft, q;
        do {
            hull.add(points.get(p));
            q = (p + 1) % points.size();

            for (int i = 0; i < points.size(); i++)
            {
                int orient = orientation(points.get(p), points.get(i), points.get(q));
                if (orient == 2 || (orient == 0 && points.get(i).calcDistance(points.get(p)) > points.get(q).calcDistance(points.get(p))))
                {
                    q = i;
                }
            }

            p = q;
        } while (p != maxLeft);

        return hull;
    }

    public static int orientation(MyPoint p, MyPoint q, MyPoint r)
    {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (val == 0)
        {
            return 0;
        }
        return (val > 0)? 1: 2; //zgodnie z zegarem lub przeciwnie do zegara
    }
}
