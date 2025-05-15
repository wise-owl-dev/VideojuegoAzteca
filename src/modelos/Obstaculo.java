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
public class Obstaculo {

    private int x;
    private int y;
    private int tipo;
    private int subtipo; // Para los diferentes tipos de obstáculos
    private Image imagen;

    // Dimensiones actualizadas de los obstáculos
    private int ancho;
    private int alto;

    // Tipos de obstáculos:
    // 0: Lanza
    // 1: Piedra
    // 2: Pinchos (con subtipos 1, 2, 3)
    // 3: Artefacto (62×59)
    // 4: Jaguar (30×42)
    // 5: Rocas (34×22)
    // 6: Ruinas (60×65)
    public Obstaculo(int x, int y, int tipo, int subtipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.subtipo = subtipo;

        // Definir dimensiones según el tipo
        switch (tipo) {
            case 0: // Lanza
                ancho = 14;
                alto = 74;
                break;
            case 1: // Piedra
                ancho = 50;
                alto = 50;
                break;
            case 2: // Pinchos
                ancho = 50;
                alto = 48;
                break;
            case 3: // Artefacto
                ancho = 62;
                alto = 59;
                break;
            case 4: // Jaguar
                ancho = 30;
                alto = 42;
                break;
            case 5: // Rocas
                ancho = 34;
                alto = 22;
                break;
            case 6: // Ruinas
                ancho = 60;
                alto = 65;
                break;
        }

        cargarImagen();
    }

    // Constructor sobrecargado para compatibilidad
    public Obstaculo(int x, int y, int tipo) {
        this(x, y, tipo, 0);
    }

    private void cargarImagen() {
        try {
            switch (tipo) {
                case 0: // Lanza
                    imagen = ImageIO.read(new File("src/imagenes/obstaculos/lanza.png"));
                    break;
                case 1: // Piedra
                    imagen = ImageIO.read(new File("src/imagenes/obstaculos/piedra.png"));
                    break;
                case 2: // Pinchos
                    // Usar el subtipo para los diferentes tipos de pinchos
                    if (subtipo >= 1 && subtipo <= 3) {
                        imagen = ImageIO.read(new File("src/imagenes/obstaculos/pinchos" + subtipo + ".png"));
                    } else {
                        imagen = ImageIO.read(new File("src/imagenes/obstaculos/pinchos1.png"));
                    }
                    break;
                case 3: // Artefacto
                    imagen = ImageIO.read(new File("src/imagenes/ambiente/artefacto.png"));
                    break;
                case 4: // Jaguar
                    // Si hay subtipos de jaguares (1-3)
                    imagen = ImageIO.read(new File("src/imagenes/ambiente/jaguar" + (subtipo > 0 ? subtipo : 1) + ".png"));
                    break;
                case 5: // Rocas
                    imagen = ImageIO.read(new File("src/imagenes/ambiente/roca" + (subtipo > 0 ? subtipo : 1) + ".png"));
                    break;
                case 6: // Ruinas
                    imagen = ImageIO.read(new File("src/imagenes/ambiente/ruina" + (subtipo > 0 ? subtipo : 1) + ".png"));
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imagen de obstáculo: " + e.getMessage());
            imagen = null;
        }
    }

    public void mover(int velocidad) {
        x -= velocidad;
    }

    public int getX() {
        return x;
    }

    public Rectangle getHitbox() {
        // Hacer hitbox ligeramente más pequeña que el sprite para colisiones más justas
        switch (tipo) {
            case 0: // Lanza
                return new Rectangle(x + 2, y - alto + 5, ancho - 4, alto - 10);
            case 1: // Piedra
                return new Rectangle(x + 5, y - alto + 5, ancho - 10, alto - 10);
            case 2: // Pinchos
                return new Rectangle(x + 5, y - alto + 5, ancho - 10, alto - 15);
            case 3: // Artefacto
                return new Rectangle(x + 10, y - alto + 10, ancho - 20, alto - 15);
            case 4: // Jaguar
                return new Rectangle(x + 5, y - alto + 5, ancho - 10, alto - 10);
            case 5: // Rocas
                return new Rectangle(x + 5, y - alto + 2, ancho - 10, alto - 4);
            case 6: // Ruinas
                return new Rectangle(x + 5, y - alto + 10, ancho - 10, alto - 15);
            default:
                return new Rectangle(x, y - alto, ancho, alto);
        }
    }

    public void dibujar(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, x, y - alto, null);

            // Para depuración - descomentar para ver hitboxes
            /*
            Rectangle hitbox = getHitbox();
            g.setColor(Color.RED);
            g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
             */
        } else {
            dibujarRespaldo(g);
        }
    }

    private void dibujarRespaldo(Graphics g) {
        switch (tipo) {
            case 0: // Lanza
                g.setColor(new Color(100, 70, 40));
                g.fillRect(x, y - alto, ancho, alto);
                break;
            case 1: // Piedra
                g.setColor(new Color(100, 100, 100));
                g.fillRoundRect(x, y - alto, ancho, alto, 10, 10);
                break;
            case 2: // Pinchos
                g.setColor(new Color(20, 120, 50));
                for (int i = 0; i < 3; i++) {
                    g.fillRect(x + i * 15 + 5, y - alto, 5, alto);
                }
                break;
            case 3: // Artefacto
                g.setColor(new Color(255, 215, 0));
                g.fillOval(x, y - alto, ancho, alto);
                break;
            case 4: // Jaguar
                g.setColor(new Color(180, 120, 40));
                g.fillRect(x, y - alto, ancho, alto);
                g.setColor(new Color(60, 40, 20));
                g.drawRect(x, y - alto, ancho, alto);
                break;
            case 5: // Rocas
                g.setColor(new Color(120, 120, 120));
                g.fillOval(x, y - alto, ancho, alto);
                break;
            case 6: // Ruinas
                g.setColor(new Color(180, 180, 180));
                g.fillRect(x, y - alto, ancho, alto);
                g.setColor(new Color(100, 100, 100));
                g.drawRect(x, y - alto, ancho, alto);
                break;
        }
    }
}
