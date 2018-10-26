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
	
	private LinkedList<WakeThread> wakeThreadQ;	//list of the queue
	 
    public Alarm()
    {
    		Machine.timer().setInterruptHandler(new Runnable()
    		{
    			public void run()
    			{ 
    				timerInterrupt(); 		//calls to stop  
    			}
	    });
	
    		wakeThreadQ = new LinkedList<WakeThread>();		//new queue with typed LinkedList
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    
    public void timerInterrupt()
    {
    		boolean intStatus = Machine.interrupt().disable();	//makes sure the interrupt is disabled hence continuous
    		long MachineTime = Machine.timer().getTime();
    		
    		int j = 0;
    		do
    		{
    			WakeThread wakingThread = wakeThreadQ.get(j);			//gets the one thread that is current
    			
    			if (wakingThread.MachineTime <= MachineTime)		//logical check if the thread thats up for the queue and can still be ran
    			{
    				wakingThread.wakeThread.ready();		//the thread is ready to be out of the queue
    				wakeThreadQ.remove(j--);			//removes the previous thread
    			}
    			
    			j++;
    			
    		}while(j < wakeThreadQ.size());
    		
    		
    		KThread.currentThread().yield();			//pause
    		Machine.interrupt().restore(intStatus);
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
	    	boolean intStatus = Machine.interrupt().disable();			//makes sure that the current status of each thread is at a 
	    	long MachineTime = Machine.timer().getTime() + x;
	    	
	    	WakeThread wakingThread = new WakeThread(MachineTime, KThread.currentThread());	//the new thread with the new time
	    	wakeThreadQ.add(wakingThread);			//adds the thread at head to the queue
	    	KThread.sleep();						//sleep
	    	
	    	Machine.interrupt().restore(intStatus);		//restores state
    }
    
    
    
    private class WakeThread
    {
        	WakeThread(long wakingThread, KThread wakingCurrentThread)
        	{
        		MachineTime = wakingThread;				//current thread thats awake but needs to wait to be executed
        		wakeThread = wakingCurrentThread;		//the current thread that is at hand
        	}
        		public long MachineTime;				
        		public KThread wakeThread;
    }
   
}
