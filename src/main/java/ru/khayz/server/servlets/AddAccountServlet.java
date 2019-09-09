package ru.khayz.server.servlets;

import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.AddAccountToDbCmd;
import ru.khayz.ms.cmd.server.AddAccountToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddAccountServlet extends CommonServlet {
    private static final String ERROR_TEMPLATE = "AddAccountResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "AddAccountResponse.xml";

    public AddAccountServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        AddAccountToServerCmd respCmd = (AddAccountToServerCmd) cmd;

        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            varsMap.put("id", respCmd.getAccId());

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
        return "/addAccount";
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");
            long clientId;
            try {
                String clientIdString = req.getParameter("client_id");
                if (clientIdString == null || clientIdString.isEmpty()) {
                    ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, "Client Id should be set");
                    return;
                }
                clientId = Long.valueOf(clientIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, "Invalid client_id");
                return;
            }

            AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Runnable sendReq = () -> {
                AddAccountToDbCmd cmd = new AddAccountToDbCmd(address, dbAddress, context, clientId);
                cs.sendCmd(cmd);
            };
            context.start(sendReq);
        } catch (Exception e) {
            e.printStackTrace();
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
