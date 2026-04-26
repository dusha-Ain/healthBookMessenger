package com.rent.healthbookmessenger.chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatEntity{
        private Long id;

        private UUID idProfile;

        private UUID idConsultant;

        private String status;         // WAITING_FOR_CONSULTANT, ACTIVE, CLOSED

        private LocalDateTime createdAt;

        private LocalDateTime lastMessageAt;

        public ChatEntity() {}

        public ChatEntity(Long id,
                          UUID idProfile,
                          UUID idConsultant,
                          String status,
                          LocalDateTime createdAt,
                          LocalDateTime lastMessageAt) {
                this.id = id;
                this.idProfile = idProfile;
                this.idConsultant = idConsultant;
                this.status = status;
                this.createdAt = createdAt;
                this.lastMessageAt = lastMessageAt;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public UUID getIdProfile() {
                return idProfile;
        }

        public void setIdProfile(UUID idProfile) {
                this.idProfile = idProfile;
        }

        public UUID getIdConsultant() {
                return idConsultant;
        }

        public void setIdConsultant(UUID idConsultant) {
                this.idConsultant = idConsultant;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public LocalDateTime getLastMessageAt() {
                return lastMessageAt;
        }

        public void setLastMessageAt(LocalDateTime lastMessageAt) {
                this.lastMessageAt = lastMessageAt;
        }

        @Override
        public String toString() {
                return "ChatEntity{" +
                        "id=" + id +
                        ", idProfile=" + idProfile +
                        ", idConsultant=" + idConsultant +
                        ", status='" + status + '\'' +
                        ", createdAt=" + createdAt +
                        ", lastMessageAt=" + lastMessageAt +
                        '}';
        }
}