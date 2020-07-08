package com.linyw.akkachat.msg;

import lombok.Data;

@Data
public class TransportToUserActorMsg {
    private String roomId;
    private String userId;
    private String msg;
}
