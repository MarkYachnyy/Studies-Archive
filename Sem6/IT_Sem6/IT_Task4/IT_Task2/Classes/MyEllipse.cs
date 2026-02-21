using System;

namespace IT_Task2.Classes;

public class MyEllipse : GeometricFigure
{
    public float RadiusX { get; set; }
    public float RadiusY { get; set; }

    public MyEllipse(float centerX, float centerY, float radiusX, float radiusY) : base(centerX, centerY)
    {
        RadiusX = radiusX;
        RadiusY = radiusY;
    }

    public override (float minX, float minY, float maxX, float maxY) BoundingRectangle
    {
        get => (CenterX, CenterY, RadiusX, RadiusY);
    }

    public override float Area
    {
        get => (float)(Math.PI * RadiusX * RadiusY);
    }

    public override string Description
    {
        get => "Эллипс с радиусами  " + RadiusX + " и " + RadiusY;
    }
}