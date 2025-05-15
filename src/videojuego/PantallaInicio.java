package videojuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PantallaInicio extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private static final int ANCHO = 800;
    private static final int ALTO = 300;

    // Componentes gráficos
    private Image fondoInicio;
    private Image imagenGuerrero;
    private JFrame parentFrame;

    // Animación del título
    private float angulo = 0;
    private Timer timerAnimacion;

    // Opciones del menú
    private Rectangle botonJugar;
    private Rectangle botonInstrucciones;
    private Rectangle botonCreditos;
    private Rectangle botonSalir;

    private int botonSeleccionado = -1;
    private boolean showInstrucciones = false;
    private boolean showCreditos = false;

    // Fuentes
    private Font fuenteTitulo;
    private Font fuenteMenu;
    private Font fuenteTexto;

    public PantallaInicio(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);

        // Crear áreas para los botones
        botonJugar = new Rectangle(ANCHO / 2 - 100, 120, 200, 40);
        botonInstrucciones = new Rectangle(ANCHO / 2 - 100, 170, 200, 40);
        botonCreditos = new Rectangle(ANCHO / 2 - 100, 220, 200, 40);
        botonSalir = new Rectangle(ANCHO / 2 - 100, 270, 200, 40);

        // Cargar recursos
        cargarRecursos();

        // Añadir listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        // Iniciar animación
        timerAnimacion = new Timer(16, this);
        timerAnimacion.start();
    }

    private void cargarRecursos() {
        try {
            // Cargar imágenes
            fondoInicio = ImageIO.read(new File("src/imagenes/fondos/fondo_inicio.png"));
            imagenGuerrero = ImageIO.read(new File("src/imagenes/guerrero/guerrero_menu.png"));

            // Cargar fuentes
            try {
                fuenteTitulo = Font.createFont(Font.TRUETYPE_FONT, new File("src/fuentes/azteca.ttf")).deriveFont(40f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(fuenteTitulo);
            } catch (FontFormatException | IOException e) {
                fuenteTitulo = new Font("Serif", Font.BOLD, 50);
            }

            fuenteMenu = fuenteTitulo.deriveFont(20f);
            fuenteTexto = fuenteTitulo.deriveFont(16f);

        } catch (IOException e) {
            System.out.println("Error al cargar recursos: " + e.getMessage());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Activar antialiasing para mejor calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar fondo
        if (fondoInicio != null) {
            g.drawImage(fondoInicio, 0, 0, ANCHO, ALTO, null);
        } else {
            // Fondo de respaldo con degradado
            GradientPaint gradiente = new GradientPaint(0, 0, new Color(120, 60, 20),
                    0, ALTO, new Color(60, 30, 10));
            g2d.setPaint(gradiente);
            g2d.fillRect(0, 0, ANCHO, ALTO);
        }

        // Dibujar decoraciones aztecas en los bordes
        dibujarDecoracionesAztecas(g2d);

        // Dibujar imagen del guerrero
        if (imagenGuerrero != null) {
            g.drawImage(imagenGuerrero, 50, ALTO - 200, null);
        }

        // Si estamos mostrando instrucciones o créditos
        if (showInstrucciones) {
            dibujarInstrucciones(g2d);
            return;
        } else if (showCreditos) {
            dibujarCreditos(g2d);
            return;
        }

        // Dibujar título con efecto de rotación suave
        dibujarTitulo(g2d);

        // Dibujar menú
        dibujarMenu(g2d);
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
        String titulo = "GUERRERO AZTECA";
        int anchoTitulo = fm.stringWidth(titulo);

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(titulo, -anchoTitulo / 2 + 3, 3); // Sombra

        // Degradado para el título
        GradientPaint gradienteTitulo = new GradientPaint(
                -anchoTitulo / 2, -40, new Color(255, 215, 0),
                anchoTitulo / 2, 40, new Color(210, 105, 30));
        g2d.setPaint(gradienteTitulo);
        g2d.drawString(titulo, -anchoTitulo / 2, 0);

        // Restaurar transformación
        g2d.setTransform(transformOriginal);
    }

    private void dibujarMenu(Graphics2D g2d) {
        g2d.setFont(fuenteMenu);

        // Dibujar cada botón
        dibujarBoton(g2d, botonJugar, "Jugar", 0);
        dibujarBoton(g2d, botonInstrucciones, "Instrucciones", 1);
        dibujarBoton(g2d, botonCreditos, "Créditos", 2);
        dibujarBoton(g2d, botonSalir, "Salir", 3);
    }

    private void dibujarBoton(Graphics2D g2d, Rectangle boton, String texto, int indice) {
        // Determinar si el botón está seleccionado
        boolean seleccionado = (indice == botonSeleccionado);

        // Dibujar fondo del botón
        GradientPaint gradienteBoton;
        if (seleccionado) {
            // Botón seleccionado con brillo
            gradienteBoton = new GradientPaint(
                    boton.x, boton.y, new Color(255, 215, 0),
                    boton.x, boton.y + boton.height, new Color(210, 105, 30));
        } else {
            // Botón normal
            gradienteBoton = new GradientPaint(
                    boton.x, boton.y, new Color(120, 60, 30),
                    boton.x, boton.y + boton.height, new Color(80, 40, 20));
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

    private void dibujarDecoracionesAztecas(Graphics2D g2d) {
        // Dibujar patrones aztecas en las esquinas
        int tamañoPatron = 80;

        // Esquina superior izquierda
        dibujarPatronAzteca(g2d, 10, 10, tamañoPatron);

        // Esquina superior derecha
        dibujarPatronAzteca(g2d, ANCHO - tamañoPatron - 10, 10, tamañoPatron);

        // Esquina inferior izquierda
        dibujarPatronAzteca(g2d, 10, ALTO - tamañoPatron - 10, tamañoPatron);

        // Esquina inferior derecha
        dibujarPatronAzteca(g2d, ANCHO - tamañoPatron - 10, ALTO - tamañoPatron - 10, tamañoPatron);
    }

    private void dibujarPatronAzteca(Graphics2D g2d, int x, int y, int tamaño) {
        g2d.setColor(new Color(230, 190, 50, 120));

        // Dibujar un patrón simple de estilo azteca
        int mitad = tamaño / 2;
        int cuarto = tamaño / 4;

        g2d.drawRect(x, y, tamaño, tamaño);
        g2d.drawRect(x + cuarto, y + cuarto, mitad, mitad);
        g2d.drawLine(x, y, x + tamaño, y + tamaño);
        g2d.drawLine(x + tamaño, y, x, y + tamaño);
        g2d.drawOval(x + cuarto, y + cuarto, mitad, mitad);

        // Dibujar triángulos en los bordes
        int[] xPoints = { x + mitad, x, x + tamaño };
        int[] yPoints = { y - cuarto, y + mitad, y + mitad };
        g2d.drawPolygon(xPoints, yPoints, 3);

        int[] xPoints2 = { x + mitad, x, x + tamaño };
        int[] yPoints2 = { y + tamaño + cuarto, y + mitad, y + mitad };
        g2d.drawPolygon(xPoints2, yPoints2, 3);
    }

    private void dibujarInstrucciones(Graphics2D g2d) {
        // Dibujar panel semitransparente para instrucciones
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(100, 50, ANCHO - 200, ALTO - 100, 20, 20);

        g2d.setColor(new Color(255, 215, 0));
        g2d.drawRoundRect(100, 50, ANCHO - 200, ALTO - 100, 20, 20);

        // Título de instrucciones
        // g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
        g2d.setFont(fuenteTexto);
        g2d.drawString("INSTRUCCIONES", 320, 85);

        // Texto de instrucciones

        g2d.setColor(Color.WHITE);

        String[] instrucciones = {
                "• Presiona ESPACIO o FLECHA ARRIBA para saltar.",
                "• Recoge frutas y oro para obtener puntos adicionales.",
                "• Evita obstáculos como lanzas, piedras y pinchos.",
                "• Acumula 3000 puntos para enfrentarte al jefe final.",
                "• En el combate con el jefe, responde correctamente las preguntas para atacar.",
                "• Presiona X o CONTROL para iniciar el ataque contra el jefe.",
                "• Usa las teclas A, B, C, D para seleccionar respuestas y ENTER para confirmar."
        };

        int y = 130;
        for (String linea : instrucciones) {
            g2d.drawString(linea, 150, y);
            y += 25;
        }

        // Botón de volver
        Rectangle botonVolver = new Rectangle(350, 280, 100, 30);
        dibujarBotonSimple(g2d, botonVolver, "Volver", botonSeleccionado == 4);
    }

    private void dibujarCreditos(Graphics2D g2d) {
        // Dibujar panel semitransparente para créditos
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(100, 50, ANCHO - 200, ALTO - 100, 20, 20);

        g2d.setColor(new Color(255, 215, 0));
        g2d.drawRoundRect(100, 50, ANCHO - 200, ALTO - 100, 20, 20);

        // Título de créditos
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2d.drawString("CRÉDITOS", 340, 85);

        // Texto de créditos
        g2d.setFont(fuenteTexto);
        g2d.setColor(Color.WHITE);

        String[] creditos = {
                "Diseño y Programación: Tu Nombre",
                "Arte y Gráficos: Nombre del Artista",
                "Música y Sonido: Nombre del Músico",
                "",
                "Agradecimientos especiales a:",
                "- Amigos y familia por su apoyo",
                "- Profesores y mentores",
                "- Comunidad de desarrolladores de juegos"
        };

        int y = 130;
        for (String linea : creditos) {
            g2d.drawString(linea, 150, y);
            y += 25;
        }

        // Botón de volver
        Rectangle botonVolver = new Rectangle(350, 280, 100, 30);
        dibujarBotonSimple(g2d, botonVolver, "Volver", botonSeleccionado == 4);
    }

    private void dibujarBotonSimple(Graphics2D g2d, Rectangle boton, String texto, boolean seleccionado) {
        // Fondo del botón
        if (seleccionado) {
            g2d.setColor(new Color(255, 215, 0));
        } else {
            g2d.setColor(new Color(150, 75, 30));
        }
        g2d.fillRoundRect(boton.x, boton.y, boton.width, boton.height, 15, 15);

        // Borde
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRoundRect(boton.x, boton.y, boton.width, boton.height, 15, 15);

        // Texto
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTexto = fm.stringWidth(texto);

        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, boton.x + (boton.width - anchoTexto) / 2,
                boton.y + ((boton.height - fm.getHeight()) / 2) + fm.getAscent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Animar el título
        angulo += 0.05;

        repaint();
    }

    // Métodos de gestión de mouse
    @Override
    public void mouseClicked(MouseEvent e) {
        Point punto = e.getPoint();

        if (showInstrucciones || showCreditos) {
            // Si estamos en instrucciones o créditos, revisar botón volver
            Rectangle botonVolver = new Rectangle(350, 280, 100, 30);
            if (botonVolver.contains(punto)) {
                showInstrucciones = false;
                showCreditos = false;
                botonSeleccionado = -1;
            }
            return;
        }

        // Comprobar si hemos hecho clic en algún botón
        if (botonJugar.contains(punto)) {
            iniciarJuego();
        } else if (botonInstrucciones.contains(punto)) {
            showInstrucciones = true;
        } else if (botonCreditos.contains(punto)) {
            showCreditos = true;
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

        if (showInstrucciones || showCreditos) {
            // Si estamos en instrucciones o créditos, revisar botón volver
            Rectangle botonVolver = new Rectangle(350, 280, 100, 30);
            botonSeleccionado = botonVolver.contains(punto) ? 4 : -1;
            return;
        }

        // Detectar sobre qué botón está el cursor
        if (botonJugar.contains(punto)) {
            botonSeleccionado = 0;
        } else if (botonInstrucciones.contains(punto)) {
            botonSeleccionado = 1;
        } else if (botonCreditos.contains(punto)) {
            botonSeleccionado = 2;
        } else if (botonSalir.contains(punto)) {
            botonSeleccionado = 3;
        } else {
            botonSeleccionado = -1;
        }

        repaint();
    }

    private void iniciarJuego() {
        timerAnimacion.stop();

        // Iniciar el juego principal
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new GuerreroAzteca());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
