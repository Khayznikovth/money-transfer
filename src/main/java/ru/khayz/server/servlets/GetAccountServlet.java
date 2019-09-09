package ru.khayz.server.servlets;

import ru.khayz.db.model.Account;
import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.GetAccountToDbCmd;
import ru.khayz.ms.cmd.server.GetAccountToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetAccountServlet extends CommonServlet {
    private static final String ERROR_TEMPLATE = "GetAccountResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "GetAccountResponse.xml";

    public GetAccountServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        GetAccountToServerCmd respCmd = (GetAccountToServerCmd) cmd;
        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            Account account = respCmd.getAccount();
            varsMap.put("id", account.getId());
            varsMap.put("client_id", account.getClientId());
            varsMap.put("amount", account.getAmount());
            varsMap.put("account_number", account.getAccountNumber());
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
        return "/getAccount";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");
            long id;
            try {
                id = Long.valueOf(req.getParameter("id"));
            } catch (NumberFormatException e) {
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, e.getMessage());
                return;
            }

            final AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Runnable sendReq = () -> {
                GetAccountToDbCmd cmd = new GetAccountToDbCmd(address, dbAddress, context, id);
                cs.sendCmd(cmd);
            };
            context.start(sendReq);
        } catch (Exception e) {
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
