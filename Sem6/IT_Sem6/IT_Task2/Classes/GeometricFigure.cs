namespace IT_Task2.Classes;

public abstract class GeometricFigure
{
    public float CenterX { get; set; }
    public float CenterY { get; set; }

    protected GeometricFigure(){}
    
    protected GeometricFigure(float centerX, float centerY)
    {
        CenterX = centerX;
        CenterY = centerY;
    }

    public abstract (float minX, float minY, float maxX, float maxY) BoundingRectangle { get; }
    public abstract float Area { get; }
    
    public abstract string Description { get; }
}