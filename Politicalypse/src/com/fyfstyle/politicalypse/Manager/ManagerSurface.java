package com.fyfstyle.politicalypse.Manager;

import com.fyfstyle.politicalypse.view.ViewGame;

import Thread.ThreadGame;
import android.view.SurfaceHolder;

public class ManagerSurface implements SurfaceHolder.Callback{
	
	private ThreadGame threadGame;
	
	private int width, height;
	
	//En este constructor se le pasa el ViewGame por si tenemos que trabajar con el
	public ManagerSurface(ViewGame view, ThreadGame threadGame){
		super();
		this.threadGame=threadGame;
	}

	//Cada vez que la superficie cambie se actualizaran estas dos variables
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		
		this.width=width;
		this.height=height;
		
	}

	//Cada vez que se crea la superficie del juego se pone el juego en iniciado y se lanza el hilo
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		threadGame.setWorking(true);
	
		try{
			threadGame.start();
		} catch(IllegalThreadStateException e){
			
		}
						
	}

	//Cada vez que la superficie del juego sea eliminada se parara el hilo
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		boolean retry=true;
		
		threadGame.setWorking(false);
		
		//Intentara para el hilo hasta que lo consiga
		while(retry){
			
			try{
				threadGame.join();
				retry=false;
			} catch(InterruptedException e){
				
			}
			
		}
		
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public void setWidth(int width){
		this.width=width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public void setHeight(int height){
		this.height=height;
	}
	
	public void setWorking(boolean working){
		threadGame.setWorking(working);
	}
	
	public boolean getWorking(){
		return threadGame.getWorking();
	}

}
