package nachos.threads;

import nachos.machine.*;

import java.util.Queue;

import java.util.LinkedList;

import java.util.Iterator;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
    	Machine.timer().setInterruptHandler(new Runnable() {
    		public void run() {
    			timerInterrupt();
    		}
	    });
    	wakeThreadQ = new LinkedList<wakeThread>();
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	boolean intStatus = Machine.interrupt().disable();
    	
    	Iterator<wakeThread> it = wakeThreadQ.iterator();
    	
    	while (it.hasNext()) {
    		wakeThread temp = it.next();
    		if(temp.machineTime <= Machine.timer().getTime()) {
    			it.remove();
    			temp.wakingThread.ready();
    		}
    		
    	}
    	
    	Machine.interrupt().restore(intStatus);
    	
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
    	
    	if(x == 0) {
    		return;
    	}
    	
    	boolean intStatus = Machine.interrupt().disable();		
    	
    	wakeThread wakingThread = new wakeThread(KThread.currentThread(), Machine.timer().getTime() + x);
   
    	wakeThreadQ.add(wakingThread);	
    	KThread.sleep();				
    	
    	Machine.interrupt().restore(intStatus);
    		
    
    }
    
    private Queue<wakeThread> wakeThreadQ;
    
    public class wakeThread {
    	private KThread wakingThread;
    	private long machineTime;

    	
    	public wakeThread(KThread thread, long mTime){
    		this.wakingThread = thread;
    		this.machineTime = mTime;
    	}
    	
    
    	
    
    }
    
   
    
}
