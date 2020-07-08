package com.linyw.akkachat.akka;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractContextActor extends AbstractActor{

    protected final ActorSystemContext actorSystemContext;

    public AbstractContextActor(ActorSystemContext actorSystemContext) {
        this.actorSystemContext = actorSystemContext;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(ActorMsg.class, msg -> {
                    if(!process(msg)){
                        log.warn("unknown message: {}!", msg);
                    }
                })
                .match(Terminated.class, this::process)
                .matchAny(msg -> log.warn("unknown message: {}",msg)).build();

    }

    protected void process(Terminated terminated){

    }
    /**
     * 处理消息
     * @param msg ActorMsg消息
     * @return
     */
    protected abstract boolean process(ActorMsg msg);
}
