namespace IT_Task2.Classes;

public class MyRectangle : GeometricFigure
{
    public float Width { get; set; }
    public float Height { get; set; }

    public MyRectangle(float centerX, float centerY, float width, float height) : base(centerX, centerY)
    {
        Width = width;
        Height = height;
    }

    public override (float minX, float minY, float maxX, float maxY) BoundingRectangle
    {
        get
        {
            float minX = CenterX - Width / 2;
            float minY = CenterY - Height / 2;
            float maxX = CenterX + Width / 2;
            float maxY = CenterY + Height / 2;
            return (minX, minY, maxX, maxY);
        }
    }

    public override float Area
    {
        get => Width * Height;
    }

    public override string Description { get => $"Прямоугольник шириной {Width} и высотой {Height}";}
}