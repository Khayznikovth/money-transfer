package ru.khayz.ms;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CmdSystem {
    private final Map<Address, ConcurrentLinkedQueue<Cmd>> cmds = new HashMap<>();

    public void sendCmd(Cmd command) {
        Queue<Cmd> cmdQueue = cmds.get(command.getTo());
        cmdQueue.add(command);
    }

    public void execForSubscriber(Subscriber subscr) {
        Queue<Cmd> cmdQueue = cmds.get(subscr.getAddress());
        if (!cmdQueue.isEmpty()) {
            Cmd command = cmdQueue.poll();
            command.exec(subscr);
        }
    }

    public void addQueue(Address address) {
        cmds.put(address, new ConcurrentLinkedQueue<>());
    }
}
