package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
    static int children_on_oahu;
    static int children_on_molokai;
    static int adult_on_oahu;
    static int adult_on_molokai;
    static Lock thread_lock = new Lock();
    static Condition2 child_oahu = new Condition2(thread_lock);
    static Condition2 child_molokai = new Condition2(thread_lock);
    static Condition2 adult_oahu = new Condition2(thread_lock);
    static isPilot = true;
    static isAdultTurn = false;
    static boat_on_oahu = true;
    static boolean isGameOver = false;

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
    
    static void AdultItinerary()
    {
        /* This is where you should put your solutions. Make calls
           to the BoatGrader to show that it is synchronized. For
           example:
           bg.AdultRowToMolokai();
           indicates that an adult has rowed the boat across to Molokai
        */
        
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
