package threadPool;

import java.util.ArrayList;
import java.util.List;

public class ThreadPoolTest {
	public static void main(String[] args) {
		ThreadPool tPool = ThreadPoolManager.getThreadPool(6);
		List<Runnable> taskList = new ArrayList<Runnable>();
		for (int i = 0; i < 1000000; i++) {
			taskList.add(new Task());			
		}
		tPool.execute(taskList);
		System.out.println(tPool);
		tPool.destroy();
	}
	
	static class Task implements Runnable{
		
        private static volatile int i = 1;
		@Override
		public void run() {
			System.out.println("当前处理线程是：" + Thread.currentThread().getName() + "执行线程" +i);			
		}
		
	}
}
