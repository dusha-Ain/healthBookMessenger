package com.rent.healthbookmessenger.message.dao;

import com.rent.healthbookmessenger.message.entity.MessageEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class MessageRowMapper implements RowMapper<MessageEntity> {
    @Override
    public MessageEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        var message = new MessageEntity();
        message.setId(rs.getLong("id"));
        message.setIdChat(rs.getLong("id_chat"));
        message.setIdSender(UUID.fromString(rs.getString("id_sender")));
        message.setContent(rs.getString("content"));
        message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
        return message;
    }
}
