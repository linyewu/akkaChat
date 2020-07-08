package com.linyw.akkachat.msg;

import akka.actor.ActorRef;
import com.linyw.akkachat.akka.ActorMsg;
import com.linyw.akkachat.akka.MsgType;
import lombok.Data;

@Data
public class TalkMsg implements ActorMsg {

    private final String msgId;
    private final ActorRef sender;
    private final String msg;

    @Override
    public MsgType getMsgType() {
        return MsgType.TALK_MAG;
    }
}
