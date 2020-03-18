package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 * 
 * <p>
 * You must implement this.
 * 
 * @see nachos.threads.Condition
 */
public class Condition2 {
	/**
	 * Allocate a new condition variable.
	 * 
	 * @param conditionLock the lock associated with this condition variable. The
	 *                      current thread must hold this lock whenever it uses
	 *                      <tt>sleep()</tt>, <tt>wake()</tt>, or
	 *                      <tt>wakeAll()</tt>.
	 */
	public Condition2(Lock conditionLock) {
		this.conditionLock = conditionLock;
		waitQueue = new LinkedList<KThread>();
	}

	/**
	 * Atomically release the associated lock and go to sleep on this condition
	 * variable until another thread wakes it using <tt>wake()</tt>. The current
	 * thread must hold the associated lock. The thread will automatically reacquire
	 * the lock before <tt>sleep()</tt> returns.
	 */
	public void sleep() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());

		conditionLock.release();
		boolean intStatus = Machine.interrupt().disable();
		
		waitQueue.add(KThread.currentThread());

		// Should I be using KThread.sleep() or KThread.currentThread().sleep()?
		//KThread.currentThread().sleep();
		KThread.sleep();
		conditionLock.acquire();
		Machine.interrupt().restore(intStatus);

	}

	/**
	 * Wake up at most one thread sleeping on this condition variable. The current
	 * thread must hold the associated lock.
	 */
	public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		
		boolean intStatus = Machine.interrupt().disable();
		if (!waitQueue.isEmpty()) {
			waitQueue.pop().ready();
		}
		Machine.interrupt().restore(intStatus);

	}

	/**
	 * Wake up all threads sleeping on this condition variable. The current thread
	 * must hold the associated lock.
	 */
	public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		
		boolean intStatus = Machine.interrupt().disable();
		while (!waitQueue.isEmpty()) {
			waitQueue.pop().ready();
		}
		Machine.interrupt().restore(intStatus);
	}

	/**
	 * Atomically release the associated lock and go to sleep on this condition
	 * variable until either (1) another thread wakes it using <tt>wake()</tt>, or
	 * (2) the specified <i>timeout</i> elapses. The current thread must hold the
	 * associated lock. The thread will automatically reacquire the lock before
	 * <tt>sleep()</tt> returns.
	 */
	public void sleepFor(long timeout) {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		
		conditionLock.release();
		boolean intStatus = Machine.interrupt().disable();
		long beginTime = System.currentTimeMillis();

		while(System.currentTimeMillis() < (beginTime + timeout)) {
			waitQueue.add(KThread.currentThread());
			KThread.sleep();
		}
		
		waitQueue.add(KThread.currentThread());
		while(System.currentTimeMillis() < (beginTime + timeout)) {
			this.sleep();
		}
		
		
		waitQueue.add(KThread.currentThread());
		while(System.currentTimeMillis() < (beginTime + timeout)) {
			KThread.yield();
		}
		
		
		
		conditionLock.acquire();
		Machine.interrupt().restore(intStatus);


	}

	private Lock conditionLock;
	private LinkedList<KThread> waitQueue;
}
