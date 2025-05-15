package modelos;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EfectoFuego {

    private int x;
    private int y;
    private Image[] frames;
    private int frameActual;
    private long ultimoCambioFrame;
    private long tiempoInicio;
    private boolean activo;
    private static final long DURACION_FRAME = 80; // ms entre frames
    private static final long DURACION_TOTAL = 800; // duración total del efecto en ms

    public EfectoFuego() {
        this.activo = false;
        this.frameActual = 0;
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Cargar los 8 frames del fuego
            frames = new Image[8];
            for (int i = 0; i < 8; i++) {
                frames[i] = ImageIO.read(new File("src/imagenes/efectos/fuego" + (i + 1) + ".png"));
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imágenes del fuego: " + e.getMessage());
            frames = null;
        }
    }

    public void iniciar(int x, int y) {
        this.x = x;
        this.y = y;
        this.frameActual = 0;
        this.ultimoCambioFrame = System.currentTimeMillis();
        this.tiempoInicio = System.currentTimeMillis();
        this.activo = true;
    }

    public void actualizar() {
        if (!activo)
            return;

        // Verificar si el efecto ha terminado
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - tiempoInicio > DURACION_TOTAL) {
            activo = false;
            return;
        }

        // Actualizar animación
        if (tiempoActual - ultimoCambioFrame > DURACION_FRAME) {
            frameActual = (frameActual + 1) % 8;
            ultimoCambioFrame = tiempoActual;
        }
    }

    public void dibujar(Graphics g) {
        if (!activo || frames == null)
            return;

        if (frameActual < frames.length && frames[frameActual] != null) {
            // Dibujar el fuego en la posición indicada
            g.drawImage(frames[frameActual], x, y, null);
        }
    }

    public boolean estaActivo() {
        return activo;
    }

    public void desactivar() {
        activo = false;
    }
}
