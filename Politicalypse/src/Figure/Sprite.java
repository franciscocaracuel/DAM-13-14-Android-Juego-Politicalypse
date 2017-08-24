package Figure;

import java.util.Random;

import com.fyfstyle.politicalypse.view.ViewGame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {

	private Bitmap bmp; // imagen sprite
	private int frameActual = 0;
	private static final int COLUMNAS = 4;
	private static final int FILAS = 2;

	private ViewGame vista;
	private int ancho, alto;
	private int ejeX, ejeY;
	private int direccionX, direccionY;

	public Sprite(ViewGame vista, Bitmap bmp) {

		this.vista = vista;
		this.bmp = bmp;
		this.alto = bmp.getHeight() / FILAS;
		this.ancho = bmp.getWidth() / COLUMNAS;
		Random rnd = new Random();
		ejeX = rnd.nextInt(vista.getWidth() - this.ancho);
		ejeY = rnd.nextInt(vista.getHeight() - this.alto);
		direccionX = rnd.nextInt(10) - 5;
		if (direccionX == 0)
			direccionX = 1;
		direccionY = rnd.nextInt(10) - 5;
		if (direccionY == 0)
			direccionY = 1;

	}
	
	public Sprite(ViewGame vista, Bitmap bmp, int x, int y, boolean movimiento){
		this(vista, bmp);
		if(x!=0 && y!=0){
			ejeX=x-ancho/2;
			ejeY=y-ancho/2;
		}
	}
	
	public boolean getChoca(Sprite s){
		Rect rectangulo=new Rect(ejeX, ejeY, ejeX+ancho, ejeY+alto);
		Rect rectanguloFigura=new Rect(s.ejeX, s.ejeY, s.ancho, s.alto);
		return Rect.intersects(rectangulo, rectanguloFigura);
	}

	private void movimiento() {
		frameActual = ++frameActual % COLUMNAS;
		if (ejeX > vista.getWidth() - ancho - direccionX
				|| ejeX + direccionX < 0) {
			direccionX = -direccionX;
		}
		ejeX = ejeX + direccionX;
		if (ejeY > vista.getHeight() - alto - direccionY
				|| ejeY + direccionY < 0) {
			direccionY = -direccionY;
		}
		ejeY = ejeY + direccionY;
	}

	public void dibujar(Canvas canvas) {
		movimiento();
		int origenx = frameActual * ancho;
		int origeny = 0;
		if (direccionX < 0)
			origeny = alto;
		else
			origeny = 0;
		Rect origen = new Rect(origenx, origeny, origenx + ancho, origeny
				+ alto);
		Rect destino = new Rect(ejeX, ejeY, ejeX + ancho, ejeY + alto);
		canvas.drawBitmap(bmp, origen, destino, null);
	}

	public void setVelocidad(float x, float y) {
		direccionX = (int) x;
		direccionY = (int) y;
	}

	public boolean tocado(float x, float y) {
		return x > ejeX && x < ejeX + ancho && y > ejeY && y < ejeY + alto;
	}

}
