/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pol.una.py.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author marcelo
 */
@ServerEndpoint("/NimEndpoint")
public class NimEndpoint {

    static final Logger LOGGER = Logger.getLogger(NimEndpoint.class.getName());
    static final List<Session> conexiones = new ArrayList<>();
    static final List<JuegoNim> juegos = new ArrayList<>();

    @OnMessage
    public void onMessage(String message, Session cliente) {
        LOGGER.log(Level.INFO, "LOG: {0}", message);
        
    }

    @OnOpen
    public void iniciaSesion(Session session) {
        LOGGER.log(Level.INFO, "Iniciando la conexion de {0}", session.getId());
        conexiones.add(session); //Agregar a la lista
        RemoteEndpoint.Basic cliente = session.getBasicRemote();
        for(Session sesion: conexiones){
            try {
                cliente.sendObject(sesion);
            } catch (    IOException | EncodeException ex) {
                Logger.getLogger(NimEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @OnClose
    public void finConexion(Session session) {
        LOGGER.info("Terminando la conexion");
        if (conexiones.contains(session)) { // se averigua si está en la colección
            try {
                LOGGER.log(Level.INFO, "Terminando la conexion de {0}", session.getId());
                session.close(); //se cierra la conexión
                conexiones.remove(session); // se retira de la lista
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
