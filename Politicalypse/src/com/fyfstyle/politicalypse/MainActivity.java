package com.fyfstyle.politicalypse;

import com.fyfstyle.politicalypse.view.ViewGame;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private LinearLayout llButtons, llSurface, llYouLost, llPlayPause;
	private TextView tvPunctuation, tvWave;
	private boolean pause=true;
	private Button btPlayPause, btInfo;
	
	private MediaPlayer mp;
	
	private ViewGame viewGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Pone la pantalla completa
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        		
		setContentView(R.layout.activity_main);
		
		inicio();
		
	}
	
	//Cada vez que se ejecute el onStart se iniciara la cancion.
	@Override
	protected void onStart() {
		
		mp=MediaPlayer.create(this, R.raw.pp);
		mp.start();
		
		mp.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.start();
			}
		});
		
		if(viewGame!=null){
			iniciarJuego();
		}
				
		super.onStart();
	}
	
	//Cada vez que se pare el activity se para la cancion
	@Override
	protected void onStop() {
		
		mp.stop();
		mp.release();
		mp=null;
		
		llSurface.removeAllViews();
		
		super.onPause();
	}

	private void inicio(){
		
		//Inicializacion de todas las variables graficas
		llButtons=(LinearLayout)findViewById(R.id.llButtons);
		llSurface=(LinearLayout)findViewById(R.id.llSurface);
		
		tvPunctuation=(TextView)findViewById(R.id.tvPunctuation);
		tvPunctuation.setText("0");
		tvPunctuation.setVisibility(View.INVISIBLE);
		
		tvWave=(TextView)findViewById(R.id.tvWave);
		tvWave.setText("1");
		tvWave.setVisibility(View.INVISIBLE);
		
		llYouLost=(LinearLayout)findViewById(R.id.llYouLost);
		llYouLost.setVisibility(View.INVISIBLE);
		
		llPlayPause=(LinearLayout)findViewById(R.id.llPlayPause);
		llPlayPause.setVisibility(View.INVISIBLE);
		
		btPlayPause=(Button)findViewById(R.id.btPlayPause);
		btPlayPause.setVisibility(View.INVISIBLE);
		
		btInfo=(Button)findViewById(R.id.btInfo);
				
	}
	
	public void play(View v){
		
		//Comenzara a hacer las animacion y al final inicia el juego
		lanzarAnimacionBotones();
		
	}
	
	public void exit(View v){
			this.finish();
	}
	
	public void retry(View v){
		
		//Lanza todas las animaciones y despues vuelve a iniciar el juego
		Animation moveLost = AnimationUtils.loadAnimation(this, R.anim.transladar_derecha_izquierda_lost);
		moveLost.reset();
	
		llYouLost.startAnimation(moveLost);
		
		moveLost.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				llYouLost.setVisibility(View.INVISIBLE);
				
			}
		});
		
		
		Animation moveWave = AnimationUtils.loadAnimation(this, R.anim.transladar_abajo_arriba_wave);
		moveWave.reset();
	
		tvWave.startAnimation(moveWave);		
		
		moveWave.setAnimationListener(new AnimationListener() {
			
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
				
				tvWave.setVisibility(View.INVISIBLE);
				
				Animation movePunctuation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.transladar_derecha_izquierda_punctuation);
				movePunctuation.reset();
			
				tvPunctuation.startAnimation(movePunctuation);
				
				movePunctuation.setAnimationListener(new AnimationListener() {
					
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
						
						iniciarJuego();
						
					}
				});
				
			}
		});
		
	}
	
	public void playPause(View v){
		
		//Si esta iniciado se para o al reves
		pause=!pause;
		
		//Si se esta jugando se pone el linearLayout mas opaco y el icono de play
		if(!pause){
						
			llPlayPause.setVisibility(View.VISIBLE);
			btPlayPause.setBackgroundResource(android.R.drawable.ic_media_play);
			
		//Si no vuelve a restablecer la pantalla normal
		} else{
			
			llPlayPause.setVisibility(View.INVISIBLE);
			btPlayPause.setBackgroundResource(android.R.drawable.ic_media_pause);
			
		}
		
		//Si el juego se ha iniciado alguna vez se le dice como tiene que estar, si ejecutandose o parado
		if(viewGame!=null)
			viewGame.setRunning(pause);
		
	}
	
	public void info(View v){

		//Muestra un dialog con el layout que he creado
		final Dialog d=new Dialog(this);
		d.setContentView(R.layout.layout_info);
		d.setTitle(getResources().getString(R.string.dialog_info_title));
		d.setCanceledOnTouchOutside(true);
		
		d.show();
		
	}
	
	//Comienzan todas las animaciones. Al final ejecuta el metodo iniciaJuego()
	public void lanzarAnimacionBotones() {

		Animation moverInfo = AnimationUtils.loadAnimation(this,R.anim.transladar_arriba_abajo_info);
		moverInfo.reset();

		btInfo.startAnimation(moverInfo);

		moverInfo.setAnimationListener(new AnimationListener() {

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
				btInfo.setVisibility(View.INVISIBLE);
			}
		});

			Animation moveButtons = AnimationUtils.loadAnimation(this, R.anim.transladar_izquierda_derecha_botones);
			moveButtons.reset();
	
			llButtons.startAnimation(moveButtons);		
			
			moveButtons.setAnimationListener(new AnimationListener() {
				
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
					
					Animation moverLLBotones = AnimationUtils.loadAnimation(MainActivity.this, R.anim.transladar_derecha_izquierda_botones);
					moverLLBotones.reset();
			
					llButtons.startAnimation(moverLLBotones);
					
					moverLLBotones.setAnimationListener(new AnimationListener() {
						
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

							llButtons.setVisibility(View.INVISIBLE);
							
							iniciarJuego();

						}
					});
					
				}
			});
		
	}

	private void iniciarJuego(){
		
		//Se inicializa la clase que contiene la superficie del juego y toda su funcionalidad
		viewGame=new ViewGame(this, this.findViewById(android.R.id.content).getRootView());
		
		//Para poder trabajar con todos los elementos graficos se usa un relative layout.
		//En ese relative layout iran todos los elementos y entre ellos un linearlayout que sera donde se metera la surface. 
		//En el estarán todos los muñecos
		llSurface.addView(viewGame);		

		//Se inicializan los contadores en los elementos graficos.
		tvPunctuation.setText("0");
		tvWave.setText("1");
		llPlayPause.setVisibility(View.INVISIBLE);
		
		//Comienzan las animaciones para quitar o poner los elementos que forman la pantalla
		//Pone primero la puntuacion		
		Animation moverPunctuation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.transladar_izquierda_derecha_punctuation);
		moverPunctuation.reset();

		tvPunctuation.setVisibility(View.VISIBLE);
		tvPunctuation.startAnimation(moverPunctuation);
		
		moverPunctuation.setAnimationListener(new AnimationListener() {
			
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
				
				//Cuando termina de poner la puntuacion se pone el tablon con las oleadas. Asi no se pisan
				Animation moverWave = AnimationUtils.loadAnimation(MainActivity.this, R.anim.transladar_arriba_abajo_wave);
				moverWave.reset();

				tvWave.setVisibility(View.VISIBLE);
				tvWave.startAnimation(moverWave);
				
				btPlayPause.setVisibility(View.VISIBLE);
				btPlayPause.startAnimation(moverWave);
				
			}
		});		
				
	}
	
	//Detecta cada vez que se pulsa el boton atras
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//Si se pulsa el boton atras lanzara un dialogo. Antes se para el juego para que no sigan los muñecos
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	
	    	if(viewGame!=null){
	    		viewGame.setRunning(false);
	    	}	    	
	        
	    	// Se crea un dialogo para confirmar la salida del juego
			AlertDialog.Builder alertDialog= new AlertDialog.Builder(MainActivity.this);
			alertDialog.setCancelable(false);
			alertDialog.setTitle(R.string.dialog_salir_title);
			
			alertDialog.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					dialog.cancel();
					
					//Si el juego ha sido inicializado lo pondra en pause o en play. Segun como estuviese
					if(viewGame!=null){
						if(pause){
							llPlayPause.setVisibility(View.INVISIBLE);
						}else{
							llPlayPause.setVisibility(View.VISIBLE);
						}
						viewGame.setRunning(pause);
					}
					
				}
			});
			
			//Este boton es "cancelar" del dialogo de confirmacion
			alertDialog.setPositiveButton(R.string.si,	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					MainActivity.this.finish();
					
				}
			});
			
			alertDialog.show();
	    	
	    	
	        return true;
	        
	    }
	    
	    return super.onKeyDown(keyCode, event);
	    
	}

}
