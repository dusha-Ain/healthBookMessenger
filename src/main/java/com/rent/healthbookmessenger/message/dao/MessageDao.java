package com.rent.healthbookmessenger.message.dao;

import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import com.rent.healthbookmessenger.message.entity.MessageEntity;
import com.rent.healthbookmessenger.message.entity.MessagesEntity;
import com.rent.healthbookmessenger.message.service.MessageValidator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MessageDao {

    private final NamedParameterJdbcTemplate template;
    private final MessageRowMapper rowMapper;

    public MessageDao(NamedParameterJdbcTemplate template,
                      MessageRowMapper rowMapper) {
        this.template = template;
        this.rowMapper = rowMapper;
    }

    public MessageEntity create(MessageEntity message) {
        String sql = """
            INSERT INTO message (id_chat, id_sender, content, sent_at)
            VALUES (:idChat, :idSender, :content, :sentAt)
            RETURNING id
        """;
        var map = new MapSqlParameterSource()
                .addValue("idChat", message.getIdChat())
                .addValue("idSender", message.getIdSender())
                .addValue("content", message.getContent())
                .addValue("sentAt", message.getSentAt());
        Long id = template.queryForObject(sql, map, Long.class);
        message.setId(id);
        return message;
    }

    public MessagesEntity getByIdChat(Long idChat) {
        String sql = "SELECT * FROM message WHERE id_chat=:idChat ORDER BY sent_at";
        var map = new MapSqlParameterSource()
                .addValue("idChat", idChat);
        return new MessagesEntity(template.query(sql, map, rowMapper));
    }

    public MessagesEntity  findLastMessages(Long idChat, int limit) {
        String sql = "SELECT * FROM message WHERE id_chat = :idChat ORDER BY sent_at DESC LIMIT :limit";
        var map = new MapSqlParameterSource()
                .addValue("idChat", idChat)
                .addValue("limit", limit);
        return new MessagesEntity(template.query(sql, map, rowMapper));
    }

    public Optional<MessageEntity> getById(Long id) {
        String sql = "SELECT * FROM message WHERE id = :id";
        List<MessageEntity> chat =
                template.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        return MessageValidator.validateSearchResult(chat, id);
    }

    public Optional<MessageEntity> getByIdWithLock(Long id) {
        String sql = "SELECT * FROM message WHERE id = :id FOR UPDATE";
        List<MessageEntity> chat =
                template.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        return MessageValidator.validateSearchResult(chat, id);
    }

    public boolean editMessage(Long id, String newContent) {
        String sql = "UPDATE message SET content = :content WHERE id = :id";
        var map = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("content", newContent);
        return 1 == template.update(sql, map);
    }

    public boolean deleteMessage(Long id){
        String sql = "DELETE FROM message WHERE id=:id";
        var map = new MapSqlParameterSource()
                .addValue("id", id);
        return 1 == template.update(sql, map);
    }
}