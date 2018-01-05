package threadPool;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolManager implements ThreadPool {

	private static int workerNum = 5;
	WorkThread[] workThreads;
	private static volatile int executeTaskNumber = 0;
	private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
	private static ThreadPoolManager threadPool;
	private AtomicLong threadNum = new AtomicLong();

	private ThreadPoolManager() {
		this(workerNum);
	}

	private ThreadPoolManager(int newWorkerNum) {
		if (newWorkerNum > 0) {
			workerNum = newWorkerNum;
		}
		workThreads = new WorkThread[workerNum];
		for (int i = 0; i < workerNum; i++) {
			workThreads[i] = new WorkThread();
			workThreads[i].setName("ThreadPool-worker" + threadNum.incrementAndGet());
			System.out.println("初始化线程数：" +(i+1)+"/"+(workerNum)+"------当前线程名称"
			                +Thread.currentThread().getName());
			workThreads[i].start();
		}
	}

	@Override
	public void execute(Runnable task) {
		synchronized (taskQueue) {
			try {
				taskQueue.put(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
			taskQueue.notifyAll();
		}
	}

	@Override
	public void execute(Runnable[] tasks) {
		synchronized (taskQueue) {
			for (Runnable runnable : tasks) {
				try {
					taskQueue.put(runnable);
				} catch (Exception e) {
					e.printStackTrace();
				}
				taskQueue.notifyAll();
			}
		}
	}

	@Override
	public void execute(List<Runnable> tasks) {
		synchronized (taskQueue) {
			for (Runnable runnable : tasks) {
				try {
					taskQueue.put(runnable);
				} catch (Exception e) {
					e.printStackTrace();
				}
				taskQueue.notifyAll();
			}
		}

	}

	@Override
	public int getExecuteTaskNumber() {

		return executeTaskNumber;
	}

	@Override
	public int getWaitTaskNumber() {

		return taskQueue.size();
	}

	@Override
	public int getWorkThreadNumber() {

		return workerNum;
	}

	public static ThreadPool getThreadPool() {
		return getThreadPool(workerNum);
	}

	public static ThreadPool getThreadPool(int newWorkerNum) {
		if (newWorkerNum <= 0) {
			newWorkerNum = workerNum;
		}
		if (threadPool == null) {
			threadPool = new ThreadPoolManager(newWorkerNum);
		}
		return threadPool;
	}

	@Override
	public void destroy() {
		while (!taskQueue.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < workerNum; i++) {
			workThreads[i].stopWorker();
			workThreads[i] = null;
		}
		threadPool = null;
		taskQueue.clear();
	}

	private class WorkThread extends Thread {
		private boolean isRunning = true;

		@Override
		public void run() {
         
			Runnable r = null;
			while (isRunning) {
				synchronized (taskQueue) {
					while (isRunning && taskQueue.isEmpty()) {
						try {
							taskQueue.wait(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			    if (!taskQueue.isEmpty()) {
			    	try {
						r = taskQueue.take();
//						r.run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			    if (r != null) {
					r.run();
				}
				executeTaskNumber++;
				r = null;
			}			
		}
		public void stopWorker() {
			isRunning = false;
		}
	}	 
}
