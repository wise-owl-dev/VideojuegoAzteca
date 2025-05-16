/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author wheezy
 */
public class Aguila {

    private float x;  // Usamos float para movimientos más suaves
    private float y;
    private Image[] frames;
    private int frameActual;
    private long ultimoCambioFrame;
    private static final long DURACION_FRAME = 100; // milisegundos entre frames

    // Variables para el movimiento ondulatorio
    private float amplitudY = 10; // Amplitud del movimiento vertical
    private float frecuenciaY = 0.02f; // Frecuencia del movimiento vertical
    private float anguloY = 0;  // Ángulo para calcular la posición vertical
    private float velocidadBase; // Velocidad base de vuelo

    // Dimensiones del águila
    private int ancho = 60;
    private int alto = 40;

    public Aguila(float x, float y) {
        this.x = x;
        this.y = y;
        this.frameActual = 0;
        this.ultimoCambioFrame = System.currentTimeMillis();
        this.anguloY = (float) (Math.random() * Math.PI * 2); // Ángulo inicial aleatorio
        this.velocidadBase = 2.0f + (float) (Math.random() * 1.5f); // Velocidad aleatoria
        this.amplitudY = 5.0f + (float) (Math.random() * 10.0f); // Amplitud aleatoria
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Cargar las tres imágenes del águila
            frames = new Image[3];
            for (int i = 0; i < 3; i++) {
                frames[i] = ImageIO.read(getClass().getResource("/imagenes/aguila/aguila" + (i + 1) + ".png"));
            }

            // Si la primera imagen se cargó correctamente, actualizar dimensiones
            if (frames[0] != null) {
                ancho = frames[0].getWidth(null);
                alto = frames[0].getHeight(null);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imágenes del águila: " + e.getMessage());
            frames = null;
        }
    }

    public void mover(int velocidadJuego) {
        // Movimiento horizontal - más rápido que la velocidad del juego para dar sensación de vuelo
        x -= velocidadBase + (velocidadJuego * 0.5f);

        // Movimiento vertical ondulatorio
        anguloY += frecuenciaY;
        y += Math.sin(anguloY) * 0.8f; // Pequeño movimiento vertical ondulatorio

        // Actualizar animación
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoCambioFrame > DURACION_FRAME) {
            frameActual = (frameActual + 1) % 3;
            ultimoCambioFrame = tiempoActual;
        }
    }

    public int getX() {
        return (int) x;
    }

    public void dibujar(Graphics g) {
        if (frames != null && frameActual < frames.length && frames[frameActual] != null) {
            g.drawImage(frames[frameActual], (int) x, (int) y, null);
        } else {
            // Dibujo de respaldo
            g.setColor(new Color(150, 100, 50));
            g.fillOval((int) x, (int) y, ancho, alto / 2);
            g.setColor(new Color(100, 100, 100));
            g.drawLine((int) x, (int) y + alto / 4, (int) x + ancho, (int) y + alto / 4);
            g.drawLine((int) x + ancho / 2, (int) y, (int) x + ancho / 2, (int) y + alto / 2);
        }
    }
}
