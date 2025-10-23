package ru.aialchemy.agent.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRq(
        @JsonProperty("userQuestion")
        String userQuestion
) {
        @Override
        public String toString() {
                return userQuestion;
        }
}
