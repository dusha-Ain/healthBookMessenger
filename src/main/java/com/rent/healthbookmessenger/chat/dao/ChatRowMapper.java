package com.rent.healthbookmessenger.chat.dao;

import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class ChatRowMapper implements RowMapper<ChatEntity> {
    @Override
    public ChatEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        var chat = new ChatEntity();
        chat.setId(rs.getLong("id"));
        chat.setIdProfile(UUID.fromString(rs.getString("id_profile")));
        String idConsultant = rs.getString("id_consultant");
        if (idConsultant != null) {
            chat.setIdConsultant(UUID.fromString(idConsultant));
        }
        chat.setStatus(rs.getString("status"));
        chat.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("last_message_at") != null) {
            chat.setLastMessageAt(rs.getTimestamp("last_message_at").toLocalDateTime());
        }
        return chat;
    }
}
