package com.linyw.akkachat.akka;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.linyw.akkachat.id.RoomId;
import com.linyw.akkachat.id.UserId;
import com.linyw.akkachat.msg.TransportToUserActorMsgWrapper;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.List;

@Slf4j
public class RoomActor extends AbstractContextActor{
    private final RoomId roomId;
    protected ActorSystemContext actorSystemContext;
    private BiMap<UserId, ActorRef> userActors;


    private RoomActor(ActorSystemContext actorSystemContext, RoomId roomId) {
        super(actorSystemContext);
        this.actorSystemContext = actorSystemContext;
        this.roomId = roomId;
        userActors = HashBiMap.create();
    }
    public static Props props(ActorSystemContext actorSystemContext, RoomId roomId){
        return Props.create(RoomActor.class,()-> new RoomActor(actorSystemContext, roomId));
    }


    @Override
    public void preStart() {
        log.debug("[{}] Starting room actor.", roomId.getId());
        try {
            initRuleChains();
            log.debug("[{}] Consumer room started.", roomId.getId());
        } catch (Exception e) {
            log.error("[{}] Unknown failure", roomId.getId(), e);
        }

    }

    public void initRuleChains(){
//        此处优化为系统初始化时只查询已经启动的规则
        List<User> listUser = actorSystemContext.getListUser();
        for(User user : listUser){
            getOrCreateActor(new UserId(user.getId()));
        }
    }

    public ActorRef getOrCreateActor(UserId userId) {
        return userActors.computeIfAbsent(userId, k -> {
            log.debug("[{}] Creating entity actor.", userId.getId());
            ActorRef ref = context().actorOf(UserActor.props(actorSystemContext,userId)
                    .withDispatcher(ActorSystemContext.ROOM_RULE_DISPATCHER_NAME), userId.getId());
            context().watch(ref);
            log.debug("[{}] Created entity actor: {}.", userId.getId(), ref);
            return ref;
        });
    }

    @Override
    public void postStop(){
        log.info("[{}] Consumer actor stopped.", roomId);

    }

    @Override
    protected boolean process(ActorMsg msg) {
        switch (msg.getMsgType()){
            case TALK_MAG:
                onToUserActorMsg((TransportToUserActorMsgWrapper) msg);
                break;
            default:
                return false;

        }

        return true;
    }

    public void onToUserActorMsg(TransportToUserActorMsgWrapper msg){
        log.info("roomId: {} userId:{} said: {}",msg.getRoomId().getId(),msg.getUserId().getId(),msg.getMsg());
        getOrCreateDeviceActor(msg.getUserId()).tell(msg, ActorRef.noSender());
    }

    private ActorRef getOrCreateDeviceActor(UserId userId) {
        return userActors.computeIfAbsent(userId, k -> {
            log.debug("[{}][{}] Creating device actor.", userId.getId(), roomId.getId());
            ActorRef userActor = context().actorOf(UserActor.props(actorSystemContext,userId)
                            .withDispatcher(ActorSystemContext.CORE_DISPATCHER_NAME)
                    , userId.getId());
            context().watch(userActor);
            log.debug("[{}][{}] Created device actor: {}.", roomId.getId(), userId.getId(), userActor);
            return userActor;
        });
    }


    @Override
    protected void process(Terminated terminated) {
        ActorRef actor = terminated.actor();
        UserId remove = userActors.inverse().remove(actor);
        if(remove!=null){
            if(actor instanceof  LocalActorRef){
                UserId remove1 = userActors.inverse().remove(actor);
                if(remove1!=null){
                    log.debug("[{}] Removed actor:", terminated);
                }else{
                    log.warn("[{}] Removed actor was not found in the device map!");
                }
            }
        }else{
            log.warn("[{}] Removed actor was not found in the entityActors map!");
        }

    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    private final SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("1 minute"), DeciderBuilder
        .match(ActorInitializationException.class, e->{
            log.error("Actor启动失败: {}", e.getLocalizedMessage());

            return SupervisorStrategy.stop();
        })
        .matchAny(e->SupervisorStrategy.resume())
        .build());

}
