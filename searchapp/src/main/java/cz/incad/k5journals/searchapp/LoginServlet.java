/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.k5journals.searchapp;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author alberto
 */
public class LoginServlet extends HttpServlet {

  public static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
  public static final String ACTION_NAME = "action";

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param req servlet request
   * @param resp servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    try {

      String actionNameParam = req.getParameter(ACTION_NAME);
      if (actionNameParam != null) {
        LoginServlet.Actions actionToDo = LoginServlet.Actions.valueOf(actionNameParam.toUpperCase());
        resp.setContentType("application/json;charset=UTF-8");
        actionToDo.doPerform(req, resp);
      } else {
        PrintWriter out = resp.getWriter();
        out.print("actionNameParam -> " + actionNameParam);
      }
    } catch (IOException e1) {
      LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.toString());
      PrintWriter out = resp.getWriter();
      out.print(e1.toString());
    } catch (SecurityException e1) {
      LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    } catch (Exception e1) {
      LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      PrintWriter out = resp.getWriter();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.toString());
      out.print(e1.toString());
    }
  }
  

  enum Actions {
    LOGIN {
      @Override
      void doPerform(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        PrintWriter out = resp.getWriter();
        JSONObject jo = new JSONObject();
        try {

          String user = req.getParameter("user");
          if (user != null) {
            
            if(LoginController.login(req, user, req.getParameter("pwd"), req.getParameter("ctx"))){
              jo.put("logged", true);
            }else{
              jo.put("logged", false);
              jo.put("error", "invalid user name or password");
            }
            
          } else {
            jo.put("logged", false);
            jo.put("error", "invalid user name or password");
          }

        } catch (Exception ex) {
          jo.put("logged", false);
          jo.put("error", ex.toString());
        }
        out.println(jo.toString());
        

      }
    },
    LOGOUT {
      @Override
      void doPerform(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        PrintWriter out = resp.getWriter();
        JSONObject jo = new JSONObject();
        try {
          req.getSession().removeAttribute("login");
          req.getSession().invalidate();
          jo.put("msg", "logged out");

        } catch (Exception ex) {
          jo.put("error", ex.toString());
        }
        out.println(jo.toString());
      }
    },
    TESTLOGIN {
      @Override
      void doPerform(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        PrintWriter out = resp.getWriter();
        JSONObject jo = new JSONObject();
        try {
          jo = (JSONObject) req.getSession().getAttribute("user");
          if(jo == null){
            jo = new JSONObject();
            jo.put("error", "nologged");
          }

        } catch (Exception ex) {
          jo.put("error", ex.toString());
        }
        if (req.getParameter("json.wrf") != null) {
          out.println(req.getParameter("json.wrf") + "(" + jo.toString(2) + ")");
        } else {
          out.println(jo.toString(2));
        }
      }
    };

    abstract void doPerform(HttpServletRequest req, HttpServletResponse resp) throws Exception;
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
