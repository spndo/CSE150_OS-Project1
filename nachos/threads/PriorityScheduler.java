package nachos.threads;

import nachos.machine.*;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A scheduler that chooses threads based on their priorities.
 *
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 *
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 *
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
    /**
     * Allocate a new priority scheduler.
     */
    public PriorityScheduler() {
    }
    //public LinkedList<KThread> waitQueue = new LinkedList<KThread>();
    // ThreadState lockHolder = null;
//     public static final int invalidPriority = -1;
// 	public int effectivePriority = -1;
	
    /**
     * Allocate a new priority thread queue.
     *
     * @param	transferPriority	<tt>true</tt> if this queue should
     *					transfer priority from waiting threads
     *					to the owning thread.
     * @return	a new priority thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
	return new PriorityQueue(transferPriority);
    }

    public int getPriority(KThread thread) {
	Lib.assertTrue(Machine.interrupt().disabled());
		       
	return getThreadState(thread).getPriority();
    }

    public int getEffectivePriority(KThread thread) {
	Lib.assertTrue(Machine.interrupt().disabled());
		       
	return getThreadState(thread).getEffectivePriority();
    }

    public void setPriority(KThread thread, int priority) {
	Lib.assertTrue(Machine.interrupt().disabled());
		       
	Lib.assertTrue(priority >= priorityMinimum &&
		   priority <= priorityMaximum);
	
	getThreadState(thread).setPriority(priority);
    }

    public boolean increasePriority() {
	boolean intStatus = Machine.interrupt().disable();
		       
	KThread thread = KThread.currentThread();

	int priority = getPriority(thread);
	if (priority == priorityMaximum)
	    return false;

	setPriority(thread, priority+1);

	Machine.interrupt().restore(intStatus);
	return true;
    }

    public boolean decreasePriority() {
	boolean intStatus = Machine.interrupt().disable();
		       
	KThread thread = KThread.currentThread();

	int priority = getPriority(thread);
	if (priority == priorityMinimum)
	    return false;

	setPriority(thread, priority-1);

	Machine.interrupt().restore(intStatus);
	return true;
    }

    /**
     * The default priority for a new thread. Do not change this value.
     */
    public static final int priorityDefault = 1;
    /**
     * The minimum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMinimum = 0;
    /**
     * The maximum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMaximum = 7;    

    /**
     * Return the scheduling state of the specified thread.
     *
     * @param	thread	the thread whose scheduling state to return.
     * @return	the scheduling state of the specified thread.
     */
    protected ThreadState getThreadState(KThread thread) {
	if (thread.schedulingState == null)
	    thread.schedulingState = new ThreadState(thread);

	return (ThreadState) thread.schedulingState;
    }

    /**
     * A <tt>ThreadQueue</tt> that sorts threads by priority.
     */
    protected class PriorityQueue extends ThreadQueue {
    
    public LinkedList<KThread> waitQueue = new LinkedList<KThread>();
    ThreadState lockHolder = null;
    
	PriorityQueue(boolean transferPriority) {
	    this.transferPriority = transferPriority;
	}

	public void waitForAccess(KThread thread) {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    getThreadState(thread).waitForAccess(this);
	}

	public void acquire(KThread thread) {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    getThreadState(thread).acquire(this);
	}

	public KThread nextThread() {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    // implement me
	    //if there is a header in the holder
	    if (lockHolder != null){
	    	lockHolder.donation.remove(this);
//	    	effectivePriority = invalidPriority;
//			getEffectivePriority();
	    	//update the new priority after remove the thread
	    	lockHolder.P();
	    	
	    }
	    ThreadState nextstate = pickNextThread();
	    if (nextstate != null){
	    	nextstate.acquire(this);
	    	return nextstate.thread;
	    }
	    else
	    	return null; 
	}

	/**
	 * Return the next thread that <tt>nextThread()</tt> would return,
	 * without modifying the state of this queue.
	 *
	 * @return	the next thread that <tt>nextThread()</tt> would
	 *		return.
	 */
	protected ThreadState pickNextThread() {
//		if (waitQueue.isEmpty())
//			return null;
//		
//		KThread thread = waitQueue.removeFirst();
		
		KThread thread = null ;
		
		int maxP = -100;
		//loop through whole wait queue and find the thread has max priority
		//that thread will be the next thread
		
			for (KThread t:waitQueue){
				int effectiveP = getEffectivePriority(t);
				if (t == null || effectiveP>maxP){
					thread = t;
					maxP = effectiveP;
				}
			}
			
			if (thread == null)
				return null;
			
			return  getThreadState(thread);
		
	}
	
	public void print() {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    // implement me (if you want)
	}

	/**
	 * <tt>true</tt> if this queue should transfer priority from waiting
	 * threads to the owning thread.
	 */
	public boolean transferPriority;
	
    }

    /**
     * The scheduling state of a thread. This should include the thread's
     * priority, its effective priority, any objects it owns, and the queue
     * it's waiting for, if any.
     *
     * @see	nachos.threads.KThread#schedulingState
     */
    protected class ThreadState {
	/**
	 * Allocate a new <tt>ThreadState</tt> object and associate it with the
	 * specified thread.
	 *
	 * @param	thread	the thread this state belongs to.
	 */
    	
    //public LinkedList<PriorityQueue> donation = new LinkedList<PriorityQueue>();
	public ThreadState(KThread thread) {
	    this.thread = thread;
	    
	    setPriority(priorityDefault);
	}

	/**
	 * Return the priority of the associated thread.
	 *
	 * @return	the priority of the associated thread.
	 */
	public int getPriority() {
	    return priority;
	}

	/**
	 * Return the effective priority of the associated thread.
	 *
	 * @return	the effective priority of the associated thread.
	 */
	public int getEffectivePriority() {
	    //the actual function that calcuate effective priority when 
	    return getEffectivePriority(new HashSet<ThreadState>());
	}
	
	private int getEffectivePriority(HashSet<ThreadState> ep){
		//check if the priority if is the set already,
		//since set has no duplicate numbers 
		//this helps reduce calculation
		if (ep.contains(this)){
			return priority;
		}
		//store the new priority 
		effectivePriority = priority;
		
		for (PriorityQueue q : donation){
			//if q needs to pass the priority 
			if (q.transferPriority){
				for (KThread k : q.waitQueue){
					//put into the set 
					ep.add(this);
					//get the effective p
					int p = getThreadState(k).getEffectivePriority(ep);
					//take out the thread in case of confilict
					ep.remove (this);
					if (p>effectivePriority)
						//update effectivePriority
						effectivePriority = p;
					
				}
			}
		}
		PriorityQueue q = (PriorityQueue) thread.jQueue;
		
		if (q.transferPriority){
			for (KThread k : q.waitQueue){
				//put into the set 
				ep.add(this);
				//get the effective p
				int p = getThreadState(k).getEffectivePriority(ep);
				//take out the thread in case of confilict
				ep.remove (this);
				if (p>effectivePriority)
					//update effectivePriority
					effectivePriority = p;
				
			}
		}
	
		
		return effectivePriority;
		
		
		
	}

	/**
	 * Set the priority of the associated thread to the specified value.
	 *
	 * @param	priority	the new priority.
	 */
	public void setPriority(int priority) {
	    if (this.priority == priority)
		return;
	    
	    this.priority = priority;
	    
	    // implement me
	    //set to the default and and the new one
	    P();
	    
	}

	/**
	 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
	 * the associated thread) is invoked on the specified priority queue.
	 * The associated thread is therefore waiting for access to the
	 * resource guarded by <tt>waitQueue</tt>. This method is only called
	 * if the associated thread cannot immediately obtain access.
	 *
	 * @param	waitQueue	the queue that the associated thread is
	 *				now waiting on.
	 *
	 * @see	nachos.threads.ThreadQueue#waitForAccess
	 */
	public void waitForAccess(PriorityQueue waitQueue) {
	    // implement me
		//add thread into waiting queue
		waitQueue.waitQueue.add(thread);
		//check the first element of the waitqueue
		//if waitqueue is empty, dont need to wait 
		if (waitQueue.lockHolder == null)
			return;
//		effectivePriority = invalidPriority;
//		getEffectivePriority();
		
		waitQueue.lockHolder.P();
	}

	/**
	 * Called when the associated thread has acquired access to whatever is
	 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
	 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
	 * <tt>thread</tt> is the associated thread), or as a result of
	 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
	 *
	 * @see	nachos.threads.ThreadQueue#acquire
	 * @see	nachos.threads.ThreadQueue#nextThread
	 */
	public void acquire(PriorityQueue waitQueue) {
	    // implement me
		//remove the thread that wants to access from waiting queue
		waitQueue.waitQueue.remove(thread);
		//set this thread 
		waitQueue.lockHolder = this;
		
		donation.add(waitQueue);
		
		P();
		
		
	}	
	
	public void P(){
		effectivePriority = invalidPriority;
		getEffectivePriority();
	}

	/** The thread with which this object is associated. */	   
	protected KThread thread;
	/** The priority of the associated thread. */
	protected int priority=priorityDefault;
	protected int effectivePriority = invalidPriority;
	protected static final int invalidPriority = -1;
	protected LinkedList<PriorityQueue> donation = new LinkedList<PriorityQueue>();
	
	
	
    }
}
