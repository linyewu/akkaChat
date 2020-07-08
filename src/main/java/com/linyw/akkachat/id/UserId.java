package com.linyw.akkachat.id;

import lombok.Getter;

import java.util.Objects;

public class UserId implements EntityId{

    @Getter
    private final String id;

    public UserId(String userId){
        this.id = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(id, userId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
