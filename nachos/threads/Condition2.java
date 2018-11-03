package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
	waitingQueue = new LinkedList<KThread>(); // declare the linked list to store all sleeping thread
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	// disable interrupts so the following codes can be operated atomically
	// save the previous status, because we need it when we enable interrups again
	boolean preStatus = Machine.interrupt().disable();
	
	conditionLock.release(); // release the lock
	
	// gain the current thread, then put it into waiting queue and make it to sleep
	KThread currThread = KThread.currentThread();
	waitingQueue.addLast(currThread);
	currThread.sleep();
	    
	Machine.interrupt().restore(preStatus); // enable interrups, restore back to the previous status
	
	conditionLock.acquire(); // get the lock back since the thread is wake up

    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	// disable interrupts so the following codes will run atomically
	boolean preStatus = Machine.interrupt().disable();
	
	// check the waiting queue has at lease one thread
	if (!waitingQueue.isEmpty()) {
		// take out the first thread in the waiting queue and make it ready to run
		KThread firstThread = waitingQueue.getFirst();
		waitingQueue.removeFirst();
		firstThread.ready();
	}
	
	Machine.interrupt().restore(preStatus); // enable interrups, restore back to the previous status
	
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	// disable interrupts so the following codes will run atomically
	boolean preStatus = Machine.interrupt().disable();
	
	// check the waiting queue has at lease one thread
	// waking up all thread in the waiting queue
	while (!waitingQueue.isEmpty()) {
		// take out the first thread in the waiting queue and make it ready to run
		KThread firstThread = waitingQueue.getFirst();
		waitingQueue.removeFirst();
		firstThread.ready();
	}
	
	Machine.interrupt().restore(preStatus); // enable interrups, restore back to the previous status
	
    }

    private Lock conditionLock;
    private LinkedList<KThread> waitingQueue; // create a linked list to store all sleeping threads
}
