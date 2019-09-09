package ru.khayz.server.servlets;

import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.SendClientToClientToDbCmd;
import ru.khayz.ms.cmd.server.SendClientToClientToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendClientToClientServlet extends CommonServlet {
    private static final String ERROR_TEMPLATE = "SendClientToClientResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "SendClientToClientResponse.xml";

    public SendClientToClientServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        SendClientToClientToServerCmd respCmd = (SendClientToClientToServerCmd) cmd;
        String message = respCmd.getMessage();
        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            varsMap.put("message", message);
            try {
                ServletUtils.createResponse(resp, SUCCESS_TEMPLATE, varsMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            varsMap.put("message", respCmd.getMessage());
            try {
                ServletUtils.createResponse(resp, ERROR_TEMPLATE, varsMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        context.complete();
    }

    @Override
    public String getUrl() {
        return "/sendClientToClient";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");
            long idFrom;
            long idTo;
            long amount;
            try {
                idFrom = Long.valueOf(req.getParameter("id_from"));
                idTo = Long.valueOf(req.getParameter("id_to"));
                amount = Long.valueOf(req.getParameter("amount"));
                if (idFrom <= 0 || idTo <= 0) {
                    ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, "Invalid client id");
                }
            } catch (NumberFormatException e) {
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, "Invalid input parameter " + e.getMessage());
                return;
            }

            final AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Runnable sendReq = () -> {
                SendClientToClientToDbCmd cmd = new SendClientToClientToDbCmd(address, dbAddress, context, idFrom, idTo, amount);
                cs.sendCmd(cmd);
            };
            context.start(sendReq);
        } catch (Exception e) {
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
