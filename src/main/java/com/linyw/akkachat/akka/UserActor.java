package com.linyw.akkachat.akka;

import akka.actor.ActorInitializationException;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import com.linyw.akkachat.id.UserId;
import com.linyw.akkachat.msg.TransportToUserActorMsgWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserActor extends AbstractContextActor{
    private UserId userId;
    private UserActor(ActorSystemContext actorSystemContext, UserId userId) {
        super(actorSystemContext);
        this.userId = userId;
    }

    public static Props props(ActorSystemContext actorSystemContext,UserId userId){
        return Props.create(UserActor.class,()-> new UserActor(actorSystemContext,userId));
    }

    @Override
    protected boolean process(ActorMsg msg) {
        switch (msg.getMsgType()){
            case TALK_MAG:
                getUserActorMsg((TransportToUserActorMsgWrapper) msg);
                break;
            default:
                return false;
        }
        return true;
    }

    public void getUserActorMsg(TransportToUserActorMsgWrapper msg){
//        log.info("userId {} roomId {} msg {}",msg.getUserId().getId(),msg.getRoomId().getId(),msg.getMsg());
    }



    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    private final SupervisorStrategy strategy = new OneForOneStrategy(DeciderBuilder
            .match(ActorInitializationException.class, e->
                {
                    return SupervisorStrategy.stop();
                }
            )
            .match(Exception.class, e-> SupervisorStrategy.escalate())
            .build());
}
