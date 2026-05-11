package Server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AircraftWarServer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        String username = req.getParameter("username");
        String userpwd = req.getParameter("password");
        String request = req.getParameter("requestGoal");
        AccountHandle userAccountHandle = new AccountHandle(username,userpwd);

        String result = "";
        boolean userAccountExist = userAccountHandle.IsAccountExisting();
        boolean userPasswordRight = userAccountHandle.IsPasswordRight();
        if (Objects.equals(request, "login")) {
            if (!userAccountExist) {
                result = "账户不存在";
            } else if (!userPasswordRight) {
                result = "密码不正确";
            } else {
                result = "登录成功";
            }
        } else if (Objects.equals(request, "signup")) {
            if (userAccountExist) {
                result = "账户已存在";
            } else {
                userAccountHandle.saveAccount();
                result = "注册成功";
            }
        }

        out.write(result);
        out.flush();
        out.close();
        System.out.println(result);
    }
}