package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator
{
    /**
     * Allocate a new communicator.
     */

    public Communicator() 
    {
    	lock = new Lock(); // create public lock
    	speakReady = new Condition(lock); // public speaker lock
    	listenReady = new Condition(lock); // public listener lock
    	ready = new Condition(lock); // public conditional lock
    	listener = 0; // listener count initialize
    	//speaker = 0;
    	readyup = false; // public boolean to check if ready to pass
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word)
    {
    	//boolean intStatus = Machine.interrupt().disable(); // disable interrupts (Like in KThread)
    	lock.acquire();
    		// check if listener is ready then put speaker to sleep
	    	while(readyup) 
	    	{
	    		//speaker++;
	    		speakReady.sleep();
	    		//listener--;
	    	}
	    
	    this.send = word; // store word
	    readyup = true; // set ready as true
	    
	    	// if listener not ready, then ready to sleep
	    	while(listener == 0)
	    	{
	    		//speaker++;
	    		ready.sleep();
	    		//listener--;
	    		
	    	}
	    
	    listenReady.wake();
	    ready.sleep();
	    readyup = false;
	    speakReady.wake();
    	lock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() 
    {
    	lock.acquire();
    	listener++;
    	
    		// if listener waiting and ready called, ready wake up, or listen sleep
		    if(listener == 1 && readyup)
		    {
		    	//listener++;
		    	ready.wake();
		    	//speaker--;
		    }
		    
		listenReady.sleep();
		ready.wake();
		listener--;
		//speakReady.wake();
		//speaker--;
		    	
		//int got = this.send;
	    lock.release();	
	    return this.send;
    }
    
    private Lock lock; // create lock
    private Condition speakReady; // create speaking condition
    private Condition listenReady; // create listening condition
    private Condition ready; // create ready condition
    private int send; // to store word
    //private int speaker;
    private int listener; // to count when listener is called 
    private boolean readyup; // to track if ready to pass condition
}





















