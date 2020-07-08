package com.linyw.akkachat.controller;

import akka.actor.ActorRef;
import com.linyw.akkachat.akka.ActorSystemContext;
import com.linyw.akkachat.msg.TransportToUserActorMsg;
import com.linyw.akkachat.msg.TransportToUserActorMsgWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/room")
public class ChatController {

    @Autowired
    private ActorSystemContext context;

    @PostMapping("/chat")
    public void sendChat(@RequestBody TransportToUserActorMsg transportToDeviceActorMsg){
        log.info("进入聊天：{}",transportToDeviceActorMsg.toString());
        TransportToUserActorMsgWrapper transportToDeviceActorMsgWrapper = new TransportToUserActorMsgWrapper(UUID.randomUUID().toString(), transportToDeviceActorMsg);
        context.getSystemActor().tell(transportToDeviceActorMsgWrapper, ActorRef.noSender());
    }

    @GetMapping("/test")
    public void getTest(@RequestParam String name){
        log.info(name);
    }
}
