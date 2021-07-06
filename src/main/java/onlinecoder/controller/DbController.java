package onlinecoder.controller;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import onlinecoder.helper.ContainerHelper;
import onlinecoder.helper.DbContainerHelper;

@Controller
public class DbController {
  @Value("${local.java.filepath}")
  private String filepath;

  @RequestMapping("/db")
  public String index(HttpServletRequest request, ModelMap modelMap) {
    modelMap.addAttribute("main",
          "public class Main {\n" +
          "  public static void main(String[] args) {\n" +
          "     DeptDAO deptDao = new DeptDAO();\n" +
          "     Dept dept = deptDao.searchByDeptno(2);\n" +
          "     System.out.println(String.format(\"deptno: %d, dname: %s, loc: %s\", dept.getDeptno(), dept.getDname(), dept.getLoc()));\n" +
          "  }\n" +
          "}");

    modelMap.addAttribute("dept",
"public class Dept {\n" +
          "  private int deptno;\n" +
          "  private String dname;\n" +
          "  private String loc;\n" +
          "\n" +
          "  public int getDeptno() {\n" +
          "    return this.deptno;\n" +
          "  }\n" +
          "  public void setDeptno(int deptno) {\n" +
          "    this.deptno = deptno;\n" +
          "  }\n" +
          "  public String getDname() {\n" +
          "    return this.dname;\n" +
          "  }\n" +
          "  public void setDname(String dname) {\n" +
          "    this.dname = dname;\n" +
          "  }\n" +
          "  public String getLoc() {\n" +
          "    return this.loc;\n" +
          "  }\n" +
          "  public void setLoc(String loc) {\n" +
          "    this.loc = loc;\n" +
          "  }\n" +
          "}");

    modelMap.addAttribute("deptdao",
            "import java.sql.Connection;\n" +
                    "import java.sql.DriverManager;\n" +
                    "import java.sql.ResultSet;\n" +
                    "import java.sql.SQLException;\n" +
                    "import java.sql.Statement;\n" +
                    "\n" +
                    "public class DeptDAO {\n" +
                    "  private static final String url = \"jdbc:postgresql://localhost:5432/test_db\";\n" +
                    "  private static final String user = \"postgres\";\n" +
                    "  private static final String password = \"postgres\";\n" +
                    "\n" +
                    "  public Dept searchByDeptno(int deptno) {\n" +
                    "     Connection conn = null;\n" +
                    "     Statement stmt = null;\n" +
                    "     ResultSet rset = null;\n" +
                    "     Dept dept = new Dept();\n" +
                    "\n" +
                    "     try {\n" +
                    "       conn = DriverManager.getConnection(url, user, password);\n" +
                    "       conn.setAutoCommit(false);\n" +
                    "       stmt = conn.createStatement();\n" +
                    "       String sql = \"SELECT * FROM dept WHERE deptno = \" + deptno;\n" +
                    "       rset = stmt.executeQuery(sql);\n" +
                    "       while(rset.next()){\n" +
                    "         dept.setDeptno(rset.getInt(1));\n" +
                    "         dept.setDname(rset.getString(2));\n" +
                    "         dept.setLoc(rset.getString(3));\n" +
                    "       }\n" +
                    "     } catch (Exception e){\n" +
                    "       e.printStackTrace();\n" +
                    "     } finally {\n" +
                    "       try {\n" +
                    "         if(rset != null)rset.close();\n" +
                    "         if(stmt != null)stmt.close();\n" +
                    "         if(conn != null)conn.close();\n" +
                    "       } catch (SQLException e){\n" +
                    "         e.printStackTrace();\n" +
                    "       }\n" +
                    "     }\n" +
                    "     return dept;\n" +
                    "  }\n" +
                    "}");
    return "db";
  }

  @RequestMapping("/db/compile")
  public String compile(HttpServletRequest request, ModelMap modelMap) {
    String result = "";
    String main = request.getParameter("main");
    String dept = request.getParameter("dept");
    String deptdao = request.getParameter("deptdao");

    try {
      FileWriter file = new FileWriter(filepath + "/Main.java");
      PrintWriter pw = new PrintWriter(new BufferedWriter(file));
      pw.println(main);
      pw.close();

      FileWriter file2 = new FileWriter(filepath + "/Dept.java");
      PrintWriter pw2 = new PrintWriter(new BufferedWriter(file2));
      pw2.println(dept);
      pw2.close();

      FileWriter file3 = new FileWriter(filepath + "/DeptDAO.java");
      PrintWriter pw3 = new PrintWriter(new BufferedWriter(file3));
      pw3.println(deptdao);
      pw3.close();

      ContainerHelper containerHelper = new DbContainerHelper();
      containerHelper.startContainer();
      containerHelper.copyToContainer(filepath);

      // postgres立ち上げ待機
      Thread.sleep(3000);

      result = containerHelper.execCode("Main");

      containerHelper.stopContainer(1);
      containerHelper.removeContainer();
    } catch (IOException | InterruptedException | DockerException | DockerCertificateException e) {
      result = "コンパイルに失敗しました。\n" + e.getMessage();
    }
    modelMap.addAttribute("main", main);
    modelMap.addAttribute("dept", dept);
    modelMap.addAttribute("deptdao", deptdao);
    modelMap.addAttribute("result", result);
    return "db";
  }
}
