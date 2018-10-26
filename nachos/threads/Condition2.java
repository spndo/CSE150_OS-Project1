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
	waitingQueue = new LinkedList<KThread>();
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	boolean oldStatus = Machine.interrupt().disable();
	
	conditionLock.release();
	
	KThread currThread = KThread.currentThread();
	waitingQueue.addLast(currThread);
	currThread.sleep();
	    
	Machine.interrupt().restore(oldStatus);
	
	conditionLock.acquire();

    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	boolean oldStatus = Machine.interrupt().disable();
	
	if (!waitingQueue.isEmpty()) {
		KThread firstThread = waitingQueue.getFirst();
		waitingQueue.removeFirst();
		firstThread.ready();
	}
	
	Machine.interrupt().restore(oldStatus);
	
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	boolean oldStatus = Machine.interrupt().disable();
	
	while (!waitingQueue.isEmpty()) {
		KThread firstThread = waitingQueue.getFirst();
		waitingQueue.removeFirst();
		firstThread.ready();
	}
	
	Machine.interrupt().restore(oldStatus);
	
    }

    private Lock conditionLock;
    private LinkedList<KThread> waitingQueue;
}
