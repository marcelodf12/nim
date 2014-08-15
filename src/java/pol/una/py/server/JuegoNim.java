/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pol.una.py.server;

import javax.websocket.Session;

/**
 *
 * @author marcelo
 */
public class JuegoNim {

    private boolean[][] matriz = new boolean[8][8];
    private String id;
    private Session jugador1;
    private Session jugador2;

    public JuegoNim(Session j1, Session j2) {
        int f, c;
        for (f = 0; f < 8; f++) {
            for (c = 0; c < 8; c++) {
                matriz[f][c] = true;
            }
        }
        jugador1 = j1;
        jugador2 = j2;
        id = jugador1.getId() + jugador2.getId();
    }

    public boolean esJuego(Session j1, Session j2) {
        return
                this.id.compareTo(j1.getId() + j2.getId()) == 0
                ||
                this.id.compareTo(j2.getId() + j1.getId()) == 0;
    }

    public void jugar(int f, int c) {
        try {
            int i, j;
            for (i = c; i < 8; i++) {
                for (j = 1; j < f; j++) {
                    this.matriz[i][j] = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error." + e.getMessage());
        }
    }

    public boolean hayGanador() {
        return matriz[7][0] == false;
    }

    public String getId() {
        return this.id;
    }

    public boolean[][] getMatriz() {
        return this.matriz;
    }

    public Session getJ2() {
        return jugador2;
    }

    public Session getJ1() {
        return jugador1;
    }
}
