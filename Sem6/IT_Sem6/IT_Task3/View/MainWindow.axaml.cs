using Avalonia.Controls;
using BusFlowApp.ViewModels;

namespace BusFlowApp.Views;

public partial class MainWindow : Window
{
    public MainWindow()
    {
        InitializeComponent();
        DataContext = new MainWindowViewModel();
    }
    
}
