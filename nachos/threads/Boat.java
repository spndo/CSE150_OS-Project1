 package nachos.threads;

 import nachos.ag.BoatGrader;
 import nachos.machine.*;

 public class Boat {
 	// global variables to check people on island.
 	//counter to hold, but only can be accessed thru threads
 	static int childOnO = 0;
 	static int childOnM = 0;
 	static int adultsOnO = 0;
 	static int adultsOnM = 0;
 	static int pplOnBoat = 0;		//# of people on boat, only used in child case
 	static int boatLocation = 0;		//boat is on Oahu

 	static Lock threadLock = new Lock();
 	//communicate how many people
 	static Communicator c = new Communicator();

 	//used to account for those on Oahu
 	static Condition2 CurrentlyOnOahu = new Condition2(threadLock);
 	//used to account for those on Molokai
 	static Condition2 CurrentlyOnMolokai = new Condition2(threadLock);
 	//used to account for the threads adult/child getting on the boat
 	static Condition2 getOnBoat = new Condition2(threadLock);

 	static BoatGrader bg;

 	public static void selfTest()
 	{
 		BoatGrader b = new BoatGrader();

 		System.out.println("\n ***Testing Boats with only 2 children***");
 		begin(0, 2, b);

 		// System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
 		// begin(1, 2, b);

 		// System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
 		// begin(3, 3, b);
 	}

 	public static void begin(int adults, int children, BoatGrader b)
 	{
 		// Store the externally generated autograder in a class
 		// variable to be accessible by children.
 		bg = b;

 		// Instantiate global variables here

 		/*They can remember how many people are on each island but knowingly beforehand
 		they should both be 0.*/

 		childOnO = children;
 		childOnM = 0;
 		adultsOnO = adults;
 		adultsOnM = 0;

 		// Create threads here. See section 3.4 of the Nachos for Java
 		// 2 Threads created here, Child & Adult Thread

 		// Walkthrough linked from the projects page.

 		//the child thread
 		Runnable childCan = new Runnable()
 		{
 			public void run()
 			{
 				int island = 0;
 				ChildItinerary(island);
 			}
 		};

 		for(int i = 1; i <= children; i++)
 		{
 			KThread childThread = new KThread(childCan);
 			childThread.setName("Child Thread: " + i);
 			childThread.fork();
 		}



 		//the adult thread
 		Runnable adultCan = new Runnable()
 		{
 			public void run()
 			{
 				int island = 0;
 				AdultItinerary(island);
 			}
 		};

 		for(int j = 1; j <= adults; j++)
 		{
 			KThread adultThread = new KThread(adultCan);
 			adultThread.setName("Adult Thread: " + j);
 			adultThread.fork();
 		}


 		/*ensure there is an end when all the people moved from the threads to Molokai
 		equals the amount of adults + children intially. */
 		while(true)
 		{
 			int totalPeople = c.listen();
 			System.out.println("Total Peopel On Molokai: " + totalPeople);
 			if(totalPeople == (children + adults))
 			{
 				break;
 			}
 		}

 	}

 	static void AdultItinerary(int island)
 	{
 		/*
 		 * This is where you should put your solutions. Make calls to the BoatGrader to
 		 * show that it is synchronized. For example: bg.AdultRowToMolokai(); indicates
 		 * that an adult has rowed the boat across to Molokai
 		 */

 		threadLock.acquire();

 		while(true)
 		{
 			if(island == 0)	//check if the adult is on Oahu
 			{
 				/*check if boat is not on Oahu, if there are > 1 child on Oahu, check spaces 
 				on boat because it cant be greater than 2 */
 				while (boatLocation != 0 || childOnO > 1 || pplOnBoat > 0 )
 				{
 					//sleeps, because the adult can row by himself over from Oahu to Molokai
 					CurrentlyOnOahu.sleep();
 				}
 				//calls the rows
 				bg.AdultRowToMolokai();

 				//update global variables
 				adultsOnO--;
 				adultsOnM++;
 				boatLocation = 1;
 				island = 1;

 				//total number of people that are currently on Molokai
 				//returns said value
 				c.speak(childOnM + adultsOnM);

 				//after the adult gets there, makes sure everyone is asleep
 				CurrentlyOnMolokai.wakeAll();
 				CurrentlyOnMolokai.sleep();

 				Lib.assertTrue(childOnO > 0);
 			}
 			else if(island == 1)		//checks if the adult is on Molokai
 			{
 				CurrentlyOnMolokai.sleep();
 			}
 			else
 			{
 				Lib.assertTrue(false);	//will ensure the break from the loop
 				break;
 			}
 		}

 		threadLock.release();

 	}

 	static void ChildItinerary(int island)
 	{
 		threadLock.acquire();

 		while(true)
 		{
 			if(island == 0)	//check if child is on Oahu
 			{
 				while(pplOnBoat >= 2 || (adultsOnO > 0 && childOnO == 1))
 				{
 					CurrentlyOnOahu.sleep();
 				}

 				CurrentlyOnOahu.wakeAll();

 				if(adultsOnO == 0 && childOnO ==1)		//case where only 1 child left
 				{
 					bg.ChildRowToMolokai();	//last child rows theres
 					childOnO--;				//childOnO == 0 in this instance
					childOnM++;
 					pplOnBoat = 0;			//ensure no one on the boat
 					island = 1;				//notify that we are on Molokai
 					boatLocation = 1;		//boat is at Molokai

 					c.speak(childOnM + adultsOnM);	//total number of people that are currently on Molokai
 					CurrentlyOnMolokai.sleep();

 				}
 				else if (childOnO > 1)		//case where more than 1 child still on Oahu
 				{
 					/*Test case where if there are more than 1 children then obviously
 					 * two children could board the boat, however explicitly we have to 
 					 * call ChildRowToMolokai as the pilot, and ChildRideToMolokai
 					 *  for the passenger*/

 					if(pplOnBoat == 0)		
 					{
 						pplOnBoat++;		//pilot child gets on
 						getOnBoat.sleep();
 						/*Waits until the second child boards then continues*/

 						childOnO--;				//accounts for pilot child leaving Oahu
 						bg.ChildRowToMolokai();	//pilot child rows to Oahu
 						childOnM++;				//pilot child arrives at Molokai
 						island = 1;				//currently at Molokai

 						getOnBoat.wake();		//wakes other child thread, the passenger
 						CurrentlyOnMolokai.sleep();	
 					}
 					else if (pplOnBoat == 1)
 					{
 						pplOnBoat++;				//should be 2 ppl/children on board the boat

 						getOnBoat.wake();		//wakes the child who is the pilot
 						getOnBoat.sleep();		

 						childOnO--;				//accounts for child passenger leaving Oahu
 						bg.ChildRideToMolokai();		//passenger only calls the RideToMolokai
 						pplOnBoat = pplOnBoat - 2;	//when both child gets off the boat, should =0
 						childOnM++;					//passenger child arrives at Molokai
 						island = 1;					//they are currently at Molokai
 						boatLocation = 1;			//boat also located at Molokai

 						c.speak(childOnM + adultsOnM);	//communicates back the total # on people that already arrived
 						CurrentlyOnMolokai.wakeAll();	//WakeAll threads then sleep em
 						CurrentlyOnMolokai.sleep();			
 					}
 				}		
 			}
 			else if (island == 1)	//child is now on Molokai
 			{
 				while(boatLocation != 1)		//if boat is not there 
 				{
 					CurrentlyOnMolokai.sleep();		//sleep
 				}

 				childOnM--;
 				bg.ChildRowToOahu();
 				childOnO++;
 				boatLocation = 0;
 				island = 0;

 				CurrentlyOnOahu.wakeAll();
 				CurrentlyOnOahu.sleep();	
 			}
 			else
 			{
 				Lib.assertTrue(false);		//will ensure the break from the loop
 				break;
 			}
 		}

 		threadLock.release();		//release the lock

 	}

 	static void SampleItinerary()
 	{

 	}

 }
