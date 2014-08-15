/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pol.una.py.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author marcelo
 */
@ServerEndpoint("/NimEndpoint")
public class NimEndpoint {

    static final Logger LOGGER = Logger.getLogger(NimEndpoint.class.getName());
    static final List<Session> conexiones = new ArrayList<>();
    static final List<JuegoNim> juegos = new ArrayList<>();
    static final Hashtable nick_session = new Hashtable();
    static final List<String> listaDeNicks = new ArrayList();

    @OnMessage
    public void onMessage(String message, Session cliente) throws IOException, JSONException {
        /*
         Codigo
         Registrar Nick  # 0;Nick;0;0
         Jugar con       # 1;Nick;0;0
         Hacer jugada    # 2;Nick;X;Y
         Me rindo        # 3;Nick;0;0
         */

        String[] codigos = message.split(";");
        String nick = codigos[1];
        Session oponente = (Session) nick_session.get(nick);
        int x, y;
        x = (int) Integer.valueOf(codigos[2]);
        y = (int) Integer.valueOf(codigos[3]);
        if (codigos[0].compareTo("0") == 0) {
            if (listaDeNicks.indexOf(nick) >= 0) {
                nick += cliente.getId().substring(1, 4);
            }
            listaDeNicks.add(nick);
            nick_session.put(nick, cliente);
        } else if (codigos[0].compareTo("1") == 0) {
            juegos.add(new JuegoNim(cliente, oponente));
            oponente.getBasicRemote().sendText("j: " + getNick(cliente));
            LOGGER.log(Level.INFO, "Retando a: {0}", nick);
        } else if (codigos[0].compareTo("2") == 0) {
            LOGGER.log(Level.INFO, "Jugada a: {0}", message);
            for (JuegoNim juego : juegos) {
                if (juego.esJuego(cliente, oponente)) {
                    juego.jugar(x, y);
                    try {
                        if (juego.hayGanador()) {
                            cliente.getBasicRemote().sendText("perdio:" + nick);
                            oponente.getBasicRemote().sendText("gano:" + nick);
                            juegos.remove(juego);
                        } else {
                            oponente.getBasicRemote().sendText("a:" + String.valueOf(x) + ":" + String.valueOf(y) + ":" + getNick(cliente));
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(NimEndpoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }

        } else if (codigos[0].compareTo("3") == 0) {
            LOGGER.log(Level.INFO, "Cerrando: {0}", message);
            oponente.getBasicRemote().sendText("x: " + getNick(cliente));
            for (JuegoNim j : juegos) {
                if (j.esJuego(oponente, cliente)) {
                    juegos.remove(j);
                }
            }
        }
        NimEndpoint.informarSesiones();
        LOGGER.log(Level.INFO, "LOG: {0}", message);
    }

    @OnOpen
    public void iniciaSesion(Session session) {
        LOGGER.log(Level.INFO, "Iniciando la conexion de {0}", session.getId());
        conexiones.add(session); //Agregar a la lista
    }

    @OnClose
    public void finConexion(Session session) {
        LOGGER.info("Terminando la conexion");
        if (conexiones.contains(session)) { // se averigua si está en la colección
            try {
                LOGGER.log(Level.INFO, "Terminando la conexion de {0}", session.getId());
                String nickDesertor = getNick(session);
                conexiones.remove(session);
                for (Session c : conexiones) {
                    c.getBasicRemote().sendText("x: " + nickDesertor);
                }
                for (String n : listaDeNicks) {
                    Session user = (Session) nick_session.get(n);
                    if (session.getId().compareTo(user.getId()) == 0) {
                        listaDeNicks.remove(n);
                        break;
                    }
                }
                nick_session.remove(session);
                session.close(); //se cierra la conexión
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, "Error: {0}", ex.getMessage());
                LOGGER.log(Level.INFO, "Pila: {0}", ex.getStackTrace());
            }
        }
        NimEndpoint.informarSesiones();
    }

    static private void informarSesiones() {
        for (Session s : conexiones) {
            avisarCambios(s);
        }

    }

    static private void avisarCambios(Session cliente, String nick) {
        boolean entro = false;
        if (listaDeNicks.indexOf(nick) >= 0) {
            listaDeNicks.remove(nick);
            entro = true;
        }
        try {
            JSONObject jsonNicks = new JSONObject().put("nick", listaDeNicks);
            cliente.getBasicRemote().sendText(jsonNicks.toString());
        } catch (JSONException | IOException ex) {
            Logger.getLogger(NimEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (entro) {
            listaDeNicks.add(nick);
        }
    }

    static private void avisarCambios(Session cliente) {
        String nick = null;
        for (String n : listaDeNicks) {
            if (cliente == nick_session.get(n)) {
                nick = n;
                avisarCambios(cliente, nick);
                break;
            }
        }
    }

    static private String getNick(Session s) {
        for (String n : listaDeNicks) {
            if (s == nick_session.get(n)) {
                return n;
            }
        }
        return null;
    }
}
