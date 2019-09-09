package ru.khayz.server.servlets;

import ru.khayz.db.model.Account;
import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.GetClientAccountsToDbCmd;
import ru.khayz.ms.cmd.server.GetClientAccountsToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetClientAccountsServlet extends CommonServlet {
    private static final String ERROR_TEMPLATE = "GetClientAccountsResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "GetClientAccountsResponse.xml";

    public GetClientAccountsServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        GetClientAccountsToServerCmd respCmd = (GetClientAccountsToServerCmd) cmd;
        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            List<String> accounts = respCmd.getAccounts().stream().map(Account::toString).collect(Collectors.toList());
            varsMap.put("accounts", accounts);
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
        return "/getClientAccounts";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");
            long id;
            try {
                id = Long.valueOf(req.getParameter("client_id"));
            } catch (NumberFormatException e) {
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, e.getMessage());
                return;
            }

            final AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Runnable sendReq = () -> {
                GetClientAccountsToDbCmd cmd = new GetClientAccountsToDbCmd(address, dbAddress, context, id);
                cs.sendCmd(cmd);
            };
            context.start(sendReq);
        } catch (Exception e) {
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
