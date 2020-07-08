package com.linyw.akkachat.id;

import lombok.Getter;

import java.util.Objects;

public class RoomId implements EntityId{

    @Getter
    private final String id;

    public RoomId(String roomId){
        this.id = roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomId roomId = (RoomId) o;
        return Objects.equals(id, roomId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
