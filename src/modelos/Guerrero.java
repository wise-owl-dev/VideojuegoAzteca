/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author wheezy
 */
public class Guerrero {

    private int x;
    private int y;
    private int yInicial;
    private int velocidadY;
    private final int GRAVEDAD = 1;
    private boolean saltando;
    private Image[] framesCaminar;
    private Image[] framesSalto;
    private int frameActual;
    private long ultimoCambioFrame;
    private static final long DURACION_FRAME = 60;

    // Dimensiones reales del sprite del guerrero
    private static final int ANCHO_SPRITE = 51;
    private static final int ALTO_SPRITE = 59;

    private Image[] framesHerido; // Imágenes para la animación de herido
    private boolean herido; // Indica si el guerrero está herido
    private int frameHeridoActual; // Frame actual de la animación de herido
    private long ultimoCambioFrameHerido; // Momento del último cambio de frame
    private static final long DURACION_FRAME_HERIDO = 150; // Duración de cada frame (más lento que caminar)
    private boolean animacionHeridoCompleta;// Indica si la animación de herido ha terminado

    public Guerrero(int x, int y) {
        this.x = x;
        this.y = y;
        this.yInicial = y;
        this.velocidadY = 0;
        this.saltando = false;
        this.frameActual = 0;
        this.ultimoCambioFrame = System.currentTimeMillis();

        // Inicializar variables de animación de herido
        this.herido = false;
        this.frameHeridoActual = 0;
        this.ultimoCambioFrameHerido = 0;
        this.animacionHeridoCompleta = false;

        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Cargar imágenes de caminar (9 frames)
            framesCaminar = new Image[9];
            for (int i = 0; i < 9; i++) {
                framesCaminar[i] = ImageIO.read(getClass().getResource("/imagenes/guerrero/guerrero" + (i + 1) + ".png"));
            }

            // Cargar imágenes de salto o usar frames de caminar si no hay
            framesSalto = new Image[2];

            // Usar frames de caminar para el salto
            framesSalto[0] = framesCaminar[0];
            framesSalto[1] = framesCaminar[1];

            // Cargar imágenes de herido (6 frames)
            framesHerido = new Image[6];
            for (int i = 0; i < 6; i++) {
                framesHerido[i] = ImageIO.read(getClass().getResource("/imagenes/guerrero/herido" + (i + 1) + ".png"));
            }

        } catch (IOException e) {
            System.out.println("Error al cargar imágenes del guerrero: " + e.getMessage());
            framesCaminar = null;
            framesSalto = null;
            framesHerido = null;
        }
    }

    public void iniciarAnimacionHerido() {
        if (!herido) {
            herido = true;
            frameHeridoActual = 0;
            ultimoCambioFrameHerido = System.currentTimeMillis();
            animacionHeridoCompleta = false;
            System.out.println("Animación de herido iniciada");
        }
    }

    public void saltar() {
        if (!saltando) {
            velocidadY = -15;
            saltando = true;
        }
    }

    public void mover() {
        if (herido) {
            // Actualizar animación de herido
            long tiempoActual = System.currentTimeMillis();
            if (tiempoActual - ultimoCambioFrameHerido > DURACION_FRAME_HERIDO) {
                frameHeridoActual++;

                if (frameHeridoActual >= framesHerido.length) {
                    // Animación de herido completa
                    animacionHeridoCompleta = true;
                    // No reiniciamos frameHeridoActual para mantener el último frame
                    frameHeridoActual = framesHerido.length - 1;
                }

                ultimoCambioFrameHerido = tiempoActual;
            }

            return; // No procesar movimiento normal si está herido
        }

        if (saltando) {
            y += velocidadY;
            velocidadY += GRAVEDAD;

            if (y >= yInicial) {
                y = yInicial;
                velocidadY = 0;
                saltando = false;
            }
        }

        // Actualizar animación
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoCambioFrame > DURACION_FRAME) {
            if (!saltando) {
                frameActual = (frameActual + 1) % 9;
            } else {
                frameActual = (frameActual + 1) % 2;
            }
            ultimoCambioFrame = tiempoActual;
        }
    }

    public boolean estaSaltando() {
        return saltando;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 10, y - ALTO_SPRITE + 10, ANCHO_SPRITE - 20, ALTO_SPRITE - 15);
    }

    public void dibujar(Graphics g) {
        if (herido && framesHerido != null && frameHeridoActual < framesHerido.length) {
            // Dibujar frame de animación de herido
            // MODIFICACIÓN: Ajustar la posición Y para que el guerrero herido esté alineado
            // con el suelo
            // Añadir un desplazamiento vertical progresivo conforme avanza la animación
            int ajusteY = 0;

            // La animación cae progresivamente al suelo
            if (frameHeridoActual >= 1) {
                // Calculamos un ajuste que aumenta con cada frame
                // para que el guerrero "caiga" al suelo gradualmente
                ajusteY = Math.min(frameHeridoActual * 5, 20); // Máximo 20 píxeles más abajo
            }

            // Dibujar con el ajuste vertical
            g.drawImage(framesHerido[frameHeridoActual], x, y - ALTO_SPRITE + ajusteY, null);

        } else if (!saltando && framesCaminar != null && frameActual < framesCaminar.length) {
            g.drawImage(framesCaminar[frameActual], x, y - ALTO_SPRITE, null);
        } else if (saltando && framesSalto != null) {
            g.drawImage(framesSalto[frameActual % framesSalto.length], x, y - ALTO_SPRITE, null);
        } else {
            dibujarRespaldo(g);
        }

        // Depuración: mostrar hitbox
        /*
         * Rectangle hitbox = getHitbox();
         * g.setColor(Color.RED);
         * g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
         */
    }

    private void dibujarRespaldo(Graphics g) {
        g.setColor(new Color(150, 75, 0));
        g.fillRect(x, y - 40, 30, 40);
        g.fillOval(x - 2, y - 60, 34, 25);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean esAnimacionHeridoCompleta() {
        return herido && animacionHeridoCompleta;
    }

    public boolean estaHerido() {
        return herido;
    }
}
