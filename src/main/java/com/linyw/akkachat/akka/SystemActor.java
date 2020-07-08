package com.linyw.akkachat.akka;


import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.linyw.akkachat.id.RoomId;
import com.linyw.akkachat.msg.TransportToUserActorMsgWrapper;
import com.linyw.akkachat.msg.RoomAwareMsg;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SystemActor extends AbstractContextActor {

    private boolean ruleChainsInitialized = false;

    private BiMap<RoomId, ActorRef> roomActors;

    private ActorRef logActor;

    private SystemActor(ActorSystemContext actorSystemContext) {
        super(actorSystemContext);
        this.roomActors = HashBiMap.create();
    }

    @Override
    protected boolean process(ActorMsg msg) {
        switch (msg.getMsgType()) {
            case SYSTEM_INIT:
                init();
                break;
            case TALK_MAG:
                onToUserActorMsg((TransportToUserActorMsgWrapper) msg);
                break;
            default:
                return false;
        }
        return true;
    }

    private void init(){
        if (!ruleChainsInitialized) {
            initRoomActors();
            ruleChainsInitialized = true;
        }
    }


    private void initRoomActors() {
        if(actorSystemContext.isConsumerComponentsInitEnabled()){
            List<Room> listRoom = actorSystemContext.getListRoom();
            for(Room room : listRoom){
                getOrCreateRoomActor(new RoomId(room.getId()));
            }
        }
    }

    public void onToUserActorMsg(RoomAwareMsg msg){
        getOrCreateRoomActor(msg.getRoomId()).tell(msg,ActorRef.noSender());

    }



    private ActorRef getOrCreateRoomActor(RoomId roomId) {

        return roomActors.computeIfAbsent(roomId, k -> {
            log.debug("[{}] Creating room actor.", roomId.getId());
            ActorRef roomActor = context().actorOf(RoomActor.props(actorSystemContext,roomId)
                    .withDispatcher(ActorSystemContext.CORE_DISPATCHER_NAME), roomId.getId());
            context().watch(roomActor);
            log.debug("[{}] Created room actor: {}.", roomId.getId(), roomActor);
            return roomActor;
        });
    }

    public static Props props(ActorSystemContext actorSystemContext){
        return Props.create(SystemActor.class, actorSystemContext);
    }


    @Override
    protected void process(Terminated terminated) {
        ActorRef actor = terminated.actor();
        if(actor instanceof LocalActorRef){
            boolean removed = roomActors.inverse().remove(actor) != null;

            if(removed){
                log.debug("[{}] Removed actor:", terminated);
            }else{
                log.warn("[{}] Removed actor was not found in the device map!");
            }
        }
    }

    /**
     * 容错机制
     * @return
     */
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    /**
     * 尝试3次，每次间隔1分钟
     */
    private SupervisorStrategy strategy =
            new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), DeciderBuilder
                    .match(RuntimeException.class,e->{
                        log.warn("系统运行时出现未知异常，系统重启中", e);
                        return SupervisorStrategy.restart();
                    }).match(Exception.class, e->{
                        log.error("系统错误，系统已停止",e);
                        return SupervisorStrategy.stop();
                    }).build());


}
