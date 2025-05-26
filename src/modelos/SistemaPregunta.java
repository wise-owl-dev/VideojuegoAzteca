package modelos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class SistemaPregunta {

    private String pregunta;
    private String respuestaCorrecta;
    private List<String> opciones;
    private boolean activa;
    private Image fondoPregunta;
    private int opcionSeleccionada;

    // Lista de preguntas y respuestas
    private List<String[]> bancoPreguntas;

    public SistemaPregunta() {
        this.activa = false;
        this.opcionSeleccionada = -1;
        this.opciones = new ArrayList<>();
        this.bancoPreguntas = new ArrayList<>();
        cargarPreguntas();
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            fondoPregunta = ImageIO.read(getClass().getResource("/imagenes/fondos/fondo_pregunta.png"));
        } catch (IOException e) {
            System.out.println("Error al cargar imagen del fondo de pregunta: " + e.getMessage());
            fondoPregunta = null;
        }
    }

    private void cargarPreguntas() {
        // === MATEMÁTICAS ===
        bancoPreguntas.add(new String[] { "¿Cuál es el valor de (5 + 3) × 2?", "16", "13", "16", "10", "15" });
        bancoPreguntas.add(new String[] { "¿Cuánto es el 25% de 200?", "50", "25", "75", "50", "100" });
        bancoPreguntas.add(new String[] { "Resuelve: 3x + 3 = 15. ¿Cuánto vale x?", "4", "5", "3", "4", "6" });
        bancoPreguntas.add(new String[] { "¿Cuál es la fórmula para el área de un triángulo?", "(Base × Altura) ÷ 2",
                "(Base × Altura) ÷ 2", "Base × Altura", "Base + Altura ÷ 2", "π × radio²" });
        bancoPreguntas
                .add(new String[] { "¿Qué es un número primo?", "Número que solo tiene dos divisores: 1 y él mismo",
                        "Número que solo tiene dos divisores: 1 y él mismo", "Número par mayor que 2",
                        "Número divisible entre 3", "Número con muchos divisores" });
        bancoPreguntas.add(new String[] { "Convierte 0.75 a fracción", "3/4", "3/4", "2/3", "4/5", "7/10" });
        bancoPreguntas.add(new String[] { "¿Cuál es el perímetro de un cuadrado de 6 cm de lado?", "24 cm", "24 cm",
                "36 cm", "12 cm", "18 cm" });
        bancoPreguntas.add(new String[] { "¿Qué representa π en matemáticas?",
                "La relación entre circunferencia y diámetro", "La relación entre circunferencia y diámetro",
                "El área de un círculo", "La mitad del radio", "El doble del diámetro" });
        bancoPreguntas.add(new String[] { "¿Cuál es el mínimo común múltiplo de 4 y 6?", "12", "12", "24", "8", "10" });
        bancoPreguntas.add(new String[] { "¿Qué tipo de ángulo mide exactamente 90°?", "Ángulo recto", "Ángulo recto",
                "Ángulo agudo", "Ángulo obtuso", "Ángulo llano" });

        // === ESPAÑOL ===
        bancoPreguntas
                .add(new String[] { "¿Qué es un sustantivo?", "Palabra que nombra personas, animales, cosas o ideas",
                        "Palabra que nombra personas, animales, cosas o ideas", "Palabra que describe una acción",
                        "Palabra que modifica al verbo", "Palabra que une oraciones" });
        bancoPreguntas.add(new String[] { "Identifica el verbo en: 'Ella canta muy bien'", "canta", "canta", "ella",
                "muy", "bien" });
        bancoPreguntas.add(new String[] { "¿Qué es un adjetivo?", "Palabra que describe a un sustantivo",
                "Palabra que describe a un sustantivo", "Palabra que indica acción", "Palabra que conecta ideas",
                "Palabra que expresa tiempo" });
        bancoPreguntas.add(new String[] { "¿Qué es una oración simple?",
                "Oración con un solo verbo y una idea principal", "Oración con un solo verbo y una idea principal",
                "Oración con muchos verbos", "Oración muy corta", "Oración sin sujeto" });
        bancoPreguntas.add(new String[] { "¿Qué tipo de texto es una receta de cocina?", "Texto instructivo",
                "Texto instructivo", "Texto narrativo", "Texto descriptivo", "Texto argumentativo" });
        bancoPreguntas
                .add(new String[] { "Da un ejemplo de palabra aguda", "Café", "Café", "Mesa", "Árbol", "Música" });
        bancoPreguntas.add(new String[] { "¿Cuál es el antónimo de 'alegría'?", "Tristeza", "Tristeza", "Felicidad",
                "Emoción", "Diversión" });
        bancoPreguntas.add(new String[] { "¿Qué es un sinónimo?", "Palabra con significado similar a otra",
                "Palabra con significado similar a otra", "Palabra con significado opuesto", "Palabra que rima",
                "Palabra compuesta" });
        bancoPreguntas.add(new String[] { "¿Qué es una fábula?", "Relato breve con moraleja",
                "Relato breve con moraleja", "Poema largo", "Obra de teatro", "Cuento de terror" });

        // === CIENCIAS NATURALES ===
        bancoPreguntas.add(new String[] { "¿Cuáles son los tres estados de la materia?", "Sólido, líquido y gaseoso",
                "Sólido, líquido y gaseoso", "Frío, tibio y caliente", "Sólido, plasma y gaseoso",
                "Mineral, animal y vegetal" });
        bancoPreguntas.add(new String[] { "¿Qué órgano bombea la sangre en el cuerpo humano?", "El corazón",
                "El corazón", "Los pulmones", "El hígado", "El cerebro" });
        bancoPreguntas.add(new String[] { "¿Qué parte de la planta realiza la fotosíntesis?", "Las hojas", "Las hojas",
                "Las raíces", "El tallo", "Las flores" });
        bancoPreguntas.add(new String[] { "¿Qué planeta es conocido como el planeta rojo?", "Marte", "Marte", "Venus",
                "Júpiter", "Saturno" });
        bancoPreguntas.add(new String[] { "¿Qué es un herbívoro?", "Animal que se alimenta de plantas",
                "Animal que se alimenta de plantas", "Animal que come carne", "Animal que come de todo",
                "Animal que no come" });
        bancoPreguntas.add(new String[] { "¿Qué función tiene el sistema respiratorio?",
                "Intercambiar oxígeno y dióxido de carbono", "Intercambiar oxígeno y dióxido de carbono",
                "Bombear sangre", "Digerir alimentos", "Filtrar desechos" });
        bancoPreguntas.add(new String[] { "¿Qué es una célula?", "La unidad básica de los seres vivos",
                "La unidad básica de los seres vivos", "Un tipo de bacteria", "Un órgano pequeño",
                "Una parte del ADN" });
        bancoPreguntas.add(new String[] { "¿Cuáles son los cinco sentidos del cuerpo humano?",
                "Vista, oído, olfato, gusto y tacto", "Vista, oído, olfato, gusto y tacto",
                "Vista, oído, habla, gusto y tacto", "Vista, memoria, olfato, gusto y tacto",
                "Vista, oído, olfato, respiración y tacto" });
        bancoPreguntas.add(new String[] { "¿Qué es una cadena alimenticia?",
                "Relación de alimentación entre seres vivos", "Relación de alimentación entre seres vivos",
                "Tipo de planta", "Grupo de animales", "Clase de ecosistema" });
        bancoPreguntas.add(new String[] { "¿Qué fuerza nos mantiene en la Tierra?", "La gravedad", "La gravedad",
                "El magnetismo", "La electricidad", "El viento" });

        // === HISTORIA ===
        bancoPreguntas.add(new String[] { "¿En qué año se inició la Independencia de México?", "1810", "1810", "1821",
                "1910", "1917" });
        bancoPreguntas.add(new String[] { "¿Quién dio el Grito de Dolores?", "Miguel Hidalgo y Costilla",
                "Miguel Hidalgo y Costilla", "José María Morelos", "Benito Juárez", "Emiliano Zapata" });
        bancoPreguntas.add(new String[] { "¿Qué civilización construyó Chichén Itzá?", "Los mayas", "Los mayas",
                "Los aztecas", "Los olmecas", "Los zapotecas" });
        bancoPreguntas.add(
                new String[] { "¿Qué país colonizó México?", "España", "España", "Francia", "Portugal", "Inglaterra" });
        bancoPreguntas.add(
                new String[] { "¿Quién fue Benito Juárez?", "Presidente mexicano que defendió las leyes de Reforma",
                        "Presidente mexicano que defendió las leyes de Reforma", "Conquistador español",
                        "Revolucionario del siglo XX", "Emperador de México" });
        bancoPreguntas.add(new String[] { "¿Cuándo ocurrió la Revolución Mexicana?", "En 1910", "En 1910", "En 1810",
                "En 1821", "En 1917" });
        bancoPreguntas.add(new String[] { "¿Qué es una cultura prehispánica?",
                "Civilización que existió antes de la llegada de los españoles",
                "Civilización que existió antes de la llegada de los españoles", "Cultura española en México",
                "Cultura moderna mexicana", "Cultura europea en América" });
        bancoPreguntas.add(new String[] { "¿Qué evento histórico ocurrió el 5 de mayo?", "La Batalla de Puebla",
                "La Batalla de Puebla", "El Grito de Dolores", "La Independencia", "La Revolución" });
        bancoPreguntas.add(new String[] { "¿Qué hizo José María Morelos?",
                "Continuó la lucha por la independencia tras la muerte de Hidalgo",
                "Continuó la lucha por la independencia tras la muerte de Hidalgo",
                "Fue el primer presidente de México", "Conquistó el imperio azteca", "Fundó la ciudad de México" });
        bancoPreguntas.add(new String[] { "¿Qué es la Constitución de 1917?",
                "Documento que establece las leyes fundamentales de México",
                "Documento que establece las leyes fundamentales de México", "Tratado de paz con España",
                "Declaración de independencia", "Acuerdo comercial" });

        // === GEOGRAFÍA ===
        bancoPreguntas.add(
                new String[] { "¿Qué continente es el más grande?", "Asia", "América", "Europa", "Asia", "África" });
        bancoPreguntas.add(new String[] { "¿Qué océano es el más extenso?", "Océano Pacífico", "Océano Pacífico",
                "Océano Atlántico", "Océano Índico", "Océano Ártico" });
        bancoPreguntas.add(
                new String[] { "¿Qué es un mapa?", "Representación gráfica de una parte o la totalidad de la Tierra",
                        "Representación gráfica de una parte o la totalidad de la Tierra",
                        "Instrumento para medir distancias", "Tipo de brújula", "Fotografía aérea" });
        bancoPreguntas.add(new String[] { "¿Qué línea divide la Tierra en hemisferio norte y sur?", "El Ecuador",
                "El Ecuador", "El Trópico de Cáncer", "El Meridiano de Greenwich", "El Trópico de Capricornio" });
        bancoPreguntas.add(new String[] { "¿Qué es una cordillera?", "Conjunto de montañas unidas entre sí",
                "Conjunto de montañas unidas entre sí", "Río muy largo", "Valle profundo", "Meseta elevada" });
        bancoPreguntas.add(new String[] { "¿Qué países forman América del Norte?", "Canadá, Estados Unidos y México",
                "Canadá, Estados Unidos y México", "Solo Estados Unidos y México", "Estados Unidos, México y Guatemala",
                "Canadá, Estados Unidos y Cuba" });
        bancoPreguntas.add(new String[] { "¿Qué es la altitud?",
                "Distancia vertical de un punto respecto al nivel del mar",
                "Distancia vertical de un punto respecto al nivel del mar", "Distancia horizontal entre dos puntos",
                "Temperatura de un lugar", "Cantidad de lluvia anual" });
        bancoPreguntas.add(new String[] { "¿Qué es una cuenca hidrográfica?",
                "Territorio donde todas las aguas drenan hacia un mismo río o lago",
                "Territorio donde todas las aguas drenan hacia un mismo río o lago", "Tipo de lago artificial",
                "Montaña muy alta", "Desierto con oasis" });
        bancoPreguntas.add(new String[] { "¿Qué es la atmósfera?", "Capa de gases que rodea la Tierra",
                "Capa de gases que rodea la Tierra", "Centro de la Tierra", "Superficie terrestre",
                "Océanos y mares" });
        bancoPreguntas.add(new String[] { "¿Qué recurso natural es vital para la vida?", "El agua", "El agua",
                "El petróleo", "El oro", "El carbón" });
    }

    public void generarPreguntaAleatoria() {
        if (bancoPreguntas.isEmpty()) {
            cargarPreguntas(); // Recargar si se agotaron
        }

        int indice = (int) (Math.random() * bancoPreguntas.size());
        String[] preguntaSeleccionada = bancoPreguntas.get(indice);

        this.pregunta = preguntaSeleccionada[0];
        this.respuestaCorrecta = preguntaSeleccionada[1];

        // Reinicia las opciones
        opciones.clear();
        for (int i = 2; i < preguntaSeleccionada.length; i++) {
            opciones.add(preguntaSeleccionada[i]);
        }

        // Mezclar opciones para que la respuesta correcta no siempre esté en la misma
        // posición
        Collections.shuffle(opciones);

        this.activa = true;
        this.opcionSeleccionada = -1;

        // Eliminar la pregunta usada para evitar repeticiones
        bancoPreguntas.remove(indice);
    }

    public void dibujar(Graphics g, int anchoVentana, int altoVentana) {
        if (!activa)
            return;

        // Dibujar fondo de pregunta
        int anchoFondo = 600;
        int altoFondo = 300;
        int xFondo = (anchoVentana - anchoFondo) / 2;
        int yFondo = (altoVentana - altoFondo) / 2;

        if (fondoPregunta != null) {
            g.drawImage(fondoPregunta, xFondo, yFondo, anchoFondo, altoFondo, null);
        } else {
            g.setColor(new Color(245, 222, 179));
            g.fillRect(xFondo, yFondo, anchoFondo, altoFondo);
            g.setColor(Color.RED);
            g.drawRect(xFondo, yFondo, anchoFondo, altoFondo);
        }
        // Fondo semitransparente negro
        g.setColor(new Color(225, 160, 87, 100));
        g.fillRect(100, 0, 600, altoVentana);

        // Dibujar pregunta
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // Dividir texto de pregunta para que se ajuste al ancho
        String[] palabras = pregunta.split(" ");
        String lineaActual = "";
        int y = yFondo + 60;
        int xTexto = xFondo + 50;
        int anchoTextoMax = anchoFondo - 100;

        for (String palabra : palabras) {
            if (g.getFontMetrics().stringWidth(lineaActual + palabra) < anchoTextoMax) {
                lineaActual += palabra + " ";
            } else {
                g.drawString(lineaActual, xTexto, y);
                y += 30;
                lineaActual = palabra + " ";
            }
        }
        g.drawString(lineaActual, xTexto, y);

        // Dibujar opciones
        g.setFont(new Font("Arial", Font.ITALIC, 18));
        y += 50;

        for (int i = 0; i < opciones.size(); i++) {
            if (i == opcionSeleccionada) {
                g.setColor(new Color(255, 215, 0)); // Dorado para seleccionado
                g.fillRect(xTexto - 30, y - 20, anchoTextoMax + 30, 30);
                g.setColor(Color.BLACK);
            }

            g.drawString((char) ('A' + i) + ") " + opciones.get(i), xTexto, y);
            y += 40;
        }

        // Instrucciones
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        g.drawString("Usa las teclas A, B, C, D para seleccionar y ENTER para confirmar",
                xFondo + 80, yFondo + altoFondo - 30);
    }

    public void seleccionarOpcion(int opcion) {
        if (activa && opcion >= 0 && opcion < opciones.size()) {
            opcionSeleccionada = opcion;
        }
    }

    public boolean confirmarRespuesta() {
        if (!activa || opcionSeleccionada == -1) {
            return false;
        }

        boolean esCorrecta = opciones.get(opcionSeleccionada).equals(respuestaCorrecta);
        activa = false;
        return esCorrecta;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }
}