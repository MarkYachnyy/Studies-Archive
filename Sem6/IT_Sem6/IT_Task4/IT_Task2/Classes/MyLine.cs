using System;

namespace IT_Task2.Classes;

public class MyLine : GeometricFigure
{
    public float StartX { get; set; }
    public float StartY { get; set; }

    public float EndX
    {
        get => StartX + 2 * (CenterX - StartX);
    }

    public float EndY
    {
        get => StartY + 2 * (CenterY - StartY);
    }

    public MyLine(float startX, float startY, float endX, float endY) : base((startX + endX) / 2, (startY + endY) / 2)
    {
        StartX = startX;
        StartY = startY;
    }

    public override (float minX, float minY, float maxX, float maxY) BoundingRectangle
    {
        get
        {
            float minX = Math.Min(StartX, EndX);
            float minY = Math.Min(StartY, EndY);
            float maxX = Math.Max(StartX, EndX);
            float maxY = Math.Max(StartY, EndY);
            return (minX, minY, maxX, maxY);
        }
    }

    public override float Area
    {
        get => 0; // Площадь линии равна 0
    }

    public override string Description
    {
        get => $"Линия, идущая из ({StartX}; {StartY}) в ({EndX}; {EndY})";
    }
}