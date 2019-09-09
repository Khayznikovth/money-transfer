package ru.khayz.server.servlets;

import ru.khayz.db.model.Client;
import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.GetClientToDbCmd;
import ru.khayz.ms.cmd.server.GetClientToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetClientServlet extends CommonServlet {
    private static final String ERROR_TEMPLATE = "GetClientResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "GetClientResponse.xml";

    public GetClientServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        GetClientToServerCmd respCmd = (GetClientToServerCmd) cmd;
        Client client = respCmd.getClient();
        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            varsMap.put("id", client.getId());
            varsMap.put("name", client.getName());
            varsMap.put("phone", client.getPhone());
            varsMap.put("birth_date", client.getBirthDate());
            varsMap.put("preffered_account", client.getPrefferedAccount());
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
        return "/getClient";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");
            long id;
            try {
                id = Long.valueOf(req.getParameter("id"));
            } catch (NumberFormatException e) {
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, "Invalid Id " + e.getMessage());
                return;
            }

            final AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Runnable sendReq = () -> {
                GetClientToDbCmd cmd = new GetClientToDbCmd(address, dbAddress, context, id);
                cs.sendCmd(cmd);

            };
            context.start(sendReq);
        } catch (Exception e) {
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
