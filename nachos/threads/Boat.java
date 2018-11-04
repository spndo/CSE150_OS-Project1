package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
    static int children_on_oahu; // number of children on oahu
    static int children_on_molokai; // number of children on molokai
    static int adult_on_oahu; // number of adults on oahu
    static int adult_on_molokai; // number of adults on molokai
    static Lock thread_lock; // create a lock
    static Condition2 child_oahu; // lock for children on oahu
    static Condition2 child_molokai; // lock for children on molokai
    static Condition2 adult_oahu; // lock for adult on oahu; since adult can only row to molokai we don't need condition variable for adult on molokai
    static boolean isDriver; // determine current thread is pilot or traveler; for child thread only
    static boolean adult_can_row; // determine whether adult can take boat
    static boolean boat_on_oahu; // determine where is the boat, true for oahu and false for molokai
    static boolean isGameOver; // condition variable to determine whether the game is over

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
        thread_lock = new Lock();
        child_oahu = new Condition2(thread_lock);
        child_molokai = new Condition2(thread_lock);
        adult_oahu = new Condition2(thread_lock);
        isDriver = true;
        adult_can_row = false;
        boat_on_oahu = true;
        isGameOver = false;

        // Create threads here. See section 3.4 of the Nachos for Java
        // Walkthrough linked from the projects page.

        Runnable childrenCan = new Runnable() {
            public void run() {
                ChildItinerary();
            }
        };
        for (int i = 0; i < children; i++) {
            KThread childThread = new KThread(childrenCan);
            childThread.setName("Children" + i);
            childThread.fork();
        }
        Runnable adultCan = new Runnable() {
            public void run() {
                AdultItinerary();
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
        if (!adult_can_row && !boat_on_oahu) {
            adult_oahu.sleep();
        }
        bg.AdultRowToMolokai(); // one adult row to molokai
        adult_on_oahu--;
        adult_on_molokai++;
        boat_on_oahu = false;
        adult_can_row = false;
        child_molokai.wake(); // wake up a child on molokai; the child should row boat back to oahu

        thread_lock.release();
    }

    static void ChildItinerary()
    {
        thread_lock.acquire();

        while (!isGameOver) {
            if (boat_on_oahu) {
                if (adult_can_row) {
                    adult_oahu.wake();
                    child_oahu.sleep();
                }
                if (isDriver) {
                    bg.ChildRowToMolokai();
                    isDriver = false;
                    children_on_oahu--;
                    children_on_molokai++;
                    child_oahu.wake();
                    child_molokai.sleep();
                } else {
                    bg.AdultRideToMolokai();
                    isDriver = true;
                    children_on_oahu--;
                    children_on_molokai++;
                    if (children_on_oahu == 0 && adult_on_oahu == 0) {
                        isGameOver = true;
                        child_oahu.sleep();
                    } else if (children_on_oahu == 0 && adult_on_oahu != 0) {
                        adult_can_row = true;
                    }
                    child_molokai.wake();
                    child_molokai.sleep();
                }
            } else {
                bg.ChildRowToOahu();
                children_on_molokai--;
                children_on_oahu++;
                if (adult_can_row) {
                    adult_oahu.wake();
                } else {
                    child_oahu.wake();
                }
                child_oahu.sleep();
            }
        }

        thread_lock.release();
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
