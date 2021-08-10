package onlinecoder.controller;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import onlinecoder.helper.BasicContainerHelper;
import onlinecoder.helper.ContainerHelper;

@Controller
public class BasicController {
  @Value("${local.java.filepath}")
  private String filepath;

  @RequestMapping("/basic")
  public String index(HttpServletRequest request, ModelMap modelMap) {
    modelMap.addAttribute("code", "public class Test {\n" +
            "  public static void main(String[] args) {\n" +
            "    System.out.println(\"this is test!\");\n" +
            "  }\n" +
            "}");

    return "basic";
  }

  @RequestMapping("/basic/compile")
  public String compile(HttpServletRequest request, ModelMap modelMap) {
    String result = "";
    String code = request.getParameter("code");
    String stdin = request.getParameter("stdin");

    try {
      Files.write(Paths.get(filepath, "Test.java"), code.getBytes());
      Files.write(Paths.get(filepath, "stdin"), stdin.getBytes());

      ContainerHelper containerHelper = new BasicContainerHelper();
      containerHelper.startContainer();
      containerHelper.copyToContainer(filepath);

      result = containerHelper.execCode("Test");

      containerHelper.stopContainer(1);
      containerHelper.removeContainer();
    } catch (IOException | InterruptedException | DockerException | DockerCertificateException e) {
      result = "コンパイルに失敗しました。\n" + e.getMessage();
    }
    modelMap.addAttribute("code", code);
    modelMap.addAttribute("stdin", stdin);
    modelMap.addAttribute("result", result);
    return "basic";
  }
}
