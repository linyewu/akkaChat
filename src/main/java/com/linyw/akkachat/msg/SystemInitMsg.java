package com.linyw.akkachat.msg;

import com.linyw.akkachat.akka.ActorMsg;
import com.linyw.akkachat.akka.MsgType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemInitMsg implements ActorMsg {
    private final String msgId;


    @Override
    public MsgType getMsgType() {
        return MsgType.SYSTEM_INIT;
    }
}
