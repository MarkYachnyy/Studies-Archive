import math


class Parser:
    """A recursive parser of mathematical expressions that
     supports variable and subscripted variables"""

    # Defines the binary operators that can be used in the expressions
    operators = [['+', lambda x, y: x + y],
                 ['-', lambda x, y: x - y],
                 ['*', lambda x, y: x * y],
                 ['/', lambda x, y: x / y],
                 ['^', lambda x, y: x ** y]]
    # Defines the functions that can be used in the expressions
    functions = {'sin': math.sin,
                 'cos': math.cos,
                 'tan': math.tan,
                 'asin': math.asin,
                 'acos': math.acos,
                 'atan': math.atan,
                 'sqrt': math.sqrt,
                 'abs': math.fabs,
                 'floor': math.floor,
                 'ln': (lambda x: math.log(x, math.e))}
    # Defines the constants that can be used in the expresions
    constants = {'pi': math.pi, 'e': math.e}

    def __init__(self, st):
        # Accepts the mathematical string in st

        st = st.strip()  # removes whitespace

        # This provides leading minuses with the implied argument to the minus operator
        if (st[0] == '-'):
            st = '0' + st

        # look for any operators that can be tackled
        found_match = self.operator_handle(st)
        if (found_match == 1): return

        # look for additional brackets e.g. ((1+2))
        if (st[0] == '(' and st[-1] == ')'):
            # If found replace this nodes content with that inside the ()
            inside_tree = self.__class__(st[1:-1])
            self.function = inside_tree.function
            self.leaves = inside_tree.leaves
            return

        # look for any functions that can be tackled
        found_match = self.function_handle(st)
        if (found_match == 1): return

        self.leaves = []  # make sure the self has "leaves" attribute

        # the expression a singular entity e.g. number or variable
        self.singles_handle(st)

    def operator_handle(self, st):
        "Searches for operators that can be handled"

        # Look for operators in standard order of operations order
        for op, func in self.operators:

            # this variable ensures we don't parse any operators inside parenthesis yet
            braket_level = 0
            # the string is revesed to make equal levels of operations be processed left to right
            for char in reversed(range(len(st))):
                # change braket levels when needed
                if (st[char] == ')'):
                    braket_level += 1
                elif (st[char] == '('):
                    braket_level -= 1

                # operator found outside any brakets
                elif (st[char] == op and braket_level == 0):
                    # save the associated function in self.function
                    self.function = func
                    self.leaves = []
                    # leaves contain further levels of recursion on the arguments
                    self.leaves.append(self.__class__(st[:char]))
                    self.leaves.append(self.__class__(st[char - len(st) + 1:]))

                    # indicate success to __init__ functions
                    return 1

    def function_handle(self, st):
        "Searches for functions that can be handled"
        # tests if the function name matches up with the string beginning
        for fu, func in self.functions.items():
            if (st[:len(fu)] == fu):
                self.function = func
                # still need functions with multiple arguments
                self.leaves = [self.__class__(st[len(fu) + 1:-1])]
                # indicate success to __init__ functions
                return 1

    def singles_handle(self, st):
        "Handles static expressions"
        # looks if the string is meant to represent a constant
        if (st.lower() in self.constants):
            self.function = self.constants[st.lower()]
            return

        try:
            # If it is a number set function equal to it
            self.function = float(st)
        except ValueError:
            # Otherwise assume its a variable
            self.function = st

    def travel(self):
        "Returns a python function that reflect the tree of self"

        # if function type is a simple number return a function that returns it
        if (type(self.function) == type(1.0) or type(self.function) == type(1)):
            def actual_function(**kargs):
                return self.function

        elif (type(self.function) == type(' ')):

            # if the function contains [ and ] assume its a subscripted variable
            if ('[' in self.function and ']' in self.function):
                def actual_function(**kargs):
                    # the variables index "picked" from the list in the user arguments
                    return kargs[self.function.split('[')[0]][int(self.function.split('[', 1)[1][:-1])]

            else:
                # other wise return a function that returns its variable "picked" from the user arguments
                def actual_function(**kargs):
                    return kargs[self.function]

        else:
            # return a function that returns the value of this nodes function using the leaves as arguments
            def actual_function(**kargs):
                return self.function(*list(map(lambda x: x.travel()(**kargs), self.leaves)))

        # return the function that we determined in the previous step
        return actual_function