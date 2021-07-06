package onlinecoder.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TopController {

  @Value("${local.java.filepath}")
  private String filepath;

  @RequestMapping("/")
  public String index(HttpServletRequest request, ModelMap modelMap) {
    return "top";
  }
}
