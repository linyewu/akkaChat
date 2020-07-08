package com.linyw.akkachat.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import com.linyw.akkachat.msg.SystemInitMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
public class ActorSystemContext {

    private static final String AKKA_CONF_FILE_NAME = "actor-system.conf";

    public static final String ROOM_RULE_DISPATCHER_NAME = "room-dispatcher";

    public static final String CORE_DISPATCHER_NAME = "core-dispatcher";

    private static final String ACTOR_SYSTEM_NAME = "Akka";

    public static final String APP_DISPATCHER_NAME = "app-dispatcher";

    private Config config;

    @Getter
    private ActorRef systemActor;

    @Value("${actors.consumer.create_components_on_init:true}")
    @Getter
    private boolean consumerComponentsInitEnabled;

    @Getter
    private ActorSystem actorSystem;

    public ActorSystemContext(){
        config = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME).withFallback(ConfigFactory.load());
    }

    public List<Room> getListRoom(){
        List<Room> list = new ArrayList<>();
        Room room1 = new Room("1","101");
        Room room2 = new Room("2","102");
        list.add(room1);
        list.add(room2);
        return list;
    }

    public List<User> getListUser(){
        List<User> list = new ArrayList<>();
        User user1 = new User("qq","aaa");
        User user2 = new User("ww","bbb");
        list.add(user1);
        list.add(user2);
        return list;
    }

    @PostConstruct
    public void initActorSystem(){
        log.info("Initializing Actor system. " );
        actorSystem = ActorSystem.create(ACTOR_SYSTEM_NAME, config);
        //这里进行actorSystemContext赋值
        systemActor = actorSystem.actorOf(SystemActor.props(this)
                .withDispatcher(APP_DISPATCHER_NAME),"systemActor");

        log.info("Actor system initialized. " );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("Received application ready event. Sending application init message to actor system");
        systemActor.tell(new SystemInitMsg(UUID.randomUUID().toString()), ActorRef.noSender());
    }


    @PreDestroy
    public void stopActorSystem(){

        Future<Terminated> status = actorSystem.terminate();
        try {
            Terminated terminated = Await.result(status, Duration.Inf());
            log.info("Actor system terminated: {}", terminated);
        } catch (Exception e) {
            log.error("Failed to terminate actor system.", e);
        }
    }

}
