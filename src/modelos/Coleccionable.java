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
public class Coleccionable {

    private int x;
    private int y;
    private int tipo; // 0: oro, 1: fruta
    private int subtipo; // Para los diferentes tipos de oro (1-10)
    private Image imagen;

    // Dimensiones (ajusta según tus imágenes)
    private static final int ANCHO = 30;
    private static final int ALTO = 30;

    public Coleccionable(int x, int y, int tipo, int subtipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.subtipo = subtipo;
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            if (tipo == 0) { // Oro
                // Usar el subtipo para los diferentes tipos de oro
                if (subtipo >= 1 && subtipo <= 10) {
                    imagen = ImageIO.read(getClass().getResource("/imagenes/coleccionables/oro" + subtipo + ".png"));
                } else {
                    imagen = ImageIO.read(getClass().getResource("/imagenes/coleccionables/oro1.png"));
                }
            } else { // Fruta
                imagen = ImageIO.read(getClass().getResource("/imagenes/coleccionables/fruta.png"));
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imagen de coleccionable: " + e.getMessage());
            imagen = null;
        }
    }

    public void mover(int velocidad) {
        x -= velocidad;
    }

    public int getX() {
        return x;
    }

    public int getTipo() {
        return tipo;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, ANCHO, ALTO);
    }

    public void dibujar(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, x, y, null);
        } else {
            dibujarRespaldo(g);
        }
    }

    private void dibujarRespaldo(Graphics g) {
        if (tipo == 0) { // Oro
            g.setColor(new Color(255, 215, 0));
            g.fillOval(x, y, ANCHO, ALTO);
        } else { // Fruta
            g.setColor(new Color(255, 0, 0));
            g.fillOval(x, y, ANCHO, ALTO);
            g.setColor(new Color(0, 100, 0));
            g.fillRect(x + ANCHO / 2 - 2, y - 5, 4, 8);
        }
    }
}
