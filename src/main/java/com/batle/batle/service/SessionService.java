package com.batle.batle.service;


import com.batle.batle.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
public class SessionService {
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        players.remove(session.getId());
    }

    public void setNick(String sessionId, String nick) {
        Player player = new Player();
        player.setId(sessionId);
        player.setName(nick);
        players.put(sessionId, player);
    }

    public Player getPlayer(String sessionId) {
        return players.get(sessionId);
    }

    public Collection<WebSocketSession> getAllSessions() {
        return sessions.values();
    }

    public String getNickBySessionId(String sessionId) {
        Player player = players.get(sessionId);
        return player != null ? player.getName() : "Desconhecido";
    }
}