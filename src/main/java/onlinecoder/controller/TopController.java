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

@Controller
public class TopController {

  @Value("${local.java.filepath}")
  private String filepath;

  @RequestMapping("/")
  public String index(HttpServletRequest request, ModelMap modelMap) {
    modelMap.addAttribute("code", "public class Test {\n" +
            "  public static void main(String[] args) {\n" +
            "    System.out.println(\"this is test!\");\n" +
            "  }\n" +
            "}");

    return "top";
  }

  @RequestMapping("/compile")
  public String compile(HttpServletRequest request, ModelMap modelMap) {
    String result = "";
    String code = request.getParameter("code");

    try {
      FileWriter file = new FileWriter(filepath + "/Test.java");
      PrintWriter pw = new PrintWriter(new BufferedWriter(file));

      pw.println(code);
      pw.close();

      ContainerHelper containerHelper = new ContainerHelper();
      containerHelper.startContainer();
      containerHelper.copyToContainer(filepath);

      result = containerHelper.execCode("Test");

      containerHelper.stopContainer(1);
      containerHelper.removeContainer();
    } catch (IOException | InterruptedException | DockerException | DockerCertificateException e) {
      result = "コンパイルに失敗しました。\n" + e.getMessage();
    }
    modelMap.addAttribute("code", code);
    modelMap.addAttribute("result", result);
    return "top";
  }
}
