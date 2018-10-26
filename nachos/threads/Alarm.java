package nachos.threads;

import nachos.machine.*;

import java.util.*;		//linked list 

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */

public class Alarm
{
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * Note: Nachos will not function correctly with more than one
     * alarm.
     */
	
    public Alarm()
    {
    		Machine.timer().setInterruptHandler(new Runnable()
    		{
    			public void run()
    			{ 
    				timerInterrupt();		 
    			}
	    });
	
    		wakeThreadQ = new LinkedList<WakeThread>();	
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    
    public void timerInterrupt()
    {
    		boolean intStatus = Machine.interrupt().disable();	
    		long MachineTime = Machine.timer().getTime();
    		
    		for(int j = 0; j < wakeThreadQ.size(); j++)
    		{
    			WakeThread wakingThread = wakeThreadQ.get(j);			
    			
    			if (MachineTime >= wakingThread.MachineTime)
    			{
    				wakingThread.wakeThread.ready();		
    				wakeThreadQ.remove(j--);			
    			}
    		}

    		KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least x ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * (current time) >= (WaitUntil called time)+(x)
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    
    
    public void waitUntil(long x)
    {
	    	boolean intStatus = Machine.interrupt().disable();			
	    	long MachineTime = Machine.timer().getTime() + x;
	    	
	    	WakeThread wakingThread = new WakeThread(MachineTime, KThread.currentThread());
	    	wakeThreadQ.add(wakingThread);	
	    	KThread.sleep();						
	    		
    }
    
    
    
    public class WakeThread
    {
        	WakeThread(long wakingThread, KThread wakingCurrentThread)
        	{
        		MachineTime = wakingThread;				
        		wakeThread = wakingCurrentThread;		
        	}
        		public long MachineTime;				
        		public KThread wakeThread;
    }
    
    public LinkedList<WakeThread> wakeThreadQ;	
   
}
