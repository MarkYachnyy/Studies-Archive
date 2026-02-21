using System.Diagnostics;

using CommunityToolkit.Mvvm.Input;

namespace BusFlowApp.ViewModels;

using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using BusFlowApp.Models;
using CommunityToolkit.Mvvm.ComponentModel;

public partial class MainWindowViewModel : ObservableObject
{
    private readonly Random _random = new();
    [ObservableProperty] private double _busX;

    public ObservableCollection<string> Log { get; set; } = new();
    public ObservableCollection<VisiblePassenger> VisualPassengers { get; set; } = new();

    private Bus _bus;
    private BusStop _busStop;

    private static readonly string[] FirstNames =
        { "Иван", "Петя", "Алексей", "Дмитрий", "Сергей", "Виктор", "Мария", "Анна", "Юля", "Оля", "Наталия" };

    private static readonly string[] ManFirstNames = { "Иван", "Петя", "Алексей", "Дмитрий", "Сергей", "Виктор" };
    private static readonly string[] WomanFirstNames = { "Мария", "Анна", "Юля", "Оля", "Наталия" };

    public MainWindowViewModel()
    {
        _bus = new Bus(capacity: 50);
        _busStop = new BusStop();

        _bus.ArrivedAtStop += () => Log.Insert(0, "Автобус прибыл на остановку");
        _bus.PassengerBoarded += name => Log.Insert(0, $"{name} сел(а) в автобус");
        _bus.BusOvercrowded += () => Log.Insert(0, "Автобус переполнен!");

        StartBusLoop();
    }

    private async void StartBusLoop()
    {
        var screenWidth = 1280;
        var stopX = 20;
        var offScreenLeft = -800;

        while (true)
        {
            _bus.PassengerCount = _random.Next(0, _bus.Capacity);
            await AnimateBus(screenWidth, stopX, TimeSpan.FromSeconds(8));
            _bus.Arrive();
            var toBoard = _busStop.GetBoardingPassengers(Math.Min(_bus.Capacity - _bus.PassengerCount, _busStop.WaitingPassengers.Count));
            var delayTime = 3000 / (toBoard.Count + 1);
            if (toBoard.Count == 0)
            {
                _bus.InvokeOvercrowded();
            }
            foreach (var p in toBoard)
            {
                await Task.Delay(delayTime);
                _bus.BoardPassenger(p);
                var visual = VisualPassengers.FirstOrDefault(v => v.Passenger.Name == p.Name);
                if (visual != null)
                    VisualPassengers.Remove(visual);
            }

            Log.Insert(0, "Автобус уехал с остановки");
            await AnimateBus(stopX, offScreenLeft, TimeSpan.FromSeconds(6));
            await Task.Delay(1000);
        }
    }

    private async Task AnimateBus(double from, double to, TimeSpan duration)
    {
        var sw = Stopwatch.StartNew();
        while (sw.Elapsed < duration)
        {
            var progress = sw.Elapsed.TotalMilliseconds / duration.TotalMilliseconds;
            progress = Math.Sin(progress * Math.PI - Math.PI / 2) / 2 + 0.5;
            BusX = from + (to - from) * progress;
            await Task.Delay(16);
        }

        BusX = to;
    }

    private void AddPassenger(IPassenger passenger)
    {
        _busStop.AddPassenger(passenger);

        var (x, y) = GenerateRandomPassengerPosition();
        var visual = new VisiblePassenger
        {
            Passenger = passenger,
            X = x,
            Y = y,
        };

        VisualPassengers.Add(visual);
        OnPropertyChanged(nameof(VisualPassengers));
    }

    private (double x, double y) GenerateRandomPassengerPosition()
    {
        double y = _random.Next(115, 125);
        double x = _random.Next(100, 240);

        return (x, y);
    }

    private string GenerateRandomName()
    {
        var random = new Random();
        return FirstNames[random.Next(FirstNames.Length)];
    }

    private string GenerateManRandomName()
    {
        var random = new Random();
        return ManFirstNames[random.Next(ManFirstNames.Length)];
    }

    private string GenerateWomanRandomName()
    {
        var random = new Random();
        return WomanFirstNames[random.Next(WomanFirstNames.Length)];
    }

    [RelayCommand]
    public void AddMan()
    {
        var name = GenerateManRandomName();
        var man = new Man(name);
        AddPassenger(man);
        Log.Insert(0, $"Мужчина {name} пришёл на остановку");
    }

    [RelayCommand]
    public void AddWoman()
    {
        var name = GenerateWomanRandomName();
        var woman = new Woman(name);
        AddPassenger(woman);
        Log.Insert(0, $"Женщина {name} пришла на остановку");
    }

    [RelayCommand]
    public void AddDisabled()
    {
        var name = GenerateRandomName();
        var disabled = new Disabled(name);
        AddPassenger(disabled);
        Log.Insert(0, $"Инвалид {name} прибыл на остановку");
    }
}