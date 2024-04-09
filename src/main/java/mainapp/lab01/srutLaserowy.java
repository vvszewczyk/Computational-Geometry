package mainapp.lab01;

public class srutLaserowy
{
    double detectionTime;
    MyPoint position;
    MyPoint velocity;

    public srutLaserowy(double detectionTime, MyPoint position, MyPoint velocity)
    {
        this.detectionTime = detectionTime;
        this.position = position;
        this.velocity = velocity;
    }
}
