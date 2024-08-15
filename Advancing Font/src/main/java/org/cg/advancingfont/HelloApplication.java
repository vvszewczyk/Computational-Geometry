package org.cg.advancingfont;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class HelloApplication extends Application
{
    // Przed uruchomieniem upewnić się, że ścieżki do siatek są podane prawidłowo
    @Override
    public void start(Stage stage) throws IOException
    {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);
        stage.setResizable(false);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter mesh to draw: ");
        System.out.println("1 - Ghost");
        System.out.println("2 - Alien");
        System.out.println("3 - Circle");
        int mesh = scanner.nextInt();
        System.out.println("Enter mesh density (1-10): ");
        int density = scanner.nextInt();
        System.out.println("Enter the number of max triangles");
        int maxTriangles = scanner.nextInt();


        switch(mesh)
        {
            case 1:
                String path1 = "D:\\Github\\Computational-Geometry-s\\Advancing Font\\src\\main\\resources\\org\\cg\\advancingfont\\latwy_wariant.txt";
                try
                {
                    drawMesh(root, path1, density, maxTriangles, mesh);
                }
                catch (IOException e)
                {
                    throw new RuntimeException();
                }
                break;
            case 2:
                String path2 = "D:\\Github\\Computational-Geometry-s\\Advancing Font\\src\\main\\resources\\org\\cg\\advancingfont\\trudny_wariant.txt";
                try
                {
                    drawMesh(root, path2, density, maxTriangles, mesh);
                }
                catch (IOException e)
                {
                    throw new RuntimeException();
                }
                break;
            case 3:
                String path3 = "D:\\Github\\Computational-Geometry-s\\Advancing Font\\src\\main\\resources\\org\\cg\\advancingfont\\circle.txt";
                try
                {
                    drawMesh(root, path3, density, maxTriangles, mesh);
                }
                catch (IOException e)
                {
                    throw new RuntimeException();
                }
        }

        stage.setTitle("GO - Advancing Font");
        stage.setScene(scene);
        stage.show();
    }

    // Przeskalowanie punktów z pliku względem okienka
    public static void translatePoints(List<MyPoint> points, double dx, double dy)
    {
        for (MyPoint point : points)
        {
            point.setX(point.getX() + dx);
            point.setY(point.getY() + dy);
        }
    }

    // Wybieranie krawędzi z listy
    public MyLine selectedEdge(List<MyPoint> front)
    {
        if (front.size() < 2)
        {
            throw new IllegalArgumentException("Front must have at least two points to form an edge.");
        }

        System.out.println("[SELECTED EDGE] - CHOSEN POINTS: "+ front.get(0) + " " + front.get(1));
        return new MyLine(front.get(0), front.get(1));
    }

    // Znajdowanie punktu (wierzchołka) C dla trójkąta równobocznego
    public MyPoint findC(MyLine edge)
    {
        double a = edge.calcDistance();
        double height = (a*Math.sqrt(3))/2;

        MyPoint normal = new MyPoint(edge.end.getY() - edge.start.getY(), -(edge.end.getX() - edge.start.getX()));

        double length = Math.sqrt(normal.x * normal.x + normal.y * normal.y);
        normal.x /= length;
        normal.y /= length;

        MyPoint midpoint = new MyPoint((edge.start.getX() + edge.end.getX()) / 2, (edge.start.getY() + edge.end.getY()) / 2);

        return new MyPoint(midpoint.x + normal.x * height, midpoint.y + normal.y * height);
    }


    // Sprawdzenie sąsiedztwa punktu C
    public MyPoint findPointInNeighborhood(MyPoint C, double radius, List<MyPoint> allPoints)
    {
        for (MyPoint point : allPoints)
        {
            if (point.calcDistance(C) <= radius)
            {
                System.out.println("[FIND POINT IN THE NEIGHBOURHOOD] - P: "+ point);
                return point;
            }
        }
        return null;
    }


    // Aktualizowanie frontu
    public void updateFront(List<MyPoint> front, MyLine edge, MyTriangle triangle)
    {
        front.remove(edge.start);

        if (!front.contains(triangle.w3))
        {
            front.add(triangle.w3); // Dla zachowania kolejności
        }
    }

