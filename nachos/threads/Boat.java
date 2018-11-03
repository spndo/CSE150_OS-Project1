package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
    static int children_on_oahu; // number of children on oahu
    static int children_on_molokai; // number of children on molokai
    static int adult_on_oahu; // number of adults on oahu
    static int adult_on_molokai; // number of adults on molokai
    static Lock thread_lock = new Lock(); // create a lock
    static Condition2 child_oahu = new Condition2(thread_lock); // lock for children on oahu
    static Condition2 child_molokai = new Condition2(thread_lock); // lock for children on molokai
    static Condition2 adult_oahu = new Condition2(thread_lock); // lock for adult on oahu; since adult can only row to molokai we don't need condition variable for adult on molokai
    static isPilot = true; // determine current thread is pilot or traveler; for child thread only 
    static boat_on_oahu = true; // determine where is the boat, true for oahu and false for molokai
    static boolean isGameOver = false; // condition variable to determine whether the game is over

    public static void selfTest()
    {
        BoatGrader b = new BoatGrader();
        
        System.out.println("\n ***Testing Boats with only 2 children***");
        begin(0, 2, b);

        //	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
        //  	begin(1, 2, b);

        //  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
        //  	begin(3, 3, b);
    }

    public static void begin( int adults, int children, BoatGrader b )
    {
        // Store the externally generated autograder in a class
        // variable to be accessible by children.
        bg = b;

        // Instantiate global variables here
        children_on_oahu = children;
        adult_on_oahu = adults;
        children_on_molokai = 0;
        adult_on_molokai = 0;
        
        // Create threads here. See section 3.4 of the Nachos for Java
        // Walkthrough linked from the projects page.

        Runnable childrenCan = new Runnable() {
                public void run() {
                    childItinerary();
                }
            };
        for (int i = 0; i < child; i++) {
            KThread childThread = new KThread(childrenCan);
            childThread.setName("Children" + i);
            childTrhead.fork();
        }
        Runnable adultCan = new Runnable() {
                public void run() {
                    adultItinerary();
                }
            };
        for (int i = 0; i < adults; i++) {
            KThread adultThread = new KThread(adultCan);
            adultThread.setName("Adult" + i);
            adultThread.fork();
        }
        
    }
    
    // constraint: adult can only row to molokai
    static void AdultItinerary()
    {
        /* This is where you should put your solutions. Make calls
           to the BoatGrader to show that it is synchronized. For
           example:
           bg.AdultRowToMolokai();
           indicates that an adult has rowed the boat across to Molokai
        */
        thread_lock.acquire(); // gain the lock

        // current adult thread goes to sleep when it is not adult's turn and boat is not on oahu
        if (boat_on_oahu == false) {
            adult_oahu.sleep();
        }
        bg.AdultRowToMolokai(); // one adult row to molokai
        adult_on_oahu--; 
        adult_on_molokai++;
        boat_on_oahu = false;
        child_molokai.wake(); // wake up a child on molokai; the child should row boat back to oahu

        thread_lock.release();
    }

    static void ChildItinerary()
    {
    }
    
    static void SampleItinerary()
    {
        // Please note that this isn't a valid solution (you can't fit
        // all of them on the boat). Please also note that you may not
        // have a single thread calculate a solution and then just play
        // it back at the autograder -- you will be caught.
        System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
        bg.AdultRowToMolokai();
        bg.ChildRideToMolokai();
        bg.AdultRideToMolokai();
        bg.ChildRideToMolokai();
    }
    
}
