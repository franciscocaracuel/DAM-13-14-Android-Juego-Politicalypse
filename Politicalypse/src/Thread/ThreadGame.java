package Thread;

import android.graphics.Canvas;

import com.fyfstyle.politicalypse.view.ViewGame;

public class ThreadGame extends Thread{
	
	private ViewGame view;
	private transient boolean working=false;
	private static final long FPS=20;
	
	public ThreadGame(ViewGame view){
		this.view=view;
	}
	
	public boolean getWorking(){
		return working;
	}
	
	public void setWorking(boolean working){
		this.working=working;
	}
	
	public void run(){
		
		long initiation;
		long ticksPS=1000/FPS;
		long waitTime;
		Canvas canvas;
		
		while(working){
			
			canvas=null;
			initiation=System.currentTimeMillis();
			
			try{
				
				canvas=view.getHolder().lockCanvas();
				if(canvas!=null){
					synchronized (view.getHolder()) {
						view.draw(canvas);
					}
				}
				
			}
			
			finally{
				if(canvas!=null){
					view.getHolder().unlockCanvasAndPost(canvas);
				}
			}
			
			waitTime=ticksPS - (System.currentTimeMillis() - initiation);
			
			try{
				if(waitTime>0){
					sleep(waitTime);
				} else {
					sleep(100);
				}
			} catch(InterruptedException e){
				
			}
			
		}
		
	}

}
