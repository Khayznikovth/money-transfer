package ru.khayz.server.servlets;

import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.CheckAmountToDbCmd;
import ru.khayz.ms.cmd.server.CheckAmountToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CheckAmountServlet extends CommonServlet {
    private static final String ERROR_TEMPLATE = "CheckAmountResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "CheckAmountResponse.xml";

    public CheckAmountServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        CheckAmountToServerCmd respCmd = (CheckAmountToServerCmd) cmd;
        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            varsMap.put("enough", String.valueOf(respCmd.isEnough()));
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
        return "/checkAmount";
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");
            long id;
            long amount;
            try {
                id = Long.valueOf(req.getParameter("id"));
                amount = Long.valueOf(req.getParameter("amount"));
            } catch (NumberFormatException e) {
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, e.getMessage());
                return;
            }

            final AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Runnable sendReq = () -> {
                CheckAmountToDbCmd cmd = new CheckAmountToDbCmd(address, dbAddress, context, id, amount);
                cs.sendCmd(cmd);
            };
            context.start(sendReq);
        } catch (Exception e) {
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
