package server;

public class ThreadPool {
	public final static int MAX_THREADS = 20;
	private PoolThread[] threads = new PoolThread[MAX_THREADS];
	//public boolean running = false;
	private int current_threads = 0;
	
	ThreadPool(){
		for(int i=0; i<MAX_THREADS; i++){
			threads[i] = new PoolThread();
			threads[i].id = i;
		}
		//running = true;
		
	}
	/*
	protected synchronized void stoppool(PoolThread repoolingThread){
		running = false;
	}
	*/
	public synchronized void add(Runnable target)
    {
		PoolThread thread = null;
		//System.out.println(threads[0].running);
        for(int i=0; i<MAX_THREADS; i++) if (threads[i].running && threads[i].idle){
        	thread = threads[i];
        	break;
        }
        if (thread == null){
        	for (int i=0; i<MAX_THREADS; i++) if (threads[i].running == false){
        		thread = threads[i];
        		break;
        	}
        	if (thread == null){
        		//Ïß³Ì³ØÒÑÂú
        		return;
        	}else{
        		current_threads++;
        		thread.running = true;
        		thread.idle = false;
        		thread.start();
        	}
        }
        	
        thread.setTarget(target);
        
    }
	public synchronized int get_currentthread_num(){
		return current_threads;
	}
}


class PoolThread extends Thread{
	public int id;
	public boolean running = false;
	public boolean idle = true;
	Runnable target = null;
	
	public synchronized void setTarget(Runnable target){
		this.target = target;
		notifyAll();
	}
	
	public void run(){
		while(running){
			idle =false;
			if (target != null){
				System.out.println("thread id:"+id);
				target.run();
			}
			idle = true;
			try{
				synchronized (this){
					wait();
				}
			}catch(InterruptedException ie){
				
			}
			idle = false;
		}
	}
}