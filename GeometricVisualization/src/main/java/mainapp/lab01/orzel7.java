package mainapp.lab01;
import java.util.List;

public class orzel7
{
    MyPoint position;
    MyPoint velocity;
    List<MyPoint> shape;

    public orzel7(MyPoint position, MyPoint velocity, List<MyPoint> shape)
    {
        this.position = position;
        this.velocity = velocity;
        this.shape = shape;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Statek 'Orzeł 7':\n");
        sb.append("Pozycja: ").append(position).append("\n");
        sb.append("Prędkość: ").append(velocity).append("\n");
        sb.append("Kształt: ");

        for (MyPoint point : shape)
        {
            sb.append(point).append(", ");
        }

        if (shape.size() > 0)
        {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
}