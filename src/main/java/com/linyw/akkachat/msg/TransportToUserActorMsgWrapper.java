package com.linyw.akkachat.msg;

import com.linyw.akkachat.akka.ActorMsg;
import com.linyw.akkachat.akka.MsgType;
import com.linyw.akkachat.id.RoomId;
import com.linyw.akkachat.id.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class TransportToUserActorMsgWrapper implements ActorMsg, RoomAwareMsg, Serializable {
    private final String msgId;
    private final RoomId roomId;
    private final UserId userId;
    private final String msg;

    public TransportToUserActorMsgWrapper(String msgId, TransportToUserActorMsg msg){
        this.msgId = msgId;
        this.roomId = new RoomId(msg.getRoomId());
        this.userId = new UserId(msg.getUserId());
        this.msg = msg.getMsg();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TALK_MAG;
    }
}
