package org.example.rangetree1d;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;
import static org.example.rangetree1d.DataGenerator.generateDataSets;

public class RangeTreeVisualization extends Application
{
    private Pane canvas;

    @Override
    public void start(Stage primaryStage)
    {
        canvas = new Pane();
        Scene scene = new Scene(canvas, 1900, 1000);

        //int[] data = {-4, 0, 1, 4, 12, 13, 39, 43, 96, 312, 400, 500};

        List<List<Integer>> generatedDataSets = generateDataSets(1);
        int[] data = generatedDataSets.get(0).stream().mapToInt(i -> i).toArray();
        RangeTree1D tree = new RangeTree1D(data);
        tree.searchRange(tree.getRoot(),0, 250);
        int initialX = (int) (canvas.getWidth() / 2);
        drawTree(tree.getRoot(), initialX + 15, 30, 0);

        primaryStage.setTitle("Range Tree Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawTree(RangeTree1D.Node node, int x, int y, int level)
    {
        if (node == null)
            return;

        final int radius = 15;
        final int width = (int) canvas.getWidth();
        final int gap = Math.max(width / (int) Math.pow(2, level + 2), 30);

        Circle circle = new Circle(x, y, radius);
        circle.setFill(node.left == null && node.right == null ? Color.LIGHTBLUE : Color.LIGHTPINK);

        String valueStr = Integer.toString(node.value);
        Text text = new Text(valueStr);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        text.setX(x - textWidth / 2);
        text.setY(y + textHeight / 4);
        canvas.getChildren().addAll(circle, text);

        //new gap between levels
        int newY = y + 50 + level * 10;

        if (node.left != null)
        {
            Line lineLeft = new Line();
            drawBranch(lineLeft, x, y, x - gap, newY, radius);
            drawTree(node.left, x - gap, newY, level + 1);
        }

        if (node.right != null)
        {
            Line lineRight = new Line();
            drawBranch(lineRight, x, y, x + gap, newY, radius);
            drawTree(node.right, x + gap, newY, level + 1);
        }
    }

    private void drawBranch(Line line, int startX, int startY, int endX, int endY, int radius)
    {
        double angle = Math.atan2(endY - startY, endX - startX);
        double x1 = startX + radius * Math.cos(angle);
        double y1 = startY + radius * Math.sin(angle);
        double x2 = endX - radius * Math.cos(angle);
        double y2 = endY - radius * Math.sin(angle);

        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);

        canvas.getChildren().add(line);
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