// Rysowanie siatki, główna pętla programu
    public void drawMesh(Pane root, String path, int density, int maxTriangles, int mesh) throws IOException
    {
        List<MyPoint> points = readData(path, density);

        if(mesh == 2)
        {
            translatePoints(points, -70, -250);
        }

        for (int i = 0; i < points.size(); i++)
        {
            MyPoint p1 = points.get(i);
            MyPoint p2 = points.get((i + 1) % points.size());
            Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            line.setStroke(Color.BLUE);
            root.getChildren().add(line);
        }

        int trianglesDrawn = 0;
        List<MyTriangle> existingTriangles = new ArrayList<>();
        List<MyPoint> front = new ArrayList<>(points);

        while (front.size() > 1 && trianglesDrawn < maxTriangles)
        {
            MyLine edge = selectedEdge(front);
            MyPoint pointC = findC(edge);
            MyPoint neighborhoodPoint = findPointInNeighborhood(pointC, edge.calcDistance()/2, points);
            System.out.println("[DRAW OBJECT] - CHOSEN C: " + pointC);
            System.out.println("[DRAW OBJECT] - CHOSEN POINT FROM NEIGHBOURHOOD: " + neighborhoodPoint);
            MyPoint finalPoint = (neighborhoodPoint != null) ? neighborhoodPoint : pointC;

            if (!edge.start.equals(finalPoint) && !edge.end.equals(finalPoint))
            {
                MyTriangle triangle = new MyTriangle(edge.start, edge.end, finalPoint);
                points.add(finalPoint);

                boolean intersectsExisting = false;
                for (MyTriangle existing : existingTriangles)
                {
                    if (triangle.intersects(existing))
                    {
                        intersectsExisting = true;
                        break;
                    }
                }

                if (!intersectsExisting)
                {
                    Line line0 = new Line(edge.start.getX(), edge.start.getY(), edge.end.getX(), edge.end.getY());
                    Line line1 = new Line(edge.start.getX(), edge.start.getY(), finalPoint.getX(), finalPoint.getY());
                    Line line2 = new Line(edge.end.getX(), edge.end.getY(), finalPoint.getX(), finalPoint.getY());

                    if (trianglesDrawn == maxTriangles - 1)
                    {
                        line0.setStroke(Color.RED);
                        line1.setStroke(Color.RED);
                        line2.setStroke(Color.RED);
                    }

                    root.getChildren().addAll(line0, line1, line2);

                    existingTriangles.add(triangle);
                    updateFront(front, edge, triangle);

                    trianglesDrawn++;
                    if (trianglesDrawn >= maxTriangles)
                    {
                        break;
                    }
                }

                front.remove(edge.start);
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to save mesh to a file? 1 - yes, 0 - no");
        int save = scanner.nextInt();
        if (save == 1)
        {
            saveData(existingTriangles, "mesh.txt");
        }
        else
        {
            System.out.println("Data will not be saved.");
        }
        scanner.close();
    }


    // Odczytanie współrzędnych siatki wejściowej z pliku
    public List<MyPoint> readData(String filename, int pointsToRead) throws FileNotFoundException
    {
        List<MyPoint> points = new ArrayList<>();
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        int pointIndex = 0;

        if (scanner.hasNextLine())
        {
            scanner.nextLine();
        }

        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (pointIndex % pointsToRead == 0)
            {
                String[] coordinates = line.split("\\s+");
                double x = Double.parseDouble(coordinates[0].trim());
                double y = Double.parseDouble(coordinates[1].trim());
                points.add(new MyPoint(x, y));
            }
            pointIndex++;
        }

        scanner.close();
        System.out.println("Wczytano " + points.size() + " punktów.");
        return points;
    }

    // Zapisanie utworzonej (wyjściowej) siatki do pliku txt
    public void saveData(List<MyTriangle> triangles, String filename)
    {
        Set<MyPoint> uniquePoints = new HashSet<>();

        for (MyTriangle triangle : triangles)
        {
            uniquePoints.add(triangle.w1);
            uniquePoints.add(triangle.w2);
            uniquePoints.add(triangle.w3);
        }

        try (FileWriter writer = new FileWriter(filename))
        {
            writer.write(uniquePoints.size() + "\n");

            for (MyPoint point : uniquePoints)
            {
                writer.write(point.getX() + " " + point.getY() + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        launch();
    }
}