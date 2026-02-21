using System;

namespace ClassLibrary;

public class MyEllipse : GeometricFigure
{
    public float RadiusX { get; set; }
    public float RadiusY { get; set; }

    public MyEllipse() : base()
    {
        RadiusX = 0;
        RadiusY = 0;
    }

    public MyEllipse(float centerX, float centerY, float radiusX, float radiusY) : base(centerX, centerY)
    {
        RadiusX = radiusX;
        RadiusY = radiusY;
    }

    public override (float minX, float minY, float maxX, float maxY) BoundingRectangle
    {
        get => (CenterX - RadiusX, CenterY - RadiusY, CenterX + RadiusX, CenterY + RadiusY);
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