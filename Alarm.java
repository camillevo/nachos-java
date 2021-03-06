package nachos.threads;

import nachos.machine.*;
import java.util.*; 
import java.util.concurrent.*; 
/**
Uses the hardware timer to provide preemption, and
to allow threads to sleep until a certain time.
 */

 /** ----------------- TASK 3 ---------------------

Complete the implementation of the Alarm class by implementing thewaitUntil(long x)method.
A thread calls waitUntil(long x) to suspend its execution until wall-clock time has advanced to at leastnow + x.
This method is useful for threads that operate in real time, such as blinking the cursor once per second.
There is no requirement that threads start running immediately after waking up; just put them on the ready queue in the timer interrupt handlerafter they have waited for atleast the right amount of time. 
Do not fork any additional threads to implementwaitUntil(); youneed only modifywaitUntil()and the timer interrupt handler methods.waitUntil()itself, though,is not limited to being called by one thread; any number of threads may call it and be suspended at anyone time. 
If the wait parameterxis0or negative, return without waiting (do not assert).
Note that only one instance of Alarm may exist at a time (due to a limitation of Nachos), and Nachos already creates one global alarm that is referenced viaThreadedKernel.alarm.
 -----------------------TESTS ---------------------
 Test ThreadGrader4.a: Tests waitUntil to  ensure it waits at least minimum amount of time  
b. Test ThreadGrader4.b: Tests whether waitUntil actually wakes up at correct time  
  */
/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		long now = Machine.timer().getTime();
		KThread.currentThread().yield();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
		//https://www.geeksforgeeks.org/treemap-in-java/
		//this
		long wakeTime = Machine.timer().getTime() + x;
		//If the wait parameter x is 0 or negative, return without waiting (do not assert).
		if(x <= 0){
			timerInterrupt();
		}
		TreeMap<KThread, int> tree_map = new TreeMap<Kthread, int>(); 
		//The thread is currently running. Only one thread can be in the RUNNING state at a time. In Nachos, the global variable currentThread always points to the currently running thread. 
		while(x > 0){
			tree_map(KThread.currentThread, x); 
			if(x > now){
				timerInterrupt(); 
			}
		}

		//TreeSet<KThread.java> ts = new TreeSet<KThread.java>();
		//long now = Machine.timer().getTime();
		/*
		if(now > wakeTime){
			ts.add(KThread); 
			timerInterrupt(); 
		}*/
		
		
		while (wakeTime > Machine.timer().getTime())
			KThread.yield();
	}
}

/**
SOURCES
https://howtodoinjava.com/java/collections/java-priorityqueue/
 */