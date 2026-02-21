namespace BusFlowApp.Models;

public class Man : IPassenger
{
    public string Name { get; }

    public Man(string name)
    {
        Name = name;
    }
}
