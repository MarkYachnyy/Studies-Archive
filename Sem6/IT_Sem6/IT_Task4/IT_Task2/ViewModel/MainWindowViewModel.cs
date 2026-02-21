using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Reflection;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using IT_Task2.Classes;

namespace IT_Task2.ViewModel;

public partial class MainWindowViewModel : ObservableObject
{
    //От 2 задания

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

    [ObservableProperty]
    ObservableCollection<GeometricFigure> _figures = new()
        { new MyEllipse(0, 0, 1, 2), new MyRectangle(10, 10, 20, 40) };

    //Всё, что связано с 4 заданием

    [ObservableProperty] private string? _dllPath;
    [ObservableProperty] private ObservableCollection<string> _methodInformations = new();
    [ObservableProperty] private string? _selectedMethod;
    [ObservableProperty] private string? _actionResult;

    [ObservableProperty] private ObservableCollection<string> _figureClassNames = new();
    [ObservableProperty] private string? _selectedFigureClass;

    private Dictionary<string, MethodInfo> _methodDescriptionsDictionary = new();
    private Dictionary<string, Type> _figureTypesDictionary = new();
    
    private object? _selectedFigureInstance;
    private Type? _figureType;
    
    [ObservableProperty] private string? _methodExecutionResult;
    [ObservableProperty] private string? _paramValue;


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
    

    [RelayCommand]
    private void LoadDll()
    {
        if (string.IsNullOrWhiteSpace(DllPath) || !File.Exists(DllPath))
        {
            ActionResult = "Неверный путь к dll";
            return;
        }

        try
        {
            Assembly asm = Assembly.LoadFrom(DllPath);

            _figureType = asm.GetType("ClassLibrary.GeometricFigure");
            _figureTypesDictionary.Clear();
            FigureClassNames.Clear();
            

            if (_figureType != null)
            {
                var figureTypes = asm.GetTypes()
                    .Where(t => t.IsSubclassOf(_figureType) && !t.IsAbstract)
                    .ToList();

                foreach (var type in figureTypes)
                {
                    _figureTypesDictionary[type.Name] = type;
                    FigureClassNames.Add(type.Name);
                }

                ActionResult = $"Успешно загружено {FigureClassNames.Count} классов";
            }
            else
            {
                ActionResult = "В dll не найдено класса геометрической фигуры!";
            }

            _methodDescriptionsDictionary.Clear();
            MethodInformations.Clear();
        }
        catch (Exception ex)
        {
            ActionResult = $"Ошибка загрузки dll: {ex.Message}";
        }
    }

    [RelayCommand]
    private void CreateSelectedFigure()
    {
        if (string.IsNullOrWhiteSpace(SelectedFigureClass) || _figureType == null) return;

        if (_figureTypesDictionary.TryGetValue(SelectedFigureClass, out var type))
        {
            try
            {
                _selectedFigureInstance = Activator.CreateInstance(type);
                ActionResult = $"Успешно создан {SelectedFigureClass}";

                _methodDescriptionsDictionary.Clear();
                MethodInformations.Clear();

                MethodInfo[] loadedMethods = _figureTypesDictionary[SelectedFigureClass].GetMethods();
                string[] objMethodNames = ["Equals", "ToString", "GetHashCode", "GetType"];
                
                foreach (var methodInfo in loadedMethods)
                {
                    if(objMethodNames.Contains(methodInfo.Name)) continue;
                    var methodInfoStr = "";
                    methodInfoStr += methodInfo.Name + "(";
                    methodInfoStr += string.Join(", ", methodInfo.GetParameters().Select(p => "Float " + p.Name));
                    methodInfoStr += ")";
                    MethodInformations.Add(methodInfoStr);
                    _methodDescriptionsDictionary.Add(methodInfoStr, methodInfo);
                }
            }
            catch (Exception ex)
            {
                ActionResult = $"Create error: {ex.Message}";
            }
        }
    }

    [RelayCommand]
    private void ExecuteSelectedAction()
    {
        if (string.IsNullOrWhiteSpace(SelectedMethod)) return;
        
        if (_methodDescriptionsDictionary.TryGetValue(SelectedMethod, out var methodInfo))
        {
            try
            {
                object[] params_to_use = [];
                if (ParamValue != null && ParamValue.Trim().Length != 0)
                {
                    try
                    {
                        float p = float.Parse(ParamValue.Trim());
                        params_to_use = [p];
                    }
                    catch (Exception e)
                    {
                        ActionResult = "Неверный формат параметра";
                        throw;
                    }
                }
                object? res = methodInfo.Invoke(_selectedFigureInstance, params_to_use);
                MethodExecutionResult = res?.ToString();
                ActionResult = $"Метод {methodInfo.Name} успешно выполнен";
            }
            catch (Exception ex)
            {
                ActionResult = $"Ошибка исполнения: {ex.Message}";
            }
        }
    }
}