using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using Avalonia.Markup.Xaml.MarkupExtensions;
using IT_Task1.Classes;

namespace IT_Task1.ViewModel;

public class MainWindowViewModel: INotifyPropertyChanged
{
    public event PropertyChangedEventHandler? PropertyChanged;
    
    private MyQueue<int> _myQueue = new();
    private string _someString;

    protected virtual void OnPropertyChanged([CallerMemberName] string? propertyName = null)
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
    }

    public void ButtonInqueueOnClick()
    {
        if (int.TryParse(TextBoxNewElement, out int res))
        {
            _myQueue.Inqueue(res); 
            TextBlockOutputError = "";
            TextBoxNewElement = "";
            OnPropertyChanged("TextBlockQueueSize");
            OnPropertyChanged("TextBlockCurrentElement");
            OnPropertyChanged("TextBlockOutputError");
            OnPropertyChanged("TextBoxNewElement");
        }
    }

    public void ButtonDequeueOnClick()
    {
        if (_myQueue.IsEmpty)
        {
            TextBlockOutputError = "Очередь пустая";
        }
        else
        {
            TextBlockOutputError = "";
            _myQueue.Dequeue();
            OnPropertyChanged("TextBlockQueueSize");
            OnPropertyChanged("TextBlockCurrentElement");
        }
        OnPropertyChanged("TextBlockOutputError");
    }

    public string TextBoxNewElement
    {
        get => _someString;
        set
        {
            _someString = value;
            OnPropertyChanged("TextBlockInputError");
        }
    }
    
    public string TextBlockCurrentElement
    {
        get => "Текущий элемент: " + (_myQueue.IsEmpty ? "-" : _myQueue.Current);
    }
    
    public string TextBlockQueueSize
    {
        get => "Размер очереди: " + _myQueue.Size;
    }

    public string TextBlockInputError
    {
        get
        {
            if (int.TryParse(TextBoxNewElement, out int result))
            {
                return "";
            }
            return "Введите целое число";
        }
    }

    public string TextBlockOutputError
    {
        get;
        set;
    }


}