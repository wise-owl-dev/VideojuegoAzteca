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
public class ElementoAmbiente {

    private int x;
    private int y;
    private int tipo; // 0-1: árboles (los únicos elementos ambientales ahora)
    private int suptipo; // 2: rocas
    private Image imagen;
    private int alto;
    private int ancho;

    public ElementoAmbiente(int x, int y, int tipo, int suptipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.suptipo = suptipo;

        // Por defecto, dimensiones para árboles Y rocas
        if (tipo == 2 || tipo == 3) { // Rocas
            ancho = 34;
            alto = 22;
        } else {
            ancho = 70;
            alto = 130;
        }
        // Si el tipo es 0 o 1, se cargará la imagen del árbol correspondiente

        cargarImagen();
    }

    public ElementoAmbiente(int x, int y, int tipo) {
        this(x, y, tipo, 0);
    }

    private void cargarImagen() {
        try {
            if (tipo == 0 || tipo == 1) { // Árboles
                imagen = ImageIO.read(new File("src/imagenes/ambiente/arbol" + (tipo + 1) + ".png"));
            } else if (tipo == 2 || tipo == 3) { // Rocas
                imagen = ImageIO.read(new File("src/imagenes/ambiente/roca" + tipo + ".png"));
            }

            // Si la imagen se cargó correctamente, actualiza las dimensiones
            if (imagen != null) {
                ancho = imagen.getWidth(null);
                alto = imagen.getHeight(null);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imagen de ambiente: " + e.getMessage());
            imagen = null;
        }
    }

    public void mover(int velocidad) {
        x -= velocidad;
    }

    public int getX() {
        return x;
    }

    public void dibujar(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, x, y - alto, null);
        } else {
            // Dibujo de respaldo (solo árboles)
            g.setColor(new Color(139, 69, 19)); // Tronco
            g.fillRect(x + ancho / 2 - 5, y - 70, 10, 70);
            g.setColor(new Color(34, 139, 34)); // Copa
            g.fillOval(x, y - 100, 60, 50);
        }
    }
}
