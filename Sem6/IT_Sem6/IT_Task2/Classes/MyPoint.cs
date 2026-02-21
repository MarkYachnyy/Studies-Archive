namespace IT_Task2.Classes;

public class MyPoint : GeometricFigure
{
    public MyPoint(float x, float y) : base(x, y) { }

    public override (float minX, float minY, float maxX, float maxY) BoundingRectangle
    {
        get => (CenterX, CenterY, CenterX, CenterY);
    }
    
    
    public override float Area
    {
        get => 0;
    }

    public override string Description { get => "Точка"; }
}