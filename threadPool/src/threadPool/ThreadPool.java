package threadPool;

import java.util.List;

public interface ThreadPool {
	
	void execute(Runnable task);
	
	void execute(Runnable [] tasks);
	
	void execute(List<Runnable> tasks);
	
	int getExecuteTaskNumber();
	
	int getWaitTaskNumber();
	
	int getWorkThreadNumber();
	
	void destroy();

}
