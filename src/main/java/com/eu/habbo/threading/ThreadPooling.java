package com.eu.habbo.threading;

import com.eu.habbo.Emulator;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

public class ThreadPooling
{
    public final int threads;
    private final ScheduledExecutorService scheduledPool;
    private volatile boolean canAdd;

    public ThreadPooling(Integer threads)
    {
        this.threads = threads;
        this.scheduledPool = Executors.newScheduledThreadPool(this.threads, new DefaultThreadFactory("ArcturusThreadFactory"));
        this.canAdd = true;
        Emulator.getLogging().logStart("Thread Pool -> Loaded!");
    }

    public ScheduledFuture run(Runnable run)
    {
        try
        {
            if (this.canAdd)
            {
                return this.run(run, 0);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }

    public ScheduledFuture run(Runnable run, long delay)
    {
        try
        {
            if (this.canAdd)
            {
                return this.scheduledPool.schedule(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            run.run();
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine(e);
                        }
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }

    public void shutDown()
    {
        this.canAdd = false;

        this.scheduledPool.shutdown();
        try
        {
            this.scheduledPool.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        while(!this.scheduledPool.isTerminated()) {
        }
    }

    public ScheduledExecutorService getService()
    {
        return this.scheduledPool;
    }
}
