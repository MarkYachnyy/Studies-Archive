namespace BusFlowApp.Models;

public class Disabled : IPassenger
{
    public string Name { get; }

    public Disabled(string name)
    {
        Name = name;
    }
}