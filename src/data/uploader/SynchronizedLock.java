package data.uploader;

import android.annotation.SuppressLint;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class SynchronizedLock implements  java.util.concurrent.locks.Lock {

	@SuppressLint("UseValueOf")
	private Integer lock_integer = new Integer(0);

	public static final SynchronizedLock sharedLock= new SynchronizedLock();
	
	@Override
	public void lock() {
		synchronized(lock_integer ){
			lock_integer = 1;
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		synchronized(lock_integer ){
			return lock_integer == 0;
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		synchronized(lock_integer ){
			lock_integer = 0;
		}
	}
	
}
