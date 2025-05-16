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
    private int vidasTotales; // Número total de vidas/preguntas necesarias
    private int vidasRestantes; // Vidas/preguntas restantes
    private int fase; // Fase actual del jefe (1, 2, 3...)
    private Image[] imagenes; // Imágenes de animación del jefe
    private Image[] barraVida; // Imágenes para la barra de vida
    private int frameActual;
    private long ultimoCambioFrame;
    private static final long DURACION_FRAME = 200; // ms entre frames
    private boolean derrotadoTemporalmente; // Indica si el jefe ha sido derrotado en esta fase

    // Dimensiones
    private int ancho = 250;
    private int alto = 200;

    // Movimiento
    private int velocidadX = 2;
    private int limiteIzquierdo = 400;
    private int limiteDerecho = 650;
    private boolean moviendoIzquierda = true;

    public JefeFinal(int x, int y, int fase) {
        this.x = x;
        this.y = y;
        this.fase = fase;

        // Configurar vidas según la fase
        if (fase == 1) {
            this.vidasTotales = 3;
        } else if (fase == 2) {
            this.vidasTotales = 5;
        } else if (fase == 3) {
            this.vidasTotales = 13; // Jefe final
        } else {
            // Para fases futuras si se añaden
            this.vidasTotales = 3 + (fase - 1) * 2;
        }

        this.vidasRestantes = this.vidasTotales;
        this.frameActual = 0;
        this.ultimoCambioFrame = System.currentTimeMillis();
        this.derrotadoTemporalmente = false;
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Cargar las tres imágenes del jefe
            imagenes = new Image[3];
            for (int i = 0; i < 3; i++) {
                imagenes[i] = ImageIO.read(getClass().getResource("/imagenes/jefe/jefe" + (i + 1) + ".png"));
            }

            // Cargar imágenes de la barra de vida (13 sprites)
            barraVida = new Image[13];
            for (int i = 0; i < 13; i++) {
                barraVida[i] = ImageIO.read(getClass().getResource("/imagenes/jefe/barra" + (i + 1) + ".png"));
            }

            // Si la primera imagen se cargó correctamente, actualizar dimensiones
            if (imagenes[0] != null) {
                ancho = imagenes[0].getWidth(null);
                alto = imagenes[0].getHeight(null);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imágenes del jefe: " + e.getMessage());
            imagenes = null;
            barraVida = null;
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

        // Dibujar barra de vida
        dibujarBarraVida(g);
    }

    private void dibujarBarraVida(Graphics g) {
        // Calcular qué sprite de barra de vida mostrar según las vidas restantes
        if (barraVida != null) {
            // Calcular el índice apropiado de la barra de vida
            int indiceBarraVida = calcularIndiceBarraVida();

            // Dibujar la barra de vida en la parte superior
            if (indiceBarraVida >= 0 && indiceBarraVida < barraVida.length) {
                int xBarra = x + (ancho - barraVida[indiceBarraVida].getWidth(null)) / 2;
                int yBarra = y - 20; // Justo encima del jefe
                g.drawImage(barraVida[indiceBarraVida], xBarra, yBarra, null);
            }
        }
    }

    private int calcularIndiceBarraVida() {
        // Calcular qué sprite usar basado en el porcentaje de vida restante
        double porcentajeVida = (double) vidasRestantes / vidasTotales;

        // Mapear el porcentaje a un índice del 0 al 12
        // Usamos 12 en lugar de 13 porque los índices van de 0 a 12
        int indice = (int) (porcentajeVida * 12);

        // Asegurar que el índice esté dentro del rango válido
        return Math.min(Math.max(indice, 0), 12);
    }

    public boolean perdioVida() {
        vidasRestantes--;

        // Verificar si el jefe ha sido derrotado en esta fase
        if (vidasRestantes <= 0) {
            derrotadoTemporalmente = true;
            return true;
        }
        return false;
    }

    public int getVidasRestantes() {
        return vidasRestantes;
    }

    public int getVidasTotales() {
        return vidasTotales;
    }

    public int getFase() {
        return fase;
    }

    public boolean estaDerrotadoTemporalmente() {
        return derrotadoTemporalmente;
    }

    // Para la última fase (fase 3), verificar si es el jefe final
    public boolean esJefeFinal() {
        return fase >= 3;
    }
}