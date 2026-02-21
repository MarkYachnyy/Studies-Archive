using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IT_Task1.Classes;

class EmptyQueueException: Exception
{
    public EmptyQueueException(string message) : base(message)
    {

    }

    public EmptyQueueException() : base()
    {

    }
}
