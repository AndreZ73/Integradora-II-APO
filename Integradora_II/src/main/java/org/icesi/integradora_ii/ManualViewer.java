package org.icesi.integradora_ii;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority; // Importar Priority

public class ManualViewer {

    private static Stage manualStage;

    public static void toggleManualVisibility() {
        if (manualStage == null) {
            manualStage = new Stage();
            manualStage.setTitle("MANUAL DE USUARIO – SGMMS");
            manualStage.setResizable(false);

            TextArea manualContent = new TextArea();
            manualContent.setEditable(false);
            manualContent.setWrapText(true);
            manualContent.setText(
                    "MANUAL DE USUARIO – SGMMS\n" +
                            "Objetivo del Simulador\n" +
                            "Como operador municipal, tu misión es garantizar la movilidad segura y la atención oportuna a incidentes en la ciudad de Palmira. Deberás asignar vehículos de emergencia, monitorear el mapa y mantener los indicadores bajo control.\n\n" +
                            "Controles de Navegación (Mapa 2D)\n" +
                            "Utiliza las siguientes teclas para moverte por la ciudad:\n" +
                            "W – Mover hacia arriba\n" +
                            "A – Mover hacia la izquierda\n" +
                            "S – Mover hacia abajo\n" +
                            "D – Mover hacia la derecha\n\n" +
                            "Escenas del Simulador\n" +
                            "Puedes cambiar entre las siguientes escenas:\n" +
                            "Centro de Monitoreo: Vista general del estado del sistema.\n" +
                            "Mapa de Tráfico: Vista del mapa 2D donde se ubican los vehículos e incidentes.\n" +
                            "Panel de Incidentes: Lista de incidentes activos y herramientas para gestionarlos.\n\n" +
                            "Tipos de Incidentes\n" +
                            "Los incidentes aparecerán aleatoriamente o como resultado del comportamiento de los vehículos:\n" +
                            "Accidentes de tráfico (basados en colisiones entre autómatas)\n" +
                            "Robos (aparición aleatoria)\n" +
                            "Incendios (aparición aleatoria)\n\n" +
                            "Cada incidente tendrá una gravedad (1–5) que determina su prioridad.\n\n" +
                            "Gestión de Vehículos\n" +
                            "Hay tres tipos de vehículos de emergencia:\n" +
                            "Ambulancias – Alta velocidad, alta prioridad para accidentes.\n" +
                            "Patrullas policiales – Velocidad media, prioridad para robos.\n" +
                            "Bomberos – Velocidad media-baja, prioridad para incendios.\n\n" +
                            "¿Cómo asignar un vehículo?\n" +
                            "Desde el Panel de Incidentes, selecciona un incidente y asigna un vehículo disponible. Este se moverá automáticamente hasta el lugar del incidente.\n\n" +
                            "Indicadores en Tiempo Real\n" +
                            "La interfaz gráfica mostrará:\n" +
                            "Número de incidentes activos, diferenciados por ícono (🔥 fuego, 🚓 robo, 💥 accidente).\n" +
                            "Puntaje actual, basado en eficiencia operativa.\n" +
                            "Estado de los vehículos (libre / en misión).\n\n" +
                            "Sistema de Puntuación\n" +
                            "Ganarás puntos por cada incidente resuelto, según:\n" +
                            "Tiempo de respuesta\n" +
                            "Tipo de vehículo utilizado\n" +
                            "Cantidad de incidentes resueltos simultáneamente\n\n" +
                            "¡Evita que los incidentes se acumulen por demasiado tiempo o perderás puntos!\n\n" +
                            "Recomendaciones Estratégicas:\n" +
                            "Prioriza los incidentes de mayor gravedad.\n" +
                            "Usa los vehículos más rápidos para los casos urgentes.\n" +
                            "Revisa constantemente el Centro de Monitoreo para tomar decisiones globales.\n\n" +
                            "Soporte:\n" +
                            "Si experimentas errores o deseas reiniciar la simulación, accede al botón “Reiniciar” desde el menú principal."
            );

            ScrollPane scrollPane = new ScrollPane(manualContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            VBox rootManual = new VBox(scrollPane);
            rootManual.setStyle("-fx-background-color: #a11616;");

            // ¡LA CLAVE ESTÁ AQUÍ! Indicar al VBox que el scrollPane debe crecer verticalmente
            VBox.setVgrow(scrollPane, Priority.ALWAYS); //

            // Mantén el tamaño de la ventana como lo tenías, por ejemplo 600x500
            // O ajústalo ligeramente si quieres que la ventana en sí sea un poco más alta o ancha
            Scene manualScene = new Scene(rootManual, 600, 500);
            manualStage.setScene(manualScene);

        }

        if (manualStage.isShowing()) {
            manualStage.hide();
        } else {
            manualStage.show();
            manualStage.toFront();
        }
    }
}