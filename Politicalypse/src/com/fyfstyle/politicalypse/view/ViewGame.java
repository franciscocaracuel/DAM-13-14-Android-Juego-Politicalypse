package com.fyfstyle.politicalypse.view;

import java.util.ArrayList;

import com.fyfstyle.politicalypse.R;
import com.fyfstyle.politicalypse.Manager.ManagerSurface;

import Figure.Political;
import Thread.ThreadGame;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewGame extends SurfaceView {
	
	private Context context;
	private View viewMainActivity;
	private SurfaceHolder containerSurface;
	private ThreadGame threadGame;
	private ManagerSurface managerSurface;
	private Bitmap bmpRajoy;
	private ArrayList<Political> listaPolitical;
	
	//lost sabe si algun politico ha llegado a la puerta
	//running se usa para el play|pause --> no tiene nada que ver con saber si la app se esta ejecutando o no
	private boolean lost=false, running=true;
	
	//No se puede modificar views desde otro sitio que no sea su activity
	//Para eso se crea un Activity a partir del contexto y en un hilo del activity se modifica el textview
	private Activity activityMain;
	
	//quantityByWave define cuantos muñecos saldran por cada oleada
	private int quantityByWave=5;		
	private int wave=1;
	
	private VelocityTracker speedControl;
	
	//posTouch se usa en el onTouch para saber que muñeco se ha pulsado y saber cual hay que mover
	private int posTouch=0;
	
	//Contador que va aumentando segun los politicos que se sacan de la pantalla
	private int politicalsKilled=0;
	
	private MediaPlayer mp;
	
	//Este constructor se pone solo porque se quejaba Eclipse//
	//////////////////////////////////////NO SE USA/////////////////////////////////////////
	public ViewGame(Context context){
		super(context, null);
	}
	
	/////////////////////////////////Constructor válido/////////////////////////////////////////
	public ViewGame(Context context, View viewMainActivity) {
		super(context);
		
		this.context=context;
		
		//////////Se crea el activity a partir del contexto//////////
		activityMain=(Activity)context;
		////////////////////////////////////////////////////////////////////////////////////////
		
		this.viewMainActivity=viewMainActivity;
		
		threadGame=new ThreadGame(this);
		
		containerSurface=getHolder();
		
		/////////////////////////Pone el fondo transparente//////////////////////////////
		this.setZOrderOnTop(true);
		containerSurface.setFormat(PixelFormat.TRANSPARENT);
		/////////////////////////////////////////////////////////////////////////////////////////////////////
				
		
		managerSurface=new ManagerSurface(this, threadGame);
		
		if(!threadGame.isAlive())
		containerSurface.addCallback(managerSurface);
		
		
		////////////////////////////////Se define el dibujo que va a pintar///////////////////////////////////////////
		bmpRajoy=BitmapFactory.decodeResource(getResources(), R.drawable.rajoy);
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
		
		/////////////////Se crea un arraylist con la cantidad de muñecos que apareceran////////////
		listaPolitical=new ArrayList<Political>();
		
		for(int i=0;i<quantityByWave; i++){
			listaPolitical.add(null);
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		////////////////////Se define el sonido que reproucira el mediaPlayer.////////////////////////
		mp=MediaPlayer.create(context, R.raw.coin);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
	}
	
	@Override
	public void draw(Canvas canvas){
		
		//Con este running sabemos si se ha pausado o no. Si es true se está jugando
		if(running){
			
			//Si aun no ha perdido. En este if entra en el estado normal del juego
			if(!lost){
				
				//Borra todo lo que haya en la pantalla
				canvas.drawColor(0, Mode.CLEAR);
				
				//Se recorre todo el for y pondra tantos muñecos como tamaño tenga el arraylist
				for(int i=0;i<listaPolitical.size();i++){
					
					if(listaPolitical.get(i)==null){
						listaPolitical.set(i, new Political(this, bmpRajoy, i, context));
					}
					
					//Dibuja el muñeco
					listaPolitical.get(i).draw(canvas);
					
				}
				
			} else{
				
				//Esto se ejecuta en el hilo principal. Así podemos utilizar vistas que estan en el
				activityMain.runOnUiThread(new Runnable() {
		            @Override
		            public void run() {     
		            	
		            	//Se definen las variables graficas necesarias.
		            	//Se ocultara el boton play/pause
		            	
		            	final Button btPlayPause=(Button)viewMainActivity.findViewById(R.id.btPlayPause);
		        		btPlayPause.setVisibility(View.INVISIBLE);
		        		
		        		Animation moverPlayPause = AnimationUtils.loadAnimation(context, R.anim.transladar_abajo_arriba_wave);
	            		moverPlayPause.reset();            		

	            		btPlayPause.startAnimation(moverPlayPause);	            		
	            		
	            		moverPlayPause.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								
								btPlayPause.setVisibility(View.INVISIBLE);
								
							}
						});
		            	
	            		//Este metodo cambia el texto del TextView que le pasemos y le pone el String que le pasemos como segundo parametro
		            	setText(R.id.tvPoliticalKilled, getResources().getString(R.string.political_killed_1)+" "+politicalsKilled+" "+getResources().getString(R.string.political_killed_2), 0);	 
		            	setText(R.id.tvNumberWaves, getResources().getString(R.string.waves)+" "+wave, 0);
		            	setText(R.id.tvTotalPunctuation, getResources().getString(R.string.total_punctuation)+" "+(wave*politicalsKilled), 0);
		            	
		            	//Se muestra el linearLayout donde se ve la puntuacion
		            	final LinearLayout llYouLost=(LinearLayout)viewMainActivity.findViewById(R.id.llYouLost);
		        		
		        		Animation moverLost = AnimationUtils.loadAnimation(context, R.anim.transladar_izquierda_derecha_lost);
	            		moverLost.reset();            		

	            		llYouLost.startAnimation(moverLost);
	            		
	            		moverLost.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {

				        		llYouLost.setVisibility(View.VISIBLE);
								
							}
						});
		            	            		
		            	LinearLayout llSurface=(LinearLayout)viewMainActivity.findViewById(R.id.llSurface);
		            	llSurface.removeAllViews();

		            }
		        });
				
			}
			
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (running) {

			switch (event.getAction()) {

			// Cada vez que se pulsa
			case MotionEvent.ACTION_DOWN:

				float x,
				y;
				x = event.getX();
				y = event.getY();

				synchronized (getHolder()) {

					// Se recorre todo el arraylist para saber que muñeco se ha
					// pulsado
					for (int i = 0; i < listaPolitical.size(); i++) {

						try {

							// Si ha sido tocado
							if (listaPolitical.get(i).touch(x, y)) {

								// Para poder mover despues el muñeco pulsado se
								// guarda el id al que corresponde
								posTouch = i;

								// Se le quita la velocidad para que se mueva a
								// donde queremos
								if (speedControl == null) {
									speedControl = VelocityTracker.obtain();
								} else {
									speedControl.clear();
								}
								speedControl.addMovement(event);
							}

						} catch (NullPointerException e) {

						}

					}

				}

				break;

			// Detecta el movimiento del dedo
			case MotionEvent.ACTION_MOVE:

				// Le asignara la velocidad y el movimiento que hagamos con el
				// dedo
				if (speedControl != null) {

					speedControl.addMovement(event);
					speedControl.computeCurrentVelocity(1000);

					float speedX = VelocityTrackerCompat.getXVelocity(
							speedControl,
							event.getPointerId(event.getActionIndex()));
					float speedY = VelocityTrackerCompat.getYVelocity(
							speedControl,
							event.getPointerId(event.getActionIndex()));

					// speedX y speedY devuelven valores muy altos. Se divide
					// para que el muñeco no se vaya disparado
					listaPolitical.get(posTouch).setSpeed(speedX / 50,
							speedY / 50);

				}

				break;

			// Cada vez que se levante el dedo
			case MotionEvent.ACTION_UP:
				if (speedControl != null) {
					speedControl.recycle();
				}
				speedControl = null;

			}

		}

		return true;		
		
	}
	
	//Devuelve si ha perdido
	public void setLost(boolean lost){
		this.lost=lost;
	}
	
	//Se ejecuta cada vez que se elimina un politico
	public void politicalKilled(int id){
		
		//Se escuchara el sonido de la moneda
		mp.start();
		
		//Si hemos matado a alguno se pone nulo para que vuelva a crearlo
		listaPolitical.set(id, null);
		
		//Aumentamos el contador
		politicalsKilled++;
		
		//Se llama al metodo que se ejecuta el hilo principal. Se le pasa el id del textview y el valor
		setText(R.id.tvPunctuation, politicalsKilled+"", 0);
		
		//Si los politicos matados son mayores que la oleada por la cantidad se le añadira otra cantidad de politicos
		//Por defecto la cantidad por oleada es 5
		//Si está en la oleada 1 habra 5 politicos.
		//Si esta en la oleada 2 habrá 10 politicos
		//Si esta en la oleada 3 habra 15 politicos....
		if(politicalsKilled>15*wave){

			wave++;
			
			setText(R.id.tvWave, wave+"", 1);
			
			//Si se añaden muchos elementos al arraylist tiene un bajon de rendimiento
			//Cuando llegue a 10 oleadas no crecerá más
			if(wave<=10){
				for(int i=0;i<quantityByWave;i++){
					listaPolitical.add(null);
				}				
			}
						
		}
		
	}
	
	//Este metodo pone en el textview que se le pase, el string que se le pase
	//Action puede ser cualquier numero o 1. Si es cualquier numero no hace nada.
	//Si es 1 se muece el cartel de las oleadas hacia arriba, se actualiza y vuelve a bajar
	private void setText(final int resource, final String text, final int action){
		
		activityMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {     
            	
            	final TextView tv=(TextView)viewMainActivity.findViewById(resource);
            	
            	if(action==1){
            		
            		Animation moverWave = AnimationUtils.loadAnimation(context, R.anim.transladar_abajo_arriba_wave);
            		moverWave.reset();

            		tv.startAnimation(moverWave);
            		
            		moverWave.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							
							tv.setText(wave+"");
							
							Animation moverWave = AnimationUtils.loadAnimation(context, R.anim.transladar_arriba_abajo_wave);
		            		moverWave.reset();
		            		
		            		tv.startAnimation(moverWave);
							
						}
					});
            	
            	//Este tv seria el de la puntuacion
            	} else if(action==0){            		
                	tv.setText(text);
            	}
            	            	
            }
        });
		
	}
	
	//Esto me comprueba si estoy jugando pero tengo la pantalla de reintentar
	public void setRunning(boolean running){
		
		this.running=running;
		
	}
	
	//Si esta el hilo iniciado
	public void setWorking(boolean working){
		managerSurface.setWorking(working);
	}
	
	public boolean getWorking(){
		return managerSurface.getWorking();
	}
	
	public int getPoliticalsKilled(){
		return politicalsKilled;
	}
		
}
