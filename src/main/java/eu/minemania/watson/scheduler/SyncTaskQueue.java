package eu.minemania.watson.scheduler;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SyncTaskQueue
{
    private static final SyncTaskQueue INSTANCE = new SyncTaskQueue();
    protected ConcurrentLinkedQueue<Runnable> _taskQueue = new ConcurrentLinkedQueue<>();

    public static SyncTaskQueue getInstance()
    {
        return INSTANCE;
    }

    public void addTask(Runnable task)
    {
        _taskQueue.add(task);
    }

    public void runTasks()
    {
        for (;;)
        {
            Runnable task = _taskQueue.poll();
            if (task == null)
            {
                break;
            }
            task.run();
        }
    }	  
}