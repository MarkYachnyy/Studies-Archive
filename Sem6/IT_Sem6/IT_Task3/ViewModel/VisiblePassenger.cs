using BusFlowApp.Models;
using CommunityToolkit.Mvvm.ComponentModel;

namespace BusFlowApp.ViewModels;

public class VisiblePassenger : ObservableObject
{
    public required IPassenger Passenger { get; set; }

    public bool IsMan
    {
        get
        {
            return Passenger.GetType() == typeof(Man);
        }
    }
    
    public bool IsWoman
    {
        get
        {
            return Passenger.GetType() == typeof(Woman);
        }
    }

    
    public bool IsDisabled
    {
        get
        {
            return Passenger.GetType() == typeof(Disabled);
        }
    }

    
    private double _x;
    public double X
    {
        get => _x;
        set => SetProperty(ref _x, value);
    }

    private double _y;
    public double Y
    {
        get => _y;
        set => SetProperty(ref _y, value);
    }

}