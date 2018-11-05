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
	
	Lock lock = new Lock();
	Condition speakReady = new Condition(lock);
	Condition listenReady = new Condition(lock);
	public int listener = 0, speaker = 0, send;
	
    public Communicator() 
    {
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
    	this.send = word;
	    	while(listener == 0) 
	    	{
	    		listenReady.sleep();
	    	}
	    speaker++;
	    speakReady.wake();
	    listener--;
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
		    while(speaker == 0)
		    {
		    	speakReady.sleep();
		    }
		listener++;
		listenReady.wake();
		speaker--;
		int got = this.send;
	    lock.release();	
	    return got;
    }
}





















