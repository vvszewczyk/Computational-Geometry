package mainapp.lab01;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.util.List;

public class MyPolygon
{
    List<MyPoint> vertices;
    List<MyLine> lines;

    public MyPolygon(List<MyPoint> vertices)
    {
        this.vertices = vertices;
        this.lines = new ArrayList<>();
        createLines();
    }

    public Polygon toPolygon()
    {
        Polygon polygon = new Polygon();
        for (MyPoint vertex : vertices)
        {
            polygon.getPoints().addAll(vertex.x, vertex.y);
        }
        return polygon;
    }

    private void createLines()
    {
        for (int i = 0; i < vertices.size(); i++)
        {
            MyPoint start = vertices.get(i);
            MyPoint koniec = vertices.get((i + 1) % vertices.size());
            MyLine line = new MyLine(start, koniec);
            lines.add(line);
        }
}
    public void printPolygonInfo()
    {
        System.out.println("Info:");
        System.out.println("Vertices:");
        for (MyPoint vertex : vertices) {
            System.out.println(vertex);
        }
        System.out.println("Lines:");
        for (MyLine line : lines)
        {
            System.out.println(line);
        }
    }

    boolean belongsToPolygon(MyPoint p)
    {
        int counter = 0;
        double x = p.getX();
        double y = p.getY();
        double epsilon = 1e-1;

        for (int i = 0; i < vertices.size(); i++)
        {
            MyPoint v1 = vertices.get(i);
            MyPoint v2 = vertices.get((i + 1) % vertices.size());

            //point on the vertex
            if ((Math.abs(x - v1.getX()) < epsilon && Math.abs(y - v1.getY()) < epsilon) || (Math.abs(x - v2.getX()) < epsilon && Math.abs(y - v2.getY()) < epsilon)) {
                return true;
            }

            //point on the line
            //x=x1+t(x2−x1)=x1+t*dx
            //y=y1+t(y2−y1)=y1+t*dy
            double dx = v2.getX() - v1.getX();
            double dy = v2.getY() - v1.getY();
            if (dx != 0 || dy != 0)
            {
                double t = ((x - v1.getX()) * dx + (y - v1.getY()) * dy) / (dx * dx + dy * dy);
                double x_on_line = v1.getX() + t * dx;
                double y_on_line = v1.getY() + t * dy;

                //<0, 1>
                if (t >= 0 && t <= 1 && Math.abs(x - x_on_line) < epsilon && Math.abs(y - y_on_line) < epsilon)
                {
                    return true;
                }
            }


            if ((v1.getY() > y) != (v2.getY() > y))
            {
                double x_intersect = (v2.getX() - v1.getX()) * (y - v1.getY()) / (v2.getY() - v1.getY()) + v1.getX();
                if (x < x_intersect || Math.abs(x - x_intersect) < epsilon)
                {
                    counter++;
                }
            }
        }

        return counter % 2 == 1;
    }
}
