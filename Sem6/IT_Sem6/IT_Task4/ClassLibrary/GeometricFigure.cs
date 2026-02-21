namespace ClassLibrary;

public abstract class GeometricFigure
{
    public float CenterX { get; set; }
    public float CenterY { get; set; }

    protected GeometricFigure()
    {
        CenterX = 0;
        CenterY = 0;
    }
    
    protected GeometricFigure(float centerX, float centerY)
    {
        CenterX = centerX;
        CenterY = centerY;
    }

    public abstract (float minX, float minY, float maxX, float maxY) BoundingRectangle { get; }
    public abstract float Area { get; }
    
    public abstract string Description { get; }
}