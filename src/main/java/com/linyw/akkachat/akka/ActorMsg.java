package com.linyw.akkachat.akka;


public interface ActorMsg {
    /**
     * 消息ID，在整个消息处理中，消息ID保持不变
     * @return
     */
    String getMsgId();

    /**
     * 消息类型
     * @return
     */
    MsgType getMsgType();
}
