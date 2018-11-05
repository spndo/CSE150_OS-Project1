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
	int send, speaker, listener;
	Condition speakReady = new Condition(lock);
	Condition listenReady = new Condition(lock);
	
    public Communicator() 
    {
    	listener = 0;
    	speaker = 0;
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
	    	if(listener == 0) 
	    	{
	    		speaker++;
	    		speakReady.sleep();
	    		listener--;
	    	}
	    	else
	    	{
	    		speaker++;
	    		listenReady.wake();
	    		listener--;
	    	}
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
		    if(speaker == 0)
		    {
		    	listener++;
		    	speakReady.sleep();
		    	speaker--;
		    }
		    else
		    {
		    	listener++;
		    	speakReady.wake();
		    	speaker--;
		    }
		//int got = this.send;
	    lock.release();	
	    return this.send;
    }
}





















