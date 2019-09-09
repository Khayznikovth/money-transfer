package ru.khayz.ms.cmd.server;

import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;

public class SendAccountToAccountToServerCmd extends ToServerCmd {
    public SendAccountToAccountToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, String message) {
        super(from, to, context, result, message);
    }
}
