package org.cg.advancingfont;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public List<MyPoint> findC(MyLine edge)
    {
        List<MyPoint> candidates = new ArrayList<>();
        double a = edge.calcDistance();
        double height = (a * Math.sqrt(3)) / 2;

        MyPoint normal = new MyPoint(edge.end.getY() - edge.start.getY(), -(edge.end.getX() - edge.start.getX()));
        double length = Math.sqrt(normal.x * normal.x + normal.y * normal.y);
        normal.x /= length;
        normal.y /= length;

        MyPoint midpoint = new MyPoint((edge.start.getX() + edge.end.getX()) / 2, (edge.start.getY() + edge.end.getY()) / 2);

        MyPoint candidate1 = new MyPoint(midpoint.x + normal.x * height, midpoint.y + normal.y * height);
        MyPoint candidate2 = new MyPoint(midpoint.x - normal.x * height, midpoint.y - normal.y * height);

        candidates.add(candidate1);
        candidates.add(candidate2);
        return candidates;
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

    if(mesh == 2) // Dla kosmity
    {
        translatePoints(points, -70, -250);
    }

    // Obwód wzorca
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

    // Lista do przechowywania utworzonych trójkątów – do animacji
    List<MyTriangle> trianglesToAnimate = new ArrayList<>();

    // Zmienna przechowująca pierwszy punkt bieżącego rzędu
    MyPoint rowStart = null;

    while (front.size() > 1 && trianglesDrawn < maxTriangles)
    {
        MyLine edge = selectedEdge(front);
        MyPoint finalPoint = null;

        // Jeśli rowStart nie został jeszcze ustawiony, a krawędź dotyczy pierwszego punktu frontu, ustaw go.
        if (rowStart == null && (edge.start.equals(front.get(0)) || edge.end.equals(front.get(0))))
        {
            rowStart = front.get(0);
        }

        // Jeśli front wskazuje, że kończy się bieżący rząd (np. front.size() == 2)
        // i rowStart jest już ustawiony – wymuś użycie rowStart jako finalPoint.
        if (front.size() == 2 && rowStart != null
                && !rowStart.equals(edge.start) && !rowStart.equals(edge.end))
        {
            finalPoint = rowStart;
        }
        else
        {
            // Standardowy wybór kandydata dla trójkąta
            List<MyPoint> candidatePoints = findC(edge);
            for (MyPoint candidate : candidatePoints)
            {
                MyPoint neighborhoodPoint = findPointInNeighborhood(candidate, 0.75 * edge.calcDistance(), points);
                MyPoint testPoint = (neighborhoodPoint != null) ? neighborhoodPoint : candidate;
                if (!edge.start.equals(testPoint) && !edge.end.equals(testPoint))
                {
                    MyTriangle candidateTriangle = new MyTriangle(edge.start, edge.end, testPoint);
                    boolean intersectsExisting = false;

                    for (MyTriangle existing : existingTriangles)
                    {
                        if (candidateTriangle.intersects(existing))
                        {
                            intersectsExisting = true;
                            break;
                        }
                    }

                    if (!intersectsExisting)
                    {
                        finalPoint = testPoint;
                        break;
                    }
                }
            }
        }

        // Jeśli udało się wybrać finalPoint, tworzymy trójkąt
        if (finalPoint != null)
        {
            MyTriangle triangle = new MyTriangle(edge.start, edge.end, finalPoint);
            existingTriangles.add(triangle);
            trianglesToAnimate.add(triangle);
            updateFront(front, edge, triangle);
            trianglesDrawn++;
        }

        // Jeśli front uległ zmianie wskazującej, że dany rząd się zamknął, resetujemy rowStart
        double epsilon = 1e-6;
        if (front.size() > 1 && front.get(0).calcDistance(front.get(front.size() - 1)) < epsilon)
        {
            rowStart = null;
        }


        front.remove(edge.start);
    }

    // Animacja
    final int[] index = {0};
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), event ->
    {
        if (index[0] < trianglesToAnimate.size())
        {
            MyTriangle triangle = trianglesToAnimate.get(index[0]);
            Line line0 = new Line(triangle.w1.getX(), triangle.w1.getY(), triangle.w2.getX(), triangle.w2.getY());
            Line line1 = new Line(triangle.w1.getX(), triangle.w1.getY(), triangle.w3.getX(), triangle.w3.getY());
            Line line2 = new Line(triangle.w2.getX(), triangle.w2.getY(), triangle.w3.getX(), triangle.w3.getY());

            // Jeśli to ostatni trójkąt - czerwony
            if (index[0] == trianglesToAnimate.size() - 1)
            {
                line0.setStroke(Color.RED);
                line1.setStroke(Color.RED);
                line2.setStroke(Color.RED);
            }
            root.getChildren().addAll(line0, line1, line2);
            index[0]++;
        }
    }));
    timeline.setCycleCount(trianglesToAnimate.size());
    timeline.play();

    Scanner scanner = new Scanner(System.in);
    System.out.println("Do you want to save mesh to a file? 1 - yes, 0 - no");
    int save = scanner.nextInt();
    if (save == 1) {
        saveData(existingTriangles, "mesh.txt");
    } else {
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
        System.out.println("Points read: " + points.size());
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