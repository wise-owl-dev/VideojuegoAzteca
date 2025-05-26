package modelos;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Flecha {
    private float x;
    private float y;
    private float velocidad;
    private boolean activa;
    private Image imagenFlecha;

    // Dimensiones de la flecha
    private static final int ANCHO = 35;
    private static final int ALTO = 10;

    // Velocidad de vuelo
    private static final float VELOCIDAD_BASE = 8.0f;

    public Flecha() {
        this.activa = false;
        this.velocidad = VELOCIDAD_BASE;
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            imagenFlecha = ImageIO.read(getClass().getResource("/imagenes/guerrero/flecha.png"));
        } catch (IOException e) {
            System.out.println("Error al cargar imagen de flecha: " + e.getMessage());
            imagenFlecha = null;
        }
    }

    public void disparar(int xInicial, int yInicial) {
        this.x = xInicial + 40; // Ajustar para que salga desde el arco
        this.y = yInicial - 30; // Ajustar altura
        this.activa = true;
    }

    public void mover() {
        if (activa) {
            x += velocidad;

            // Desactivar si sale de la pantalla
            if (x > 850) {
                activa = false;
            }
        }
    }

    public boolean estaActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y - ALTO / 2, ANCHO, ALTO);
    }

    public void dibujar(Graphics g) {
        if (activa) {
            if (imagenFlecha != null) {
                g.drawImage(imagenFlecha, (int) x, (int) y - ALTO / 2, ANCHO, ALTO, null);
            } else {
                // Dibujo de respaldo
                g.setColor(new java.awt.Color(139, 69, 19));
                g.fillRect((int) x, (int) y - ALTO / 2, ANCHO - 10, 3);
                // Punta de la flecha
                g.fillPolygon(
                        new int[] { (int) x + ANCHO - 10, (int) x + ANCHO - 10, (int) x + ANCHO },
                        new int[] { (int) y - 5, (int) y + 5, (int) y },
                        3);
                // Plumas
                g.setColor(new java.awt.Color(255, 255, 255));
                g.fillRect((int) x, (int) y - 6, 5, 3);
                g.fillRect((int) x, (int) y + 3, 5, 3);
            }
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
