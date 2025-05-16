package videojuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PantallaFinal extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private static final int ANCHO = 800;
    private static final int ALTO = 300;

    // Componentes gráficos
    private Image fondoFinal;
    private JFrame parentFrame;

    // Animación del título
    private float angulo = 0;
    private Timer timerAnimacion;

    // Opciones del menú
    private Rectangle botonJugarDeNuevo;
    private Rectangle botonSalir;

    private int botonSeleccionado = -1;

    // Fuentes
    private Font fuenteTitulo;
    private Font fuenteMenu;
    private Font fuenteTexto;

    // Datos del juego
    private int puntuacionFinal;
    private int oroRecogido;
    private int frutasRecogidas;
    private boolean victoria;

    // Efectos visuales
    private Color[] coloresEstrellas;
    private Point[] posicionesEstrellas;
    private int[] tamañosEstrellas;
    private float[] velocidadesRotacion;

    public PantallaFinal(JFrame parentFrame, boolean victoria, int puntuacion, int oro, int frutas) {
        this.parentFrame = parentFrame;
        this.victoria = victoria;
        this.puntuacionFinal = puntuacion;
        this.oroRecogido = oro;
        this.frutasRecogidas = frutas;

        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);

        // Crear áreas para los botones
        botonJugarDeNuevo = new Rectangle(ANCHO / 2 - 150, 220, 300, 40);
        botonSalir = new Rectangle(ANCHO / 2 - 100, 270, 200, 30);

        // Cargar recursos
        cargarRecursos();

        // Inicializar efectos visuales
        inicializarEfectos();

        // Añadir listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        // Iniciar animación
        timerAnimacion = new Timer(16, this);
        timerAnimacion.start();
    }

    private void inicializarEfectos() {
        // Crear estrellas o destellos para la pantalla de victoria
        int numEstrellas = victoria ? 50 : 20;
        coloresEstrellas = new Color[numEstrellas];
        posicionesEstrellas = new Point[numEstrellas];
        tamañosEstrellas = new int[numEstrellas];
        velocidadesRotacion = new float[numEstrellas];

        for (int i = 0; i < numEstrellas; i++) {
            if (victoria) {
                // Colores dorados y brillantes para victoria
                coloresEstrellas[i] = new Color(
                        255,
                        215 - (int) (Math.random() * 30),
                        (int) (Math.random() * 50));
            } else {
                // Colores más apagados para derrota
                coloresEstrellas[i] = new Color(
                        150 + (int) (Math.random() * 50),
                        50 + (int) (Math.random() * 50),
                        50 + (int) (Math.random() * 50));
            }

            posicionesEstrellas[i] = new Point(
                    (int) (Math.random() * ANCHO),
                    (int) (Math.random() * ALTO));

            tamañosEstrellas[i] = 2 + (int) (Math.random() * 5);
            velocidadesRotacion[i] = 0.01f + (float) (Math.random() * 0.03f);
        }
    }

    private void cargarRecursos() {
        try {
            fondoFinal = ImageIO.read(new File("src/imagenes/fondos/fondo_inicio.png"));

            // Cargar fuentes
            try {
                fuenteTitulo = Font.createFont(Font.TRUETYPE_FONT, new File("src/fuentes/azteca.ttf")).deriveFont(40f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(fuenteTitulo);
            } catch (FontFormatException | IOException e) {
                fuenteTitulo = new Font("Serif", Font.BOLD, 50);
            }

            fuenteMenu = fuenteTitulo.deriveFont(24f);
            fuenteTexto = fuenteTitulo.deriveFont(18f);

        } catch (IOException e) {
            System.out.println("Error al cargar recursos: " + e.getMessage());
            // Fondos y fuentes de respaldo
            fuenteTitulo = new Font("Serif", Font.BOLD, 40);
            fuenteMenu = new Font("Serif", Font.BOLD, 24);
            fuenteTexto = new Font("Serif", Font.PLAIN, 18);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Activar antialiasing para mejor calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar fondo
        if (fondoFinal != null) {
            g.drawImage(fondoFinal, 0, 0, ANCHO, ALTO, null);
        } else {
            // Fondo de respaldo con degradado
            GradientPaint gradiente;
            if (victoria) {
                gradiente = new GradientPaint(0, 0, new Color(100, 80, 20),
                        0, ALTO, new Color(60, 40, 10));
            } else {
                gradiente = new GradientPaint(0, 0, new Color(80, 20, 20),
                        0, ALTO, new Color(40, 10, 10));
            }
            g2d.setPaint(gradiente);
            g2d.fillRect(0, 0, ANCHO, ALTO);
        }

        // Dibujar efectos de estrellas/destellos
        for (int i = 0; i < posicionesEstrellas.length; i++) {
            g2d.setColor(coloresEstrellas[i]);
            dibujarEstrella(g2d, posicionesEstrellas[i].x, posicionesEstrellas[i].y,
                    tamañosEstrellas[i], angulo * velocidadesRotacion[i]);
        }

        // Dibujar título con efecto de rotación suave
        dibujarTitulo(g2d);

        // Dibujar panel para mostrar estadísticas
        dibujarPanelEstadisticas(g2d);

        // Dibujar menú con botones
        dibujarMenu(g2d);
    }

    private void dibujarEstrella(Graphics2D g2d, int x, int y, int tamaño, float rotacion) {
        AffineTransform transformOriginal = g2d.getTransform();

        g2d.translate(x, y);
        g2d.rotate(rotacion);

        for (int i = 0; i < 8; i++) {
            double angulo = Math.PI * 2 * i / 8;
            int xPunta = (int) (Math.cos(angulo) * tamaño);
            int yPunta = (int) (Math.sin(angulo) * tamaño);
            g2d.drawLine(0, 0, xPunta, yPunta);
        }

        g2d.setTransform(transformOriginal);
    }

    private void dibujarTitulo(Graphics2D g2d) {
        g2d.setFont(fuenteTitulo);

        // Guardar transformación actual
        AffineTransform transformOriginal = g2d.getTransform();

        // Aplicar efecto de oscilación suave
        g2d.translate(ANCHO / 2, 70);
        g2d.rotate(Math.sin(angulo) * 0.03);
        g2d.scale(1.0 + Math.sin(angulo) * 0.05, 1.0 + Math.sin(angulo) * 0.05);

        // Texto con sombra
        FontMetrics fm = g2d.getFontMetrics();
        String titulo = victoria ? "¡VICTORIA!" : "DERROTA";
        int anchoTitulo = fm.stringWidth(titulo);

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(titulo, -anchoTitulo / 2 + 3, 3); // Sombra

        // Degradado para el título
        GradientPaint gradienteTitulo;
        if (victoria) {
            gradienteTitulo = new GradientPaint(
                    -anchoTitulo / 2, -40, new Color(255, 215, 0),
                    anchoTitulo / 2, 40, new Color(210, 105, 30));
        } else {
            gradienteTitulo = new GradientPaint(
                    -anchoTitulo / 2, -40, new Color(220, 50, 50),
                    anchoTitulo / 2, 40, new Color(150, 30, 30));
        }
        g2d.setPaint(gradienteTitulo);
        g2d.drawString(titulo, -anchoTitulo / 2, 0);

        // Restaurar transformación
        g2d.setTransform(transformOriginal);
    }

    private void dibujarPanelEstadisticas(Graphics2D g2d) {
        // Panel semitransparente para estadísticas
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(ANCHO / 2 - 200, 90, 400, 120, 20, 20);

        // Borde del panel
        if (victoria) {
            g2d.setColor(new Color(255, 215, 0));
        } else {
            g2d.setColor(new Color(200, 80, 80));
        }
        g2d.drawRoundRect(ANCHO / 2 - 200, 90, 400, 120, 20, 20);

        // Título del panel
        g2d.setFont(fuenteMenu);
        g2d.setColor(Color.WHITE);
        String estadisticasTitulo = "RESUMEN DE AVENTURA";
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTitulo = fm.stringWidth(estadisticasTitulo) - 20;
        g2d.drawString(estadisticasTitulo, ANCHO / 2 - anchoTitulo / 2, 120);

        // Datos de estadísticas
        g2d.setFont(fuenteTexto);
        String[] estadisticas = {
                "Puntuación: " + puntuacionFinal,
                "Oro recogido: " + oroRecogido,
                "Frutas recogidas: " + frutasRecogidas
        };

        int y = 150;
        for (String estadistica : estadisticas) {
            g2d.drawString(estadistica, ANCHO / 2 - 150, y);
            y += 25;
        }
    }

    private void dibujarMenu(Graphics2D g2d) {
        // Dibujar cada botón
        dibujarBoton(g2d, botonJugarDeNuevo, "Jugar de Nuevo", 0);
        dibujarBoton(g2d, botonSalir, "Salir", 1);
    }

    private void dibujarBoton(Graphics2D g2d, Rectangle boton, String texto, int indice) {
        // Determinar si el botón está seleccionado
        boolean seleccionado = (indice == botonSeleccionado);

        // Dibujar fondo del botón
        GradientPaint gradienteBoton;
        if (seleccionado) {
            // Botón seleccionado con brillo
            if (victoria) {
                gradienteBoton = new GradientPaint(
                        boton.x, boton.y, new Color(255, 215, 0),
                        boton.x, boton.y + boton.height, new Color(210, 105, 30));
            } else {
                gradienteBoton = new GradientPaint(
                        boton.x, boton.y, new Color(220, 80, 80),
                        boton.x, boton.y + boton.height, new Color(150, 40, 40));
            }
        } else {
            // Botón normal
            if (victoria) {
                gradienteBoton = new GradientPaint(
                        boton.x, boton.y, new Color(150, 100, 50),
                        boton.x, boton.y + boton.height, new Color(100, 60, 30));
            } else {
                gradienteBoton = new GradientPaint(
                        boton.x, boton.y, new Color(120, 60, 60),
                        boton.x, boton.y + boton.height, new Color(80, 40, 40));
            }
        }

        g2d.setPaint(gradienteBoton);
        g2d.fillRoundRect(boton.x, boton.y, boton.width, boton.height, 20, 20);

        // Borde del botón
        if (seleccionado) {
            g2d.setColor(new Color(255, 255, 200));
            g2d.setStroke(new BasicStroke(3f));
        } else {
            g2d.setColor(new Color(60, 30, 15));
            g2d.setStroke(new BasicStroke(1.5f));
        }
        g2d.drawRoundRect(boton.x, boton.y, boton.width, boton.height, 20, 20);

        // Texto del botón
        g2d.setFont(fuenteMenu);
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTexto = fm.stringWidth(texto);

        if (seleccionado) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(new Color(220, 220, 220));
        }

        g2d.drawString(texto, boton.x + (boton.width - anchoTexto) / 2,
                boton.y + ((boton.height - fm.getHeight()) / 2) + fm.getAscent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Animar el título y efectos
        angulo += 0.05;

        // Mover algunas estrellas
        for (int i = 0; i < posicionesEstrellas.length; i++) {
            if (Math.random() < 0.02) { // 2% de probabilidad de mover cada estrella
                posicionesEstrellas[i].y += (Math.random() < 0.5) ? -1 : 1;
                posicionesEstrellas[i].x += (Math.random() < 0.5) ? -1 : 1;
            }
        }

        repaint();
    }

    // Métodos de gestión de mouse
    @Override
    public void mouseClicked(MouseEvent e) {
        Point punto = e.getPoint();

        // Comprobar si hemos hecho clic en algún botón
        if (botonJugarDeNuevo.contains(punto)) {
            iniciarJuego();
        } else if (botonSalir.contains(punto)) {
            System.exit(0);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point punto = e.getPoint();

        // Detectar sobre qué botón está el cursor
        if (botonJugarDeNuevo.contains(punto)) {
            botonSeleccionado = 0;
        } else if (botonSalir.contains(punto)) {
            botonSeleccionado = 1;
        } else {
            botonSeleccionado = -1;
        }

        repaint();
    }

    private void iniciarJuego() {
        timerAnimacion.stop();

        // Iniciar el juego principal
        GuerreroAzteca juego = new GuerreroAzteca();
        parentFrame.getContentPane().removeAll();
        parentFrame.add(juego);
        parentFrame.revalidate();
        parentFrame.repaint();

        // Solicitar el foco para el panel de juego
        SwingUtilities.invokeLater(() -> juego.requestFocusInWindow());
    }
}