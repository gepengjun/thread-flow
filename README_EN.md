# ThreadFlow

#### introduce
Thread arrange tool. Can execute any string parallel combination of threads;  
Each unit can have its own parameters and return values. It supports asynchronous callback of single unit results.   
It supports the timeout control of single unit;  
It Support the timeout call of the overall task and the result call after the completion of the overall task;  
It supports the dynamic decision to skip the execution of units that have not been executed and do not need to be executed any more, and it can also interrupt the thread that is executing but does not need to continue;   
Each unit can be used in different task choreography;  
Supports dynamic modification of post units;   
If you have any good suggestions, please leave a message;  
If you like it or if it's helpful, make a star :) 

#### Software architecture
Understanding this project can be seen from the test cases in test.  
Basic functions:  
 **1. Serial thread call** :    
&nbsp;&nbsp;1.1. order of execution&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  A -> B -> C    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;the cost of the execution&nbsp;&nbsp;&nbsp;1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1
&nbsp;&nbsp;&nbsp;&nbsp;unit:second  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exceptionally&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  N&nbsp;&nbsp;&nbsp;&nbsp;N&nbsp;&nbsp;&nbsp;&nbsp;N    
&nbsp;&nbsp;&nbsp;Expect: the unit C executes after the unit B executes after the unit A   
     
&nbsp;&nbsp;1.2.  order of execution&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  A -> B -> C    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;the cost of the execution
&nbsp;&nbsp;1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1
&nbsp;&nbsp;&nbsp;&nbsp;unit:second  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exceptionally&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  N&nbsp;&nbsp;&nbsp;&nbsp;Y&nbsp;&nbsp;&nbsp;&nbsp;N  
&nbsp;&nbsp;&nbsp;Expect: the unit B executes exceptionally after the unit A executes successfully, the unit C direct to be exceptional to void execution  
 
**2. Simple parallel call:**   
&nbsp;&nbsp;2.1 all are unnecessary for next  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  unit A,B,C are all unnecessary for unit D, then the unit D executes after the first executes successfully in A,B,C   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A --unnecessary successful 1 second-->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B --unnecessary successful 2 second-->    D --successful 0.5 second--   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C --unnecessary successful 2 second-->  
&nbsp;&nbsp;&nbsp;&nbsp;Expect:  the unit D executes after the unit A executes successfully, the set the execute state of B,C to be exceptional,after about 1.5 seconds we can get the result of the unit D.   
&nbsp;&nbsp;2.2  some are necessary and some are unnecessary for next  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;unit A is necessary for unit D, and the unit B,C are unnecessary for unit D. then the unit A firstly execute successfully, and the unit D does not to execute at this time until after the first executes successfully in B,C    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A --necessary &nbsp;&nbsp;successful 1 second-->    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B --unnecessary successful 2 second-->  D --successful 0.5 second--  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C --unnecessary successful 2 second-->      
&nbsp;&nbsp;&nbsp;&nbsp;Expect:  the unit D executes after the first executes successfully in B,C. we can get the result in 2.5 seconds.   
&nbsp;&nbsp;2.3 all are necessary for next     
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;unit A,B,C are all necessary for unit D, then the unit D executes after the last executes successfully in A,B,C  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A -- necessary &nbsp;&nbsp;successful 1 second-->   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B -- necessary &nbsp;&nbsp;successful 2 second-->  D --successful 0.5 second--  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C -- necessary &nbsp;&nbsp;successful 3 second--> 
&nbsp;&nbsp;&nbsp;&nbsp;Expect:  D   executes after the unit C executes successfully 

  2.4 all are necessary for next but one of the previous executes exceptionally  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  the unit A is necessary for the unit D, executes successfully in 1 second;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  the unit B is necessary for the unit D, executes exceptionally in 1 second;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  the unit C is necessary for the unit D, executes successfully in 2 second;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A -- necessary &nbsp;&nbsp;successful 1 second-->   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B -- necessary &nbsp;&nbsp;exceptional 1 second-->  D --successful 0.5 second--  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C -- necessary &nbsp;&nbsp;successful 2 second-->     
&nbsp;&nbsp;&nbsp;&nbsp;Expect:  the unit D direct set the self's execute state to be exceptional to void execution and set the unit C's execute state to be exceptional after the unit B executes exceptionally 

 **3. Serial parallel combination**   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A --1 second-->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C -- unnecessary 1 second-->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B --1 second-->    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
F---->     
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;D --10 second-->E -- unnecessary 1 second-->  
&nbsp;&nbsp;&nbsp;&nbsp;the unit A,B,D start at the same time.   
&nbsp;&nbsp;&nbsp;&nbsp;at last the unit F executes after the unit C executes successfully  
&nbsp;&nbsp;&nbsp;&nbsp;and set the unit E's sexecute state to be exceptional to void execution  
&nbsp;&nbsp;&nbsp;&nbsp;and set the unit D's execute state to be exceptional   
&nbsp;&nbsp;&nbsp;&nbsp;and interrupts the the unit D's thread  if the unit D's enableInterrupted is true.  
 **4. WorkHandler can be expanded**  
        example:  
&nbsp;&nbsp;&nbsp;&nbsp;1. let the current work executes after the previous executes exceptionally  
&nbsp;&nbsp;&nbsp;&nbsp;2. we can add next work depends on the execute state of the current work

