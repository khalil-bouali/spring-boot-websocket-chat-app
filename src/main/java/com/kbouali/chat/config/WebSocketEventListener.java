package com.kbouali.chat.config;

import com.kbouali.chat.chat.ChatMessage;
import com.kbouali.chat.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static com.kbouali.chat.chat.MessageType.LEAVE;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username == null) {
            return;
        }
        log.info("User disconnected: {}", username);
        ChatMessage chatMessage = ChatMessage.builder()
                .type(LEAVE)
                .sender(username)
                .build();

        messageTemplate.convertAndSend("/topic/public", chatMessage);
    }
}
