package mainapp.lab01;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class HelloApplication extends Application
{
    private boolean isPaused = false;
    private long pauseTime = 0;
    private final long[] lastUpdateTime = {0};
    public void togglePause(long now)
    {
        if (isPaused)
        {
            isPaused = false;
            lastUpdateTime[0] += (now - pauseTime);
        }
        else
        {
            isPaused = true;
            pauseTime = now;
        }
        System.out.println(isPaused ? "Simulation paused" : "Simulation resumed");
    }

    @Override
    public void start(Stage stage)
    {
        System.out.println("Choose your option: ");
        System.out.println("1 - draw a line based on 2 points");
        System.out.println("2 - check the point's affiliation to line");
        System.out.println("3 - check the point's affiliation to segment");
        System.out.println("4 - move the line by the vector");
        System.out.println("5 - make a mirror image of a point (and his distance)");
        System.out.println("6 - check common point of straight lines");
        System.out.println("7 - check the common point of the lines with the given ends");
        System.out.println("8 - make triangle from given lines");
        System.out.println("9 - check if point belongs to triangle");
        System.out.println("Q - check if point belongs to polygon");
        System.out.println("W - Draw hull for cloud of points");
        System.out.println("E - Battle visualization");
        System.out.println("ESC - exit");


        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);
        stage.setResizable(false);


        scene.setOnKeyPressed(event ->
        {
            switch (event.getCode())
            {
                case DIGIT1:
                    drawLine(root);
                    break;
                case DIGIT2:
                    pointLocation(root);
                    break;
                case DIGIT3:
                    pointInSegment(root);
                    break;
                case DIGIT4:
                    translateLine(root);
                    break;
                case DIGIT5:
                    mirrorPointDistance(root);
                    break;
                case DIGIT6:
                    isCCCP(root);
                    break;
                case DIGIT7:
                    isCCCPSegment(root);
                    break;
                case DIGIT8:
                    laneTriangle(root);
                    break;
                case DIGIT9:
                    belongsToTriangle(root);
                    break;
                case KeyCode.Q:
                    belongsToPolygon(root);
                    break;
                case KeyCode.W:
                    try
                    {
                        drawConvexHull(root);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                    break;
                case KeyCode.E:
                    game(root);
                    break;
                case KeyCode.SPACE:
                    long now = System.nanoTime();
                    togglePause(now);
                    break;
                case ESCAPE:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
            }
        });

        stage.setTitle("GO - LAB_01/02/03/04");
        stage.setScene(scene);
        stage.show();
    }

    private void drawLine(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coordinates for two points (format: x1,y1;x2,y2):");
        dialog.setContentText("Please enter coordinates:");

        // user replies
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input ->
        {
            String[] pointData = input.split(";");

            if (pointData.length == 2)
            {
                String[] point1 = pointData[0].split(",");
                String[] point2 = pointData[1].split(",");

                if (point1.length == 2 && point2.length == 2)
                {
                    try
                    {
                        double x1 = Double.parseDouble(point1[0]);
                        double y1 = Double.parseDouble(point1[1]);
                        double x2 = Double.parseDouble(point2[0]);
                        double y2 = Double.parseDouble(point2[1]);

                        MyPoint p1 = new MyPoint(x1, y1);
                        MyPoint p2 = new MyPoint(x2, y2);

                        MyLine myline = new MyLine(p1, p2);
                        System.out.println("Point 1 - X: " + p1.getX() + " Y: " + p1.getY());
                        System.out.println("Point 2 - X: " + p2.getX() + " Y: " + p2.getY());

                        Circle point1Circle = new Circle(p1.getX(), p1.getY(), 3);
                        Circle point2Circle = new Circle(p2.getX(), p2.getY(), 3);

                        List<MyPoint> edgePoints = myline.findEdgePoints(800,600);

                        Line line = new Line(edgePoints.get(0).getX(), edgePoints.get(0).getY(), edgePoints.get(1).getX(), edgePoints.get(1).getY());

                        root.getChildren().add(point1Circle);
                        root.getChildren().add(point2Circle);
                        root.getChildren().add(line);

                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid coordinates.");
                    }
                }
                else
                {
                    showAlert("Please enter valid coordinates for both points.");
                }
            }
            else
            {
                showAlert("Please enter coordinates for both points.");
            }
        });
    }
    private void pointLocation(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coefficients A, B, C and coordinates for point (format: a,b,c;x,y)");
        dialog.setContentText("Please enter coefficients and coordinates:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input ->
        {
            System.out.println("Input data: " + input);
            String[] data = input.split(";");
            if (data.length == 2)
            {
                String[] coefficients = data[0].split(",");
                String[] point2 = data[1].split(",");

                if (coefficients.length == 3 && point2.length == 2)
                {
                    try
                    {
                        double a = Double.parseDouble(coefficients[0]);
                        double b = Double.parseDouble(coefficients[1]);
                        double c = Double.parseDouble(coefficients[2]);

                        double x1 = Double.parseDouble(point2[0]);
                        double y1 = Double.parseDouble(point2[1]);
                        System.out.println("Coefficients: a=" + a + ", b=" + b + ", c=" + c);
                        System.out.println("Point: x=" + x1 + ", y=" + y1);

                        MyLine line = new MyLine(a, b, c);
                        MyPoint p = new MyPoint(x1, y1);

                        Circle pointCircle = new Circle(p.getX(), p.getY(), 3);

                        if(line.whichSide(p) == 0)
                        {
                            System.out.println("Is on line ");
                            pointCircle.setFill(Color.RED);
                        }
                        else if(line.whichSide(p) == -1)
                        {
                            System.out.println("On the left ");
                        }
                        else if(line.whichSide(p) == 1)
                        {
                            System.out.println("On the right ");
                        }

                        List<MyPoint> edgePoints = line.findEdgePoints(800,600);
                        if(edgePoints.size() == 2)
                        {
                            Line line2 = new Line(edgePoints.get(0).getX(), edgePoints.get(0).getY(),
                                    edgePoints.get(1).getX(), edgePoints.get(1).getY());

                            root.getChildren().add(line2);
                        }

                        root.getChildren().add(pointCircle);
                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid numbers.");
                    }
                }
                else
                {
                    showAlert("Incorrect number of coefficients or point coordinates.");
                }
            }
            else
            {
                showAlert("Incorrect format of input data.");
            }
        });
    }
    private void pointInSegment(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Points");
        dialog.setHeaderText("Enter coordinates for 3 points (format: x1,y1;x2,y2;x3,y3):");
        dialog.setContentText("Please enter coordinates for 3 points");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input ->
        {
            String[] pointData = input.split(";");

            if (pointData.length == 3)
            {
                String[] point1 = pointData[0].split(",");
                String[] point2 = pointData[1].split(",");
                String[] point3 = pointData[2].split(",");

                if (point1.length == 2 && point2.length == 2 && point3.length == 2)
                {
                    try
                    {
                        double x1 = Double.parseDouble(point1[0]);
                        double y1 = Double.parseDouble(point1[1]);
                        double x2 = Double.parseDouble(point2[0]);
                        double y2 = Double.parseDouble(point2[1]);
                        double x3 = Double.parseDouble(point3[0]);
                        double y3 = Double.parseDouble(point3[1]);

                        MyPoint p1 = new MyPoint(x1, y1);
                        MyPoint p2 = new MyPoint(x2, y2);
                        MyPoint p = new MyPoint(x3, y3);

                        MyLine myline = new MyLine(p1, p2);

                        Circle point1Circle = new Circle(p1.getX(), p1.getY(), 3);
                        Circle point2Circle = new Circle(p2.getX(), p2.getY(), 3);
                        Circle pointCircle = new Circle(p.getX(), p.getY(), 3);

                        Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());

                        if(myline.isInSegment(p, p1, p2))
                        {
                            pointCircle.setFill(Color.RED);
                        }

                        root.getChildren().add(point1Circle);
                        root.getChildren().add(point2Circle);
                        root.getChildren().add(pointCircle);
                        root.getChildren().add(line);
                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid coordinates.");
                    }
                }
                else
                {
                    showAlert("Please enter valid coordinates for all three points.");
                }
            }
            else
            {
                showAlert("Please enter coordinates for all three points.");
            }
        });
    }
    private void translateLine(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coefficients A, B, C and a vector to shift (format: a,b,c;x,y)");
        dialog.setContentText("Please enter coefficients and vector:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input ->
        {
            System.out.println("Input data: " + input);
            String[] data = input.split(";");
            if (data.length == 2)
            {
                String[] coefficients = data[0].split(",");
                String[] vector = data[1].split(",");

                if (coefficients.length == 3 && vector.length == 2)
                {
                    try
                    {
                        double a = Double.parseDouble(coefficients[0]);
                        double b = Double.parseDouble(coefficients[1]);
                        double c = Double.parseDouble(coefficients[2]);

                        double x1 = Double.parseDouble(vector[0]);
                        double y1 = Double.parseDouble(vector[1]);

                        System.out.println("Coefficients: a=" + a + ", b=" + b + ", c=" + c);
                        System.out.println("Vector: x=" + x1 + ", y=" + y1);


                        MyLine line = new MyLine(a, b, c);
                        MyLine translatedLine = line.translate(x1, y1);

                        List<MyPoint> edgePoints1 = line.findEdgePoints(800,600);
                        List<MyPoint> edgePointsV = translatedLine.findEdgePoints(800,600);

                        Line line3 = new Line(edgePoints1.get(0).getX(), edgePoints1.get(0).getY(), edgePoints1.get(1).getX(), edgePoints1.get(1).getY());
                        Line lineV = new Line(edgePointsV.get(0).getX(), edgePointsV.get(0).getY(), edgePointsV.get(1).getX(), edgePointsV.get(1).getY());

                        lineV.setStroke(Color.GREEN);
                        root.getChildren().add(line3);
                        root.getChildren().add(lineV);


                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid numbers.");
                    }
                }
                else
                {
                    showAlert("Incorrect number of coefficients or vector coordinates.");
                }
            }
            else
            {
                showAlert("Incorrect format of input data.");
            }
        });
    }
    private void mirrorPointDistance(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coefficients A, B, C and coordinates for point (format: a,b,c;x,y)");
        dialog.setContentText("Please enter coefficients and coordinates:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input ->
        {
            System.out.println("Input data: " + input);
            String[] data = input.split(";");
            if (data.length == 2)
            {
                String[] coefficients = data[0].split(",");
                String[] point2 = data[1].split(",");

                if (coefficients.length == 3 && point2.length == 2)
                {
                    try
                    {
                        double a = Double.parseDouble(coefficients[0]);
                        double b = Double.parseDouble(coefficients[1]);
                        double c = Double.parseDouble(coefficients[2]);

                        double x1 = Double.parseDouble(point2[0]);
                        double y1 = Double.parseDouble(point2[1]);
                        System.out.println("Coefficients: a=" + a + ", b=" + b + ", c=" + c);
                        System.out.println("Point: x=" + x1 + ", y=" + y1);


                        MyLine line = new MyLine(a, b, c);
                        MyPoint p = new MyPoint(x1, y1);
                        double distance = line.pointDistance(p);
                        System.out.println("Distance of the point/mirror point from the straight line: " + distance);
                        MyPoint pMirrored = line.mirror(line, p);

                        List<MyPoint> edgePoints = line.findEdgePoints(800,600);

                        Circle pointCircle = new Circle(p.getX(), p.getY(), 3);
                        Circle pointCircleMirrored = new Circle(pMirrored.getX(), pMirrored.getY(), 3);
                        pointCircleMirrored.setFill(Color.RED);


                        Line line2 = new Line(edgePoints.get(0).getX(), edgePoints.get(0).getY(), edgePoints.get(1).getX(), edgePoints.get(1).getY());
                        root.getChildren().add(pointCircle);
                        root.getChildren().add(pointCircleMirrored);
                        root.getChildren().add(line2);


                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid numbers.");
                    }
                }
                else
                {
                    showAlert("Incorrect number of coefficients or point coordinates.");
                }
            }
            else
            {
                showAlert("Incorrect format of input data.");
            }
        });
    }
    private void isCCCP(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coefficients A1, B1, C1 and A2, B2, C2 (format: a1,b1,c1;a2,b2,c2)");
        dialog.setContentText("Please enter coefficients:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input ->
        {
            System.out.println("Input data: " + input);
            String[] data = input.split(";");
            if (data.length == 2)
            {
                String[] coefficients1 = data[0].split(",");
                String[] coefficients2 = data[1].split(",");

                if (coefficients1.length == 3 && coefficients2.length == 3)
                {
                    try
                    {
                        double a1 = Double.parseDouble(coefficients1[0]);
                        double b1 = Double.parseDouble(coefficients1[1]);
                        double c1 = Double.parseDouble(coefficients1[2]);

                        double a2 = Double.parseDouble(coefficients2[0]);
                        double b2 = Double.parseDouble(coefficients2[1]);
                        double c2 = Double.parseDouble(coefficients2[2]);

                        System.out.println("Coefficients: a1=" + a1 + ", b1=" + b1 + ", c1=" + c1);
                        System.out.println("Coefficients: a2=" + a2 + ", b2=" + b2 + ", c2=" + c2);

                        MyLine line1 = new MyLine(a1, b1, c1);
                        MyLine line2 = new MyLine(a2, b2, c2);

                        MyPoint p = line1.CCCP(line1, line2);
                        double angle = line1.calcAngle(line1, line2);
                        System.out.println("Angle: "+ angle);

                        List<MyPoint> edgePoints1 = line1.findEdgePoints(800,600);
                        List<MyPoint> edgePoints2 = line2.findEdgePoints(800,600);

                        if(p != null)
                        {
                            if(line1.isInLine(p) && line2.isInLine(p))
                            {
                                Circle pointCircle = new Circle(p.getX(), p.getY(), 3);
                                pointCircle.setFill(Color.RED);
                                root.getChildren().add(pointCircle);
                            }
                        }

                        Line lineOne = new Line(edgePoints1.get(0).getX(), edgePoints1.get(0).getY(), edgePoints1.get(1).getX(), edgePoints1.get(1).getY());
                        Line lineTwo = new Line(edgePoints2.get(0).getX(), edgePoints2.get(0).getY(), edgePoints2.get(1).getX(), edgePoints2.get(1).getY());

                        root.getChildren().add(lineOne);
                        root.getChildren().add(lineTwo);
                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid numbers.");
                    }
                }
                else
                {
                    showAlert("Incorrect number of coefficients.");
                }
            }
            else
            {
                showAlert("Incorrect format of input data.");
            }
        });
    }
    private void isCCCPSegment(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Points");
        dialog.setHeaderText("Enter coordinates for 4 points (format: x1,y1;x2,y2;x3,y3;x4,y4):");
        dialog.setContentText("Please enter coordinates for 4 points");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input ->
        {
            String[] pointData = input.split(";");

            if (pointData.length == 4)
            {
                String[] point1 = pointData[0].split(",");
                String[] point2 = pointData[1].split(",");
                String[] point3 = pointData[2].split(",");
                String[] point4 = pointData[3].split(",");

                if (point1.length == 2 && point2.length == 2 && point3.length == 2 && point4.length == 2)
                {
                    try
                    {
                        double x1 = Double.parseDouble(point1[0]);
                        double y1 = Double.parseDouble(point1[1]);
                        double x2 = Double.parseDouble(point2[0]);
                        double y2 = Double.parseDouble(point2[1]);
                        double x3 = Double.parseDouble(point3[0]);
                        double y3 = Double.parseDouble(point3[1]);
                        double x4 = Double.parseDouble(point4[0]);
                        double y4 = Double.parseDouble(point4[1]);

                        MyPoint p1 = new MyPoint(x1, y1);
                        MyPoint p2 = new MyPoint(x2, y2);
                        MyPoint p3 = new MyPoint(x3, y3);
                        MyPoint p4 = new MyPoint(x4, y4);

                        MyLine myline1 = new MyLine(p1, p2);
                        MyLine myline2 = new MyLine(p3, p4);

                        Circle point1Circle = new Circle(p1.getX(), p1.getY(), 3);
                        Circle point2Circle = new Circle(p2.getX(), p2.getY(), 3);
                        Circle point3Circle = new Circle(p3.getX(), p3.getY(), 3);
                        Circle point4Circle = new Circle(p4.getX(), p4.getY(), 3);

                        Line line1 = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                        Line line2 = new Line(p3.getX(), p3.getY(), p4.getX(), p4.getY());

                        MyPoint p = myline1.CCCP(myline1, myline2);


                        if(p != null)
                        {
                            if (myline1.isInSegment(p, p1, p2) && myline2.isInSegment(p, p3, p4))
                            {
                                Circle pointCircle = new Circle(p.getX(), p.getY(), 3);
                                pointCircle.setFill(Color.RED);
                                root.getChildren().add(pointCircle);
                            }
                        }

                        root.getChildren().add(point1Circle);
                        root.getChildren().add(point2Circle);
                        root.getChildren().add(point3Circle);
                        root.getChildren().add(point4Circle);
                        root.getChildren().add(line1);
                        root.getChildren().add(line2);
                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid coordinates.");
                    }
                }
                else
                {
                    showAlert("Please enter valid coordinates for all four points.");
                }
            }
            else
            {
                showAlert("Please enter coordinates for all four points.");
            }
        });
    }
    private void laneTriangle(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coefficients (A1, B1, C1), (A2, B2, C2) and (A3, B3, C3) (format: a1,b1,c1;a2,b2,c2;a3,b3,c3)");
        dialog.setContentText("Please enter coefficients:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input ->
        {
            System.out.println("Input data: " + input);
            String[] data = input.split(";");
            if (data.length == 3)
            {
                String[] coefficients1 = data[0].split(",");
                String[] coefficients2 = data[1].split(",");
                String[] coefficients3 = data[2].split(",");

                if (coefficients1.length == 3 && coefficients2.length == 3 && coefficients3.length == 3)
                {
                    try
                    {
                        double a1 = Double.parseDouble(coefficients1[0]);
                        double b1 = Double.parseDouble(coefficients1[1]);
                        double c1 = Double.parseDouble(coefficients1[2]);

                        double a2 = Double.parseDouble(coefficients2[0]);
                        double b2 = Double.parseDouble(coefficients2[1]);
                        double c2 = Double.parseDouble(coefficients2[2]);

                        double a3 = Double.parseDouble(coefficients3[0]);
                        double b3 = Double.parseDouble(coefficients3[1]);
                        double c3 = Double.parseDouble(coefficients3[2]);

                        System.out.println("Coefficients: a1=" + a1 + ", b1=" + b1 + ", c1=" + c1);
                        System.out.println("Coefficients: a2=" + a2 + ", b2=" + b2 + ", c2=" + c2);
                        System.out.println("Coefficients: a3=" + a3 + ", b3=" + b3 + ", c3=" + c3);

                        MyLine line1 = new MyLine(a1, b1, c1);
                        MyLine line2 = new MyLine(a2, b2, c2);
                        MyLine line3 = new MyLine(a3, b3, c3);

                        MyTriangle t = MyTriangle.makeTriangle(line1, line2, line3);
                        double area = t.calcTriangleArea();
                        System.out.println("Area: "+ area);

                        MyLine trline1 = new MyLine(t.w1, t.w2);
                        MyLine trline2 = new MyLine(t.w2, t.w3);
                        MyLine trline3 = new MyLine(t.w3, t.w1);

                        List<MyPoint> edgePoints1 = trline1.findEdgePoints(800,600);
                        List<MyPoint> edgePoints2 = trline2.findEdgePoints(800,600);
                        List<MyPoint> edgePoints3 = trline3.findEdgePoints(800,600);


                        Circle pointCircle1 = new Circle(t.w1.getX(), t.w1.getY(), 3);
                        Circle pointCircle2 = new Circle(t.w2.getX(), t.w2.getY(), 3);
                        Circle pointCircle3 = new Circle(t.w3.getX(), t.w3.getY(), 3);


                        Line lineOne = new Line(edgePoints1.get(0).getX(), edgePoints1.get(0).getY(), edgePoints1.get(1).getX(), edgePoints1.get(1).getY());
                        Line lineTwo = new Line(edgePoints2.get(0).getX(), edgePoints2.get(0).getY(), edgePoints2.get(1).getX(), edgePoints2.get(1).getY());
                        Line lineThree = new Line(edgePoints3.get(0).getX(), edgePoints3.get(0).getY(), edgePoints3.get(1).getX(), edgePoints3.get(1).getY());

                        root.getChildren().add(lineOne);
                        root.getChildren().add(lineTwo);
                        root.getChildren().add(lineThree);
                        root.getChildren().add(pointCircle1);
                        root.getChildren().add(pointCircle2);
                        root.getChildren().add(pointCircle3);
                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid numbers.");
                    }
                }
                else
                {
                    showAlert("Incorrect number of coefficients.");
                }
            }
            else
            {
                showAlert("Incorrect format of input data.");
            }
        });
    }
    private void belongsToTriangle(Pane root)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input data");
        dialog.setHeaderText("Enter coefficients (A1, B1, C1), (A2, B2, C2), (A3, B3, C3) and point (x1,x2) (format: a1,b1,c1;a2,b2,c2;a3,b3,c3;x1,x2)");
        dialog.setContentText("Please enter coefficients and point:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input ->
        {
            System.out.println("Input data: " + input);
            String[] data = input.split(";");
            if (data.length == 4)
            {
                String[] coefficients1 = data[0].split(",");
                String[] coefficients2 = data[1].split(",");
                String[] coefficients3 = data[2].split(",");
                String[] point = data[3].split(",");

                if (coefficients1.length == 3 && coefficients2.length == 3 && coefficients3.length == 3 && point.length == 2)
                {
                    try
                    {
                        double a1 = Double.parseDouble(coefficients1[0]);
                        double b1 = Double.parseDouble(coefficients1[1]);
                        double c1 = Double.parseDouble(coefficients1[2]);

                        double a2 = Double.parseDouble(coefficients2[0]);
                        double b2 = Double.parseDouble(coefficients2[1]);
                        double c2 = Double.parseDouble(coefficients2[2]);

                        double a3 = Double.parseDouble(coefficients3[0]);
                        double b3 = Double.parseDouble(coefficients3[1]);
                        double c3 = Double.parseDouble(coefficients3[2]);

                        double x1 = Double.parseDouble(point[0]);
                        double y1 = Double.parseDouble(point[1]);

                        System.out.println("Coefficients: a1=" + a1 + ", b1=" + b1 + ", c1=" + c1);
                        System.out.println("Coefficients: a2=" + a2 + ", b2=" + b2 + ", c2=" + c2);
                        System.out.println("Coefficients: a3=" + a3 + ", b3=" + b3 + ", c3=" + c3);
                        System.out.println("Point x: "+x1+" y: "+y1);

                        MyLine line1 = new MyLine(a1, b1, c1);
                        MyLine line2 = new MyLine(a2, b2, c2);
                        MyLine line3 = new MyLine(a3, b3, c3);
                        MyPoint p = new MyPoint(x1,y1);

                        MyTriangle t = MyTriangle.makeTriangle(line1, line2, line3);
                        t.belongsToTriangleAreas(p);
                        System.out.println(t.belongsToTriangleSides(p));

                        MyLine trline1 = new MyLine(t.w1, t.w2);
                        MyLine trline2 = new MyLine(t.w2, t.w3);
                        MyLine trline3 = new MyLine(t.w3, t.w1);

                        List<MyPoint> edgePoints1 = trline1.findEdgePoints(800,600);
                        List<MyPoint> edgePoints2 = trline2.findEdgePoints(800,600);
                        List<MyPoint> edgePoints3 = trline3.findEdgePoints(800,600);


                        Circle pointCircle1 = new Circle(t.w1.getX(), t.w1.getY(), 3);
                        Circle pointCircle2 = new Circle(t.w2.getX(), t.w2.getY(), 3);
                        Circle pointCircle3 = new Circle(t.w3.getX(), t.w3.getY(), 3);
                        Circle pointCircle4 = new Circle(x1,y1, 3);
                        pointCircle4.setFill(Color.RED);


                        Line lineOne = new Line(edgePoints1.get(0).getX(), edgePoints1.get(0).getY(), edgePoints1.get(1).getX(), edgePoints1.get(1).getY());
                        Line lineTwo = new Line(edgePoints2.get(0).getX(), edgePoints2.get(0).getY(), edgePoints2.get(1).getX(), edgePoints2.get(1).getY());
                        Line lineThree = new Line(edgePoints3.get(0).getX(), edgePoints3.get(0).getY(), edgePoints3.get(1).getX(), edgePoints3.get(1).getY());

                        root.getChildren().add(lineOne);
                        root.getChildren().add(lineTwo);
                        root.getChildren().add(lineThree);
                        root.getChildren().add(pointCircle1);
                        root.getChildren().add(pointCircle2);
                        root.getChildren().add(pointCircle3);
                        root.getChildren().add(pointCircle4);
                    }
                    catch (NumberFormatException e)
                    {
                        showAlert("Please enter valid numbers.");
                    }
                }
                else
                {
                    showAlert("Incorrect number of coefficients.");
                }
            }
            else
            {
                showAlert("Incorrect format of input data.");
            }
        });
    }
    private void belongsToPolygon(Pane root)
    {
        TextInputDialog verticesDialog = new TextInputDialog();
        verticesDialog.setTitle("Input data");
        verticesDialog.setHeaderText("Enter the number of vertices for the polygon:");
        verticesDialog.setContentText("Number of vertices:");
        Optional<String> verticesResult = verticesDialog.showAndWait();

        verticesResult.ifPresent(numberOfVerticesInput ->
        {
            try
            {
                int numberOfVertices = Integer.parseInt(numberOfVerticesInput);
                TextInputDialog pointsDialog = new TextInputDialog();
                pointsDialog.setTitle("Input data");
                pointsDialog.setHeaderText("Enter coordinates for all vertices followed by the point to check (format: x1,y1;x2,y2;...;xn,yn;xp,yp):");
                pointsDialog.setContentText("Coordinates:");
                Optional<String> pointsResult = pointsDialog.showAndWait();
                List<MyPoint> vertices = new ArrayList<>();
                pointsResult.ifPresent(pointsInput ->
                {
                    String[] allPoints = pointsInput.split(";");
                    if (allPoints.length == numberOfVertices + 1)
                    {
                        try
                        {
                            for (int i = 0; i < numberOfVertices; i++)
                            {
                                String[] coordinates = allPoints[i].split(",");
                                double x = Double.parseDouble(coordinates[0]);
                                double y = Double.parseDouble(coordinates[1]);
                                vertices.add(new MyPoint(x, y));
                            }

                            String[] checkPointCoordinates = allPoints[numberOfVertices].split(",");
                            double xp = Double.parseDouble(checkPointCoordinates[0]);
                            double yp = Double.parseDouble(checkPointCoordinates[1]);

                            MyPoint checkPoint = new MyPoint(xp, yp);
                            MyPolygon p = new MyPolygon(vertices);
                            Polygon polygon = new Polygon();

                            for (MyPoint element : vertices)
                            {
                                polygon.getPoints().addAll(element.getX(), element.getY());
                            }

                            polygon.setFill(Color.LIGHTGRAY);
                            polygon.setStroke(Color.BLACK);

                            root.getChildren().add(polygon);
                            System.out.println(p.belongsToPolygon(checkPoint));

                            for (int i = 0; i < vertices.size(); i++)
                            {
                                MyPoint start = vertices.get(i);
                                MyPoint end = vertices.get((i + 1) % vertices.size());

                                Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
                                root.getChildren().add(line);

                                Circle circle = new Circle(start.getX(), start.getY(), 3);
                                root.getChildren().add(circle);
                            }
                            Circle checkPointCircle = new Circle(xp, yp, 3, Color.RED);
                            root.getChildren().add(checkPointCircle);


                        }
                        catch (NumberFormatException e)
                        {
                            showAlert("Please enter valid numbers.");
                        }
                    }
                    else
                    {
                        showAlert("The number of vertices plus one check point does not match the input.");
                    }
                });

            }
            catch (NumberFormatException e)
            {
                showAlert("Please enter a valid number of vertices.");
            }
        });
    }
    private void showAlert(String content)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }
    public List<MyPoint> readData(String resourcePath) throws IOException
    {
        List<MyPoint> points = new ArrayList<>();

        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null)
        {
            throw new IllegalArgumentException("Plik nie został znaleziony: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2)
                {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    points.add(new MyPoint(x, y));
                }
            }
        }

        return points;
    }
    public List<MyPoint> scalePoints(List<MyPoint> originalPoints, double scaleFactor)
    {
        List<MyPoint> scaledPoints = new ArrayList<>();

        for (MyPoint p : originalPoints)
        {
            double scaledX = p.getX() * scaleFactor;
            double scaledY = p.getY() * scaleFactor;
            scaledPoints.add(new MyPoint(scaledX, scaledY));
        }

        return scaledPoints;
    }
    public void drawConvexHull(Pane root) throws IOException
    {
        List<MyPoint> hullPoints = readData("/data/krvx.txt");

        List<MyPoint> scaledPoints = scalePoints(hullPoints, 1);

        long startTime = System.nanoTime();
        List<MyPoint> hull = Jarvis.findHull(scaledPoints);
        long endTime = System.nanoTime();
        System.out.println("Jarvis: " + hull.size());
        System.out.println("Time: " + (endTime - startTime) + " ns");

        long startTime2 = System.nanoTime();
        List<MyPoint> hull2 = Graham.findHull(scaledPoints);
        long endTime2 = System.nanoTime();
        System.out.println("Graham: " + hull2.size());
        System.out.println("Time: " + (endTime2 - startTime2) + " ns");


        for(MyPoint p: scaledPoints)
        {
            Circle circle = new Circle(p.getX(), p.getY(), 3);
            root.getChildren().add(circle);
        }

        for (MyPoint p : hull)
        {
            Circle circle = new Circle(p.getX(), p.getY(), 3);
            circle.setFill(Color.RED);
            root.getChildren().add(circle);
        }

        for (int i = 0; i < hull.size(); i++)
        {
            MyPoint p1 = hull.get(i);
            MyPoint p2 = hull.get((i + 1) % hull.size());

            Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            root.getChildren().add(line);
        }
    }

    public void game(Pane root)
    {
        try
        {
            orzel7 spaceCraft = loadSpaceCraftData("/data/2/space_craft1.txt");
            List<srutLaserowy> missiles = loadMissilesData("data/2/missiles1.txt");
            simulateAndVisualizeBattle(root, spaceCraft, missiles);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private orzel7 loadSpaceCraftData(String resourcePath) throws IOException
    {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null)
        {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String positionLine = reader.readLine();
        String velocityLine = reader.readLine();
        reader.close();

        String[] positionData = positionLine.split("\\s+");
        String[] velocityData = velocityLine.split("\\s+");

        MyPoint position = new MyPoint(Double.parseDouble(positionData[0]), Double.parseDouble(positionData[1]));
        MyPoint velocity = new MyPoint(Double.parseDouble(velocityData[0]), Double.parseDouble(velocityData[1]));

        List<MyPoint> shape = readData("/data/2/craft1_ksztalt.txt");

        return new orzel7(position, velocity, shape);
    }
    private List<srutLaserowy> loadMissilesData(String resourcePath) throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null)
        {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<srutLaserowy> missiles = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null)
        {
            String[] data = line.split("\\s+");
            double detectionTime = Double.parseDouble(data[0]);
            MyPoint position = new MyPoint(Double.parseDouble(data[1]), Double.parseDouble(data[2]));
            MyPoint velocity = new MyPoint(Double.parseDouble(data[3]), Double.parseDouble(data[4]));
            missiles.add(new srutLaserowy(detectionTime, position, velocity));
        }
        is.close();
        return missiles;
    }
    private void updatePositions(orzel7 spaceCraft, List<srutLaserowy> missiles, double deltaTime, double missileSpeedMultiplier)
    {
        //Obliczanie przesunięcia na podstawie prędkości i deltaTime
        double dx = spaceCraft.velocity.x * deltaTime;
        double dy = spaceCraft.velocity.y * deltaTime;

        //Przesuwanie kształtu statku
        for (MyPoint point : spaceCraft.shape)
        {
            point.x += dx;
            point.y += dy;
        }

        //Przesuwanie pocisków
        for (srutLaserowy missile : missiles)
        {
            missile.position.x += missile.velocity.x * deltaTime * missileSpeedMultiplier;
            missile.position.y += missile.velocity.y * deltaTime * missileSpeedMultiplier;
        }
    }
    private void checkCollisions(orzel7 spaceCraft, List<srutLaserowy> missiles, Pane root)
    {
        List<MyPoint> hull = Graham.findHull(spaceCraft.shape);
        MyPolygon spacecraftPolygon = new MyPolygon(hull);
        for (srutLaserowy missile : missiles)
        {
            if (spacecraftPolygon.belongsToPolygon(missile.position))
            {
                Circle collisionCircle = new Circle(missile.position.x, missile.position.y, 5);
                collisionCircle.setFill(Color.YELLOW);
                root.getChildren().add(collisionCircle);
                break;
            }
        }
    }
    private void updateVisuals(Pane root, orzel7 spaceCraft, List<srutLaserowy> missiles)
    {

        //Usuwanie poprzednich elementów graficznych
        root.getChildren().clear();

        /*//Rysowanie statku kosmicznego z otoczką
        List<MyPoint> hull = Graham.findHull(spaceCraft.shape);
        MyPolygon spacecraftPolygon = new MyPolygon(hull);
        Polygon polygon = spacecraftPolygon.toPolygon();
        polygon.setFill(Color.LIGHTGRAY);
        polygon.setStroke(Color.BLACK);
        root.getChildren().add(polygon);*/

        //Rysowanie statku
        for (MyPoint point : spaceCraft.shape)
        {
            Circle pointCircle = new Circle(point.x, point.y, 3);
            pointCircle.setFill(Color.RED);
            root.getChildren().add(pointCircle);
        }

        //Rysowanie śrutów laserowych
        for (srutLaserowy missile : missiles)
        {
            Circle circle = new Circle(missile.position.x, missile.position.y, 3);
            circle.setFill(Color.RED);
            root.getChildren().add(circle);
        }
    }
    private void simulateAndVisualizeBattle(Pane root, orzel7 spaceCraft, List<srutLaserowy> missiles)
    {
        final long[] lastUpdateTime = {0};

        AnimationTimer timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                if (isPaused)
                {
                    lastUpdateTime[0] = now;
                    return;
                }

                if (lastUpdateTime[0] == 0)
                {
                    lastUpdateTime[0] = now;
                }

                double deltaTime = (now - lastUpdateTime[0]) / 1_000_000_000.0;
                lastUpdateTime[0] = now;


                updatePositions(spaceCraft, missiles, deltaTime, 0.5);
                updateVisuals(root, spaceCraft, missiles);
                checkCollisions(spaceCraft, missiles, root);
            }
        };
        timer.start();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
