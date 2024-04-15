package mainapp.lab01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Graham
{
    public static MyPoint startPoint;

    public static List<MyPoint> findHull(List<MyPoint> points)
    {
        MyPoint[] points2 = points.toArray(new MyPoint[0]);

        if (points2.length < 3)
        {
            return null;
        }

        int minHeight = 0;
        for (int i = 1; i < points2.length; i++)
        {
            if (points2[i].getY() < points2[minHeight].getY() || (points2[i].getY() == points2[minHeight].getY() && points2[i].getX() < points2[minHeight].getX()))
            {
                minHeight = i;
            }
        }

        MyPoint temp = points2[0];
        points2[0] = points2[minHeight];
        points2[minHeight] = temp;

        startPoint = points2[0];
        Arrays.sort(points2, 1, points2.length, Graham::compare);

        Stack<MyPoint> stack = new Stack<>();
        stack.push(points2[0]);
        stack.push(points2[1]);
        stack.push(points2[2]);

        for (int i = 3; i < points2.length; i++)
        {
            while (stack.size() > 1 && orientation(stack.get(stack.size() - 2), stack.peek(), points2[i]) != 2)
            {
                stack.pop();
            }
            stack.push(points2[i]);
        }

        return new ArrayList<>(stack);
    }

    public static int orientation(MyPoint p, MyPoint q, MyPoint r)
    {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (val == 0)
        {
            return 0;
        }
        return (val > 0) ? 1 : 2; //Zegar lub przeciwnie do zegara
    }

    public static int compare(MyPoint p1, MyPoint p2)
    {
        int o = orientation(startPoint, p1, p2);
        if (o == 0)
        {
            return (startPoint.calcDistance(p1) >= startPoint.calcDistance(p2)) ? -1 : 1;
        }
        return (o == 2) ? -1 : 1;
    }
}
