# todo-learn-fp
Simple Todo cli app that leverages functional programming and typelevel stack.

This is my first Scala AND Cats Effect appilication, I have also tried to introduce a _tagless final_ pattern but
ofc this is an overkill for such app like this. 

Application holds its state in a single place via `Ref`, errors are handled via `Either` + `MonadThrow`. 

## Typical program session
``` 
 Todo App (Yet another)
=========================
 press 'h' for help or 'q' to quit

>>> add buy milk
>>> add learn about monad transformers
>>> add implement next uber, but for dogs
>>> ls
+-----+-----------------------------------+-------+
| No. | Tasks                             | State |
+-----+-----------------------------------+-------+
| 1.  | buy milk                          | TODO  |
| 2.  | learn about monad transformers    | TODO  |
| 3.  | implement next uber, but for dogs | TODO  |
+-----+-----------------------------------+-------+
>>> t 1
>>> ls
+-----+-----------------------------------+-------+
| No. | Tasks                             | State |
+-----+-----------------------------------+-------+
| 1.  | buy milk                          | DONE  |
| 2.  | learn about monad transformers    | TODO  |
| 3.  | implement next uber, but for dogs | TODO  |
+-----+-----------------------------------+-------+
>>> t 3
>>> t 3
>>> ls
+-----+-----------------------------------+-----------+
| No. | Tasks                             | State     |
+-----+-----------------------------------+-----------+
| 1.  | buy milk                          | DONE      |
| 2.  | learn about monad transformers    | TODO      |
| 3.  | implement next uber, but for dogs | CANCELLED |
+-----+-----------------------------------+-----------+
>>> h

Available Commands:
rm <num> - Removes a task number <num> from the list.
t <num> - Cycle through task state [DONE, TODO, CANCELLED] for task with <num>.
ls OR list - List your tasks.
q OR quit - Exits the application.
h OR help - Display help (the same you are reading right now).

>>> q
Goodbye!
```

The application is heavily inspired by this [Todo App](https://github.com/alvinj/FunctionalToDoListWithCats), which gave me some
kind of courage and ignition to start my own (small) things in typelevel and scala stack. Thanks for that.
