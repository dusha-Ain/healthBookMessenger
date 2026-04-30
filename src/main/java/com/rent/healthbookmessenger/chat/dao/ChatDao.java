package com.rent.healthbookmessenger.chat.dao;

import com.rent.healthbookmessenger.chat.dao.enums.ChatStatus;
import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import com.rent.healthbookmessenger.chat.entity.ChatsEntity;
import com.rent.healthbookmessenger.chat.service.ChatValidator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ChatDao {

    private final NamedParameterJdbcTemplate template;
    private final ChatRowMapper rowMapper;

    public ChatDao(NamedParameterJdbcTemplate template,
                   ChatRowMapper rowMapper) {
        this.template = template;
        this.rowMapper = rowMapper;
    }

    public ChatEntity create(ChatEntity chat) {
        String sql = """
                    INSERT INTO chat(id_profile, id_consultant, status, created_at, last_message_at)
                    VALUES (:idProfile, :idConsultant, :status, :created, :lastMessage)
                    RETURNING id
                """;
        var map = new MapSqlParameterSource()
                .addValue("idProfile", chat.getIdProfile())
                .addValue("idConsultant", chat.getIdConsultant())
                .addValue("status", chat.getStatus())
                .addValue("created", chat.getCreatedAt())
                .addValue("lastMessage", chat.getLastMessageAt());
        Long id = template.queryForObject(sql, map, Long.class);
        chat.setId(id);
        return chat;
    }

    public Optional<ChatEntity> getById(Long id) {
        String sql = "SELECT * FROM chat WHERE id = :id";
        List<ChatEntity> chat =
                template.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        return ChatValidator.validateSearchResult(chat, id);
    }

    public Optional<ChatEntity> getByIdWithLock(Long id) {
        String sql = "SELECT * FROM chat WHERE id = :id";
        List<ChatEntity> chat =
                template.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        return ChatValidator.validateSearchResult(chat, id);
    }

    public boolean update(ChatEntity chat) {
        String sql = """
                    UPDATE chat
                    SET id_consultant=:idConsultant, last_message_at=:lastMessage, status=:status
                    WHERE id = :id
                """;
        var map = new MapSqlParameterSource()
                .addValue("idConsultant", chat.getIdConsultant())
                .addValue("lastMessage", chat.getLastMessageAt())
                .addValue("id", chat.getId())
                .addValue("status", chat.getStatus());
        return 1 == template.update(sql, map);
    }

    public boolean updateStatus(ChatStatus status, Long id) {
        String sql = """
                UPDATE chat
                SET status=:status
                WHERE id=:id
                """;
        var map = new MapSqlParameterSource()
                .addValue("status", status.name())
                .addValue("id", id);
        return 1 == template.update(sql, map);
    }

    public ChatsEntity getByStatus(ChatStatus status) {
        String sql = "SELECT * FROM chat WHERE status = :status ORDER BY created_at";
        var map = new MapSqlParameterSource()
                .addValue("status", status.name());
        return new ChatsEntity(template.query(sql, map, rowMapper));
    }

    public Optional<ChatEntity> getChatByIdProfile(UUID idProfile) {
        String sql = """
                SELECT * FROM chat
                WHERE id_profile = :idProfile
                """;
        var map = new MapSqlParameterSource()
                .addValue("idProfile", idProfile);
        List<ChatEntity> chat = template.query(sql, map, rowMapper);
        return ChatValidator.validateSearchResultByProfile(chat, idProfile);
    }

    public ChatsEntity getActiveChatsByIdConsultant(UUID idConsultant) {
        String sql = """
                SELECT * FROM chat
                WHERE id_consultant = :idConsultant
                AND status = 'ACTIVE'
                """;
        var map = new MapSqlParameterSource()
                .addValue("idConsultant", idConsultant);
        return new ChatsEntity(template.query(sql, map, rowMapper));
    }

    public boolean updateLastMessageTime(Long idChat, LocalDateTime time) {
        String sql = "UPDATE chat SET last_message_at=:time WHERE id = :id";
        var map = new MapSqlParameterSource()
                .addValue("id", idChat)
                .addValue("time", Timestamp.valueOf(time));
        return 1 == template.update(sql, map);
    }

    public boolean closeChat(Long idChat) {
        String sql = "UPDATE chat SET status = 'CLOSED' WHERE id = :id";
        var map = new MapSqlParameterSource()
                .addValue("id", idChat);
        return 1 == template.update(sql, map);
    }

    public ChatsEntity getAllChatsByIdProfile(UUID idProfile) {
        String sql = """
                    SELECT * FROM chat
                    WHERE id_profile = :idProfile OR id_consultant = :idProfile
                    ORDER BY last_message_at DESC NULLS LAST
                """;
        var map = new MapSqlParameterSource()
                .addValue("idProfile", idProfile);
        return new ChatsEntity(template.query(sql, map, rowMapper));
    }

    public boolean deleteChat(Long id){
        String sql = "DELETE FROM chat WHERE id=:id";
        var map = new MapSqlParameterSource()
                .addValue("id", id);
        return 1 == template.update(sql, map);
    }
}
