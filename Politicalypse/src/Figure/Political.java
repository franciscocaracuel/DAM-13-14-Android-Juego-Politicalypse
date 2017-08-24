package Figure;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.fyfstyle.politicalypse.view.ViewGame;

public class Political {
	
	private Bitmap bmp;
	private int thisFrame=0;
	private static final int COLUMNS=4;
	private static final int ROWS=2;
	
	private ViewGame view;
	private int width, height;
	private int axisX, axisY;
	private int speedX, speedY;
	
	private int id;
	
	//El id es un valor identificativo para saber que tipo de objeto es
	public Political(ViewGame view, Bitmap bmp, int id, Context context){
		
		this.id=id;
		
		this.view=view;
		this.bmp = bmp;
		this.height = bmp.getHeight() / ROWS;
		this.width = bmp.getWidth() / COLUMNS;
		
		//Se usa el random para que la velocidad sea variable
		Random random = new Random();
		
		int start=random.nextInt(40);
		
		int speedInitX=random.nextInt(10);
		if(speedInitX==0){
			speedInitX=1;
		}
				
		//50% de probabilidad de salir por la izquierda o por la derecha
		if(start%2==0){
						
			//Aparecera justo por la parte izquierda de la pantalla. width es el ancho del muñeco
			axisX=0-width;
			
			//Solo quiero que aparezca a esa altura para que aparezca por las calles y no por lo edificios.
			//Aparecera por la parte inferior de la pantalla
			axisY=view.getHeight()-view.getHeight()/4;

			//La velocidad en horizontal
			speedX = speedInitX;
			
			//La velocidad hacia arriba o abajo
			speedY = start/2;
			
		} else{
			
			//Aparecera por la parte derecha de la pantalla
			axisX=view.getWidth();
			
			//Solo quiero que aparezca a esa altura para que aparezca por las calles y no por lo edificios.
			//Aparecera por la parte inferior de la pantalla
			axisY=view.getHeight()-view.getHeight()/4;

			//La velocidad ahora sera negativa para que vaya para la izquierda
			speedX = -speedInitX;
			
			//La velocidad hacia arriba o abajo
			speedY = start/-2;
			
		}

	}
	
	//Este constructor es para cuando se mueva con el dedo el muñeco	
	public Political(ViewGame view, Bitmap bmp, int x, int y, boolean moving, int id, Context context){
		
		this(view, bmp, id, context);
		
		if(x!=0 && y!=0){
			axisX=x-width/2;
			axisY=y-width/2;
		}
		
	}
	
	//Detecta cuando un objeto que se le pase colisiona con otro
	//No se usa en mi juego
	/*public boolean getCrash(Political p){
		
		Rect rectGeneral=new Rect(axisX, axisY, axisX+width, axisY+height);
		Rect rectPolitical=new Rect(p.axisX, p.axisY, p.width, p.height);
		return Rect.intersects(rectGeneral, rectPolitical);
		
	}*/
	
	private void moving(){
		
		//Para hacer el efecto del spriter se va cogiendo la parte del dibujo que interesa
		thisFrame=++thisFrame%COLUMNS;

		axisX=axisX+speedX;
		
		if(axisY+speedY<view.getHeight()/1.8 || axisY>view.getHeight()){
			speedY=-speedY;
		}
		
		axisY=axisY+speedY;
			
		//Si llega a la X de la puerta del congreso la velocidadX sera 0, se estaran quietos. Y la velocidad hacia arriba sera la misma que tenia
		//el desplazamiento horizontal. Asi llegaran arriba pero se parara
		if(axisX>view.getWidth()/2-(width*2) && axisX<view.getWidth()/2-(width)){			
			
			//Si la velocidad es positiva (significa que esta bajando), se le cambia el valor y empezaría a subir
			if(speedY>0){
				speedY=-speedY;
			}
			
			speedX=0;
			
			//Si llega arriba ha perdido
			if(axisY+speedY<=view.getHeight()/1.8){
				view.setLost(true);
			}
			
		}
		
		//Esto detecta si se ha sacado un politico de la pantalla. Si lo saca se le mandara al viewGame para que haga lo que tenga que hacer
		if(axisX<-1*view.getWidth()/4 || axisX>view.getWidth()+view.getWidth()/4){
			view.politicalKilled(id);						
		}
			
	}
	
	public void draw(Canvas canvas){
		
		moving();
		
		int originX = thisFrame * width;
		int originY = 0;
		
		//Si se mueve hacia la izquierda cogera la parte de abajo del spriter.png
		if (speedX < 0)
			originY = height;
		else
			originY = 0;
		
		Rect origin = new Rect(originX, originY, originX + width, originY+ height);
		Rect destiny = new Rect(axisX, axisY, axisX + width, axisY + height);
		
		canvas.drawBitmap(bmp, origin, destiny, null);
		
	}
	
	public void setSpeed(float x, float y){
		speedX=(int)x;
		speedY=(int)y;
	}
	
	public boolean touch(float x, float y){
		return x>axisX && x<axisX+width && y>axisY && y<axisY+height;
	}

}
