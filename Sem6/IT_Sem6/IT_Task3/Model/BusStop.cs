using System;

namespace BusFlowApp.Models;

using System.Collections.Generic;

public class BusStop
{
    public List<IPassenger> WaitingPassengers { get; }

    public BusStop()
    {
        WaitingPassengers = new List<IPassenger>();
    }

    public void AddPassenger(IPassenger passenger)
    {
        WaitingPassengers.Add(passenger);
    }

    public List<IPassenger> GetBoardingPassengers(int passengersToBoard)
    {
        var toBoard = WaitingPassengers.GetRange(0, passengersToBoard);
        WaitingPassengers.RemoveRange(0, passengersToBoard);
        return toBoard;
    }
}
