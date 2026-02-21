namespace BusFlowApp.Models;

public class Woman : IPassenger
{
    public string Name { get; private set; }

    public Woman(string name)
    {
        Name = name;
    }
}
