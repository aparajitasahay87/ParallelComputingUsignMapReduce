# ParallelComputingUsignMapReduce
Implemented inverted index program using Map reduce 
Inversed Indexing using Map Reduce


Aparajita Sahay
Performance Improvement : 
With one node : 11148 ms i.e . 11 seconds.
With four node : 10058 ms i.e . 10 seconds
Inverted indexing program Documentation:-
In mapper class, using getInputsplit() method I get input split for this map and using reporter object we can retrieve filename associated with the given map object. Each mapper will get one line of document. In my implementation, after tokenizing each word of a given line, I compare the word with the keywords. If there is a match then count variable is incremented and at the end of while loop I concatenate filename and count to a string variable and finally set it to the text type variable word. Then each map will have a keyword, filename count associated with it.
Each map task has a circular memory buffer associated that it writes the output to. The buffer has a default size but can be changed to using io.sort.mb property. When the content of the buffer reached a certain threshold size , a new spill file is created, so after the map task has written its last output record, there could be several spill files. Before the task is finished, the spill file are merged into single partitioned and sorted output file. 
Now the reduce task needs the map output for its particular partition from various map tasks. So reduce tasks starts copying their output as each map task completes. After the reduces tasks completes copying , reduce task move into sort phase , which merge the map output and maintain their sort order. So at this stage, each reducer object gets one keyword and document id associated with it.
Then at reduce class, we make sure that output of map class and input of reduce class is same. Once we get keyword and filename_count, we iterate through each value for a keyword, split it’s content by using split() function. I used hash map for storing filename as key and count as value. Used hash map over list so that data retrieval time becomes less time consuming i.e. O(1) time, which can help in performance improvement. After splitting the content of reducer input values  to filename and count , I compared it with file name exist in hash map, if it exist then add the count with hash map’s values current count and update the hash map or add a new key value pair. After all the values at reducer stored in iterator are iterated , retrieve keyvalue pair from hash map and concatenate it again. Used string builder for string concatenation of all filename_counts. Since it is much faster than and consumes less memory than string concat() method. Then finally added keyword, doclisttext to output collect of reducer.

Programmability: 
If I will implement Inversed indexing Using MPI a brief overview is mentioned below:- 
 In MPI, master node will assign each processes (master and slave) there block of data(using send and receive method of mpi). Each process will look for a keyword in their assigned section of data. If data exists each process will store it in an array or list or any data structure After each process performs their map equivalent task of finding the keyword, each process have to send(using send receive function) their data to master node. Master node will gather all the data from processes sorts and combine the data as per the keyword. At this state master node will make sure that each process will get one keyword and all the document_id associate with it. After doing the task master node will again assign each process some task (each process will get one keyword and all the document_id). Again all the processes will do their computation and once there task is done, they will send the result back to master node which will collect/store all the data and print it out as the final output. To maintain synchronization within the process and to make sure all the data have responded back to the master we have to use some wait commands (ex MPI_Barrier) .And once each process returned the data then proceed forward for next state.
Advantage of using MPI: 
•	In my implementation of map reduce each document’s each line goes to different mapper .But by using MPI, we can have more control over data and can assign more jobs to each process. Once each process (in MPI) has more data to compute the result then can be sent to master node. We can reduce network latency and hence increase performance by having more control over data. On the other hand , in Map/Reduce we don’t have control over data. How Hadoop is goingto allocate work to each process , so it can be less performant in few scenarios than MPI. Map reduce is good if we need more robust and fault tolerant result. 
•	Using MPI, it is easy to backtrack the result. We can track from which process are we getting this particular result. But in Hadoop it is difficult to track down the source of particular result. So, if we want to analyze the source of the result it is difficult in map reduce but in mpi it can be done.
Disadvantage of using MPI:
•	In MPI, programmer have to make sure about the synchronization within the process and also have to think about job scheduling. But in Map reduce programmer need not have to worry about it. So Map reduce is good from programbility point of view than MPI. 
•	More robust/fault tolerant: If one process fails to perform the task then the job is not suspended. Hadoop assigns tasks to another processes, and the job gets completed. However, in MPI if one process is down whole job is suspended. So, MAP/reduce is more reliable and fault tolerant than MPI. Hadoop ability to handle task failure. Log files are created when failure occur such as task failure. MPI not as easy to debug as map reduce. Maximum number of attempt to runs a task can be controlled by mapred.map.max.attempts. 
•	Shuffle and sort : Mapreduce reduce  makes sure that input to every reducer is sorted by key. We have to do implement this logic in MPI.
Usability Map Reduce:
Pros:
•	It is easy to code and debug.
•	More fault tolerant.
•	Good for load balancing.
Cons:
•	Not a lot of control over data. Source of the data cannot be back tracked which can be a limitation in few applications.
•	Since Hadoop divide and assigns jobs to each task scheduler, it often ignores data locality.

Possible applications: Application where there not a lot of inter process communication works well for Map reduce.
Problems which can be easily broken into parts and each part are independent of each other , map reduce can be a good solution . On the other hand problems where there is a data exchange at intermediate state , for example heat 2d . It can be better in MPI.
So, when data size is too large (in terabytes) , which exceeds the memory size of a machine then we can use Map reduce.  For example word count , inversed indexing
But if have application where there is many intermediate data exchange within a set of data the MPI can be used. For example heat2d.


   










