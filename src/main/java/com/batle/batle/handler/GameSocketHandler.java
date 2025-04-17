package com.batle.batle.handler;

import com.batle.batle.model.Habilidade;
import com.batle.batle.model.Player;
import com.batle.batle.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;

@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private final SessionService sessionService;

    public GameSocketHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionService.addSession(session);

        // Gera nick aleatório e registra o jogador
        String nick = "Player-" + UUID.randomUUID().toString().substring(0, 5);
        sessionService.setNick(session.getId(), nick);

        session.sendMessage(new TextMessage("Seu nick gerado é: " + nick));
        broadcast("🎮 " + nick + " entrou na sala.");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        if (payload.startsWith("NICK:")) {
            String nick = payload.substring(5);
            sessionService.setNick(session.getId(), nick);
            session.sendMessage(new TextMessage("Bem-vindo, " + nick));
            return;
        }

        if (payload.startsWith("HABILIDADE:")) {
            String nomeHab = payload.substring(11).toUpperCase();
            try {
                Habilidade habilidade = Habilidade.valueOf(nomeHab);
                Player p = sessionService.getPlayer(session.getId());

                if (!p.podeUsar(habilidade)) {
                    session.sendMessage(new TextMessage("⏳ Habilidade em cooldown."));
                    return;
                }

                switch (habilidade) {
                    case RUSH -> session.sendMessage(new TextMessage("💨 Você correu mais rápido por 3s."));
                    case ATTACK -> session.sendMessage(new TextMessage("⚔️ Você atacou o inimigo!"));
                    case FREEZE -> session.sendMessage(new TextMessage("❄️ Você congelou o inimigo por 3s."));
                    case HEAL -> {
                        p.setVida(Math.min(100, p.getVida() + 10));
                        session.sendMessage(new TextMessage("❤️ Você se curou para " + p.getVida() + " de vida."));
                    }
                }

                p.aplicarCooldown(habilidade, 5);
            } catch (IllegalArgumentException e) {
                session.sendMessage(new TextMessage(" Habilidade inválida."));
            }
            return;
        }

        if (payload.startsWith("CHAT:")) {
            String texto = payload.substring(5).trim();
            if (!texto.isEmpty()) {
                String nick = sessionService.getNickBySessionId(session.getId());
                broadcast("💬 " + nick + ": " + texto);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String nick = sessionService.getNickBySessionId(session.getId());
        sessionService.removeSession(session);
        broadcast("👋 " + nick + " saiu da sala.");
    }

    private void broadcast(String message) {
        for (WebSocketSession s : sessionService.getAllSessions()) {
            try {
                s.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}