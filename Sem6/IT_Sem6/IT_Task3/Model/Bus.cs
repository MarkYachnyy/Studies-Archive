namespace BusFlowApp.Models;
    
using System;

public class Bus
{
    public int Capacity { get; }
    public int PassengerCount { get; set; }

    public event Action ArrivedAtStop = delegate { };
    public event Action<string> PassengerBoarded = delegate { };
    public event Action BusOvercrowded = delegate { };

    public Bus(int capacity)
    {
        Capacity = capacity;
    }

    public void Arrive()
    {
        ArrivedAtStop.Invoke();
    }

    public void InvokeOvercrowded()
    {
        BusOvercrowded.Invoke();
    }

    public void BoardPassenger(IPassenger passenger)
    {
        PassengerCount++;
        PassengerBoarded.Invoke(passenger.Name);
        if (PassengerCount >= Capacity)
        {
            BusOvercrowded.Invoke();
        }
    }
}
