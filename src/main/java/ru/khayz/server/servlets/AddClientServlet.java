package ru.khayz.server.servlets;

import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.cmd.ToServerCmd;
import ru.khayz.ms.cmd.db.AddClientToDbCmd;
import ru.khayz.ms.cmd.server.AddClientToServerCmd;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


@WebServlet(urlPatterns="/addClient", asyncSupported=true)
public class AddClientServlet extends CommonServlet {
    private static final DateFormat XSD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String ERROR_TEMPLATE = "AddClientResponseError.xml";
    private static final String SUCCESS_TEMPLATE = "AddClientResponse.xml";

    public AddClientServlet(CmdSystem cs, Address dbAddress) {
        super(cs, dbAddress);
    }

    @Override
    public void processResponse(ToServerCmd cmd) {
        AsyncContext context = cmd.getContext();
        HttpServletResponse resp = (HttpServletResponse) context.getResponse();
        AddClientToServerCmd respCmd = (AddClientToServerCmd)cmd;
        Map<String, Object> varsMap = new HashMap<>();
        if (ToServerCmd.ResultCode.SUCCESS.equals(respCmd.getResult())) {
            varsMap.put("id", respCmd.getId());
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
        cmd.getContext().complete();
    }

    @Override
    public String getUrl() {
        return "/addClient";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/html;charset=utf-8");

            //get params
            String name;
            String phone;
            Date birthDate = null;

            try {
                name = req.getParameter("name");
                if (name == null) {
                    ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, "Client name should be set");
                    return;
                }
                phone = req.getParameter("phone");
                String birthDateString = req.getParameter("birth_date");
                if (birthDateString != null && !birthDateString.isEmpty()) {
                    birthDate = new Date(XSD_DATE_FORMAT.parse(birthDateString).getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
                ServletUtils.createErrorMessage(resp, ERROR_TEMPLATE, e.getMessage());
                return;
            }

            // send request to DB service
            final AsyncContext context = req.startAsync();
            context.setTimeout(3000);
            Date finalBirthDate = birthDate;
            Runnable sendReq = () -> {
                AddClientToDbCmd cmd = new AddClientToDbCmd(address, dbAddress, context, name, phone, finalBirthDate);
                cs.sendCmd(cmd);
            };
            context.start(sendReq);
        } catch (Exception e) {
            ServletUtils.createInternalErrorMessage(resp);
        }
    }
}
