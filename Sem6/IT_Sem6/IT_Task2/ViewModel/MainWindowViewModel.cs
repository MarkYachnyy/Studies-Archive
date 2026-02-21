using System.Collections.ObjectModel;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using IT_Task2.Classes;

namespace IT_Task2.ViewModel;

public partial class MainWindowViewModel : ObservableObject
{
    [ObservableProperty] private float _lineX1;
    [ObservableProperty] private float _lineY1;
    [ObservableProperty] private float _lineX2;
    [ObservableProperty] private float _lineY2;

    [ObservableProperty] private float _pointX;
    [ObservableProperty] private float _pointY;

    [ObservableProperty] private float _ellipseX;
    [ObservableProperty] private float _ellipseY;
    [ObservableProperty] private float _ellipseRX;
    [ObservableProperty] private float _ellipseRY;

    [ObservableProperty] private float _rectangleX;
    [ObservableProperty] private float _rectangleY;
    [ObservableProperty] private float _rectangleW;
    [ObservableProperty] private float _rectangleH;

    [ObservableProperty] ObservableCollection<GeometricFigure> _figures = new(){new MyEllipse(0,0, 1, 2), new MyRectangle(10, 10, 20, 40)};


    [RelayCommand]
    public void AddEllipse()
    {
        if (EllipseRX <= 0 || EllipseRY <= 0) return;
        Figures.Add(new MyEllipse(EllipseX, EllipseY, EllipseRX, EllipseRY));
    }

    [RelayCommand]
    public void AddLine()
    {
        Figures.Add(new MyLine(LineX1, LineY1, LineX2, LineY2));
    }

    [RelayCommand]
    public void AddPoint()
    {
        Figures.Add(new MyPoint(PointX, PointY));
    }

    [RelayCommand]
    public void AddRectangle()
    {
        if (RectangleH <= 0 || RectangleW <= 0) return;
        Figures.Add(new MyRectangle(RectangleX, RectangleY, RectangleW, RectangleH));
    }

    [RelayCommand]
    public void RemoveFigure(GeometricFigure figure)
    {
        Figures.Remove(figure);
    }
}