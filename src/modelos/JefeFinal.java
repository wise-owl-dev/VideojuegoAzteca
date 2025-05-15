package modelos;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JefeFinal {

    private int x;
    private int y;
    private int vidas;
    private Image[] imagenes;
    private int frameActual;
    private long ultimoCambioFrame;
    private static final long DURACION_FRAME = 200; // ms entre frames

    // Dimensiones
    private int ancho = 250;
    private int alto = 200;

    // Movimiento
    private int velocidadX = 2;
    private int limiteIzquierdo = 400;
    private int limiteDerecho = 650;
    private boolean moviendoIzquierda = true;

    public JefeFinal(int x, int y) {
        this.x = x;
        this.y = y;
        this.vidas = 5; // Número de vidas del jefe
        this.frameActual = 0;
        this.ultimoCambioFrame = System.currentTimeMillis();
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Cargar las tres imágenes del jefe
            imagenes = new Image[3];
            for (int i = 0; i < 3; i++) {
                imagenes[i] = ImageIO.read(new File("src/imagenes/jefe/jefe" + (i + 1) + ".png"));
            }

            // Si la primera imagen se cargó correctamente, actualizar dimensiones
            if (imagenes[0] != null) {
                ancho = imagenes[0].getWidth(null);
                alto = imagenes[0].getHeight(null);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imágenes del jefe: " + e.getMessage());
            imagenes = null;
        }
    }

    public void mover() {
        // Movimiento horizontal
        if (moviendoIzquierda) {
            x -= velocidadX;
            if (x <= limiteIzquierdo) {
                moviendoIzquierda = false;
            }
        } else {
            x += velocidadX;
            if (x >= limiteDerecho) {
                moviendoIzquierda = true;
            }
        }

        // Actualizar animación
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoCambioFrame > DURACION_FRAME) {
            frameActual = (frameActual + 1) % 3; // 3 frames de animación
            ultimoCambioFrame = tiempoActual;
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 50, y + 50, ancho - 100, alto - 100);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void dibujar(Graphics g) {
        if (imagenes != null && frameActual < imagenes.length && imagenes[frameActual] != null) {
            g.drawImage(imagenes[frameActual], x, y, null);
        } else {
            // Dibujo de respaldo
            g.fillRect(x, y, ancho, alto);
        }
    }

    public boolean perdioVida() {
        vidas--;
        return vidas <= 0;
    }

    public int getVidas() {
        return vidas;
    }
}
