package com.linyw.akkachat.msg;

import com.linyw.akkachat.id.RoomId;

public interface RoomAwareMsg {
    RoomId getRoomId();
}
