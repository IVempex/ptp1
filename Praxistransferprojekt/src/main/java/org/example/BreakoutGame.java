package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreakoutGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> initUndZeigeGUI());
    }

    private static void initUndZeigeGUI() {
        // Initialisiere und zeige das GUI-Fenster
        JFrame frame = new JFrame("Breakout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout()); // Verwende FlowLayout für die Anordnung der Komponenten

        JButton spielenButton = new JButton("Spielen");
        frame.add(spielenButton);

        JButton beendenButton = new JButton("Beenden");
        frame.add(beendenButton);

        // Positioniere die Buttons nebeneinander
        spielenButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        beendenButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        spielenButton.addActionListener(e -> starteSpiel(frame, spielenButton, beendenButton));
        beendenButton.addActionListener(e -> System.exit(0));

        // Passe die Größe des JFrames an die Buttons an
        int buttonBreite = spielenButton.getPreferredSize().width;
        int buttonHoehe = spielenButton.getPreferredSize().height;
        frame.setSize(buttonBreite * 2 + 20, buttonHoehe + 80);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private static void starteSpiel(JFrame frame, JButton spielenButton, JButton beendenButton) {
        // Bereite das GUI für das Spiel vor und starte es
        frame.remove(spielenButton); // Entferne den "Spielen"-Button
        frame.remove(beendenButton); // Entferne den "Beenden"-Button
        spielenButton.setVisible(false); // Setze den "Spielen"-Button auf unsichtbar
        beendenButton.setVisible(false); // Setze den "Beenden"-Button auf unsichtbar
        frame.repaint(); // Aktualisiere das Fenster
        frame.setSize(600, 800); // Setze die Größe des Fensters für das Spiel
        frame.setLocationRelativeTo(null); // Zentriere das Fenster auf dem Bildschirm
        frame.validate(); // Validiere das Fenster

        GamePanel gamePanel = new GamePanel(); // Erzeuge ein GamePanel-Objekt
        frame.add(gamePanel); // Füge das GamePanel zum Frame hinzu
        gamePanel.requestFocusInWindow(); // Setze den Fokus auf das GamePanel
    }

static class GamePanel extends JPanel implements KeyListener, ActionListener {
        private int schlaegerX = 250; // X-Koordinate des Schlägers
        private int ballX = 300; // X-Koordinate des Balls
        private int ballY = 500; // Y-Koordinate des Balls
        private int ballGeschwindigkeitX = 5; // Geschwindigkeit des Balls in X-Richtung
        private int ballGeschwindigkeitY = -5; // Geschwindigkeit des Balls in Y-Richtung
        private List<Rectangle> ziegelsteine; // Liste für die Ziegelsteine

        public GamePanel() {
            setFocusable(true); // Setze das Panel fokussierbar für Tastatureingaben
            addKeyListener(this); // Füge den KeyListener hinzu

            setPreferredSize(new Dimension(600, 800)); // Setze die bevorzugte Größe des Panels
            initialisiereZiegelsteine(); // Initialisiere die Ziegelsteine
            Timer timer = new Timer(10, this); // Erzeuge einen Timer für die Animation
            timer.start(); // Starte den Timer
        }

        private void initialisiereZiegelsteine() {
            // Erzeuge eine Liste für die Ziegelsteine
            ziegelsteine = new ArrayList<>();
            int ziegelBreite = 50; // Breite der Ziegelsteine
            int ziegelHoehe = 20; // Höhe der Ziegelsteine
            int reihen = 5; // Anzahl der Reihen von Ziegelsteinen
            int spalten = 11; // Anzahl der Spalten von Ziegelsteinen

            for (int i = 0; i < reihen; i++) {
                for (int j = 0; j < spalten; j++) {
                    int ziegelX = j * (ziegelBreite + 5); // X-Position des Ziegelsteins
                    int ziegelY = i * (ziegelHoehe + 5); // Y-Position des Ziegelsteins
                    Rectangle ziegel = new Rectangle(ziegelX, ziegelY, ziegelBreite, ziegelHoehe); // Erzeuge einen Ziegelstein
                    ziegelsteine.add(ziegel); // Füge den Ziegelstein zur Liste hinzu
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(schlaegerX, 750, 100, 10); // Zeichne den Schläger

            g.setColor(Color.BLUE);
            g.fillOval(ballX, ballY, 20, 20); // Zeichne den Ball

            g.setColor(Color.BLUE);
            for (Rectangle ziegel : ziegelsteine) {
                g.fillRect(ziegel.x, ziegel.y, ziegel.width, ziegel.height); // Zeichne die Ziegelsteine
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            moveBall(); // Bewege den Ball
            checkCollisions(); // Überprüfe Kollisionen
            repaint(); // Aktualisiere das Panel
        }

        private void moveBall() {
            ballX += ballGeschwindigkeitX; // Bewege den Ball in X-Richtung
            ballY += ballGeschwindigkeitY; // Bewege den Ball in Y-Richtung

            if (ballX <= 0 || ballX + 20 >= getWidth()) {
                ballGeschwindigkeitX = -ballGeschwindigkeitX; // Ändere die Richtung in X-Richtung bei Kollision mit den Seitenwänden
            }

            if (ballY <= 0) {
                ballGeschwindigkeitY = -ballGeschwindigkeitY; // Ändere die Richtung in Y-Richtung bei Kollision mit der oberen Wand
            }

            if (ballY + 20 >= getHeight()) {
                // Game over
                ballGeschwindigkeitY = 0;
                ballGeschwindigkeitX = 0; // Stoppe den Ball bei Kollision mit der unteren Wand (Game Over)
            }
        }

        private void checkCollisions() {
            Rectangle ballRect = new Rectangle(ballX, ballY, 20, 20); //Erzeuge ein Rechteck für den Ball
            Rectangle schlaegerRect = new Rectangle(schlaegerX, 750, 100, 10); // Erzeuge ein Rechteck für den Schläger

            if (ballRect.intersects(schlaegerRect)) {
                ballGeschwindigkeitY = -ballGeschwindigkeitY; // Ändere die Richtung in Y-Richtung bei Kollision mit dem Schläger
            }

            for (Rectangle ziegel : ziegelsteine) {
                if (ballRect.intersects(ziegel)) {
                    ziegelsteine.remove(ziegel); // Entferne den Ziegelstein bei Kollision mit dem Ball
                    ballGeschwindigkeitY = -ballGeschwindigkeitY; // Ändere die Richtung in Y-Richtung
                    break; // Beende die Schleife, nachdem ein Ziegelstein getroffen wurde
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT && schlaegerX > 0) {
                schlaegerX -= 20;
            } else if (keyCode == KeyEvent.VK_RIGHT && schlaegerX < getWidth() - 100) {
                schlaegerX += 20;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
