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
    	lock = new Lock();
    	speakReady = new Condition(lock);
    	listenReady = new Condition(lock);
    	ready = new Condition(lock);
    	listener = 0;
    	//speaker = 0;
    	readyup = false;
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
    	
	    	while(readyup) 
	    	{
	    		//speaker++;
	    		speakReady.sleep();
	    		//listener--;
	    	}
	    readyup = true;
	    this.send = word;
	    
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
    
    private Lock lock;
    private Condition speakReady;
    private Condition listenReady;
    private Condition ready;
    private int send;
    //private int speaker;
    private int listener;
    private boolean readyup;
}





















