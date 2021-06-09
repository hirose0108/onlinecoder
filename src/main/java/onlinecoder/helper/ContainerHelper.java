package onlinecoder.helper;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ExecCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import onlinecoder.utils.PortUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ContainerHelper {
  private final DockerClient docker;
  private final String containerId;
  private final ContainerInfo containerInfo;

  public ContainerHelper() throws DockerCertificateException, IOException, DockerException, InterruptedException {
    this.docker = DefaultDockerClient.fromEnv().build();

    int portNum = PortUtils.findRandomAvailablePort();
    String[] ports = { String.valueOf(portNum) };
    Map<String, List<PortBinding>> portBindings = new HashMap<>();
    for (String port : ports) {
      List<PortBinding> hostPorts = new ArrayList<>();
      hostPorts.add(PortBinding.of("0.0.0.0", port));
      portBindings.put(port, hostPorts);
    }

    HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();
    ContainerConfig containerConfig = ContainerConfig.builder()
            .hostConfig(hostConfig)
            .image("adoptopenjdk/openjdk8:alpine").exposedPorts(ports)
            .cmd("sh", "-c", "while :; do sleep 1; done")
            .build();

    ContainerCreation creation = docker.createContainer(containerConfig);
    this.containerId = creation.id();
    this.containerInfo = docker.inspectContainer(this.containerId);
  }

  public void startContainer() throws DockerException, InterruptedException {
    this.docker.startContainer(this.containerId);
  }

  public void stopContainer(int secondsToWaitBeforeKilling) throws DockerException, InterruptedException {
    this.docker.stopContainer(this.containerId, secondsToWaitBeforeKilling);
  }

  public void removeContainer() throws DockerException, InterruptedException {
    this.docker.removeContainer(this.containerId);
  }

  public String execCode(String className) throws DockerException, InterruptedException {
    String result = execCompile(className);
    if (StringUtils.isBlank(result)) {
      result = execCompiledCode(className);
    }
    return result;
  }

  public String execCompile(String className) throws DockerException, InterruptedException {
    String execCommand = String.format("javac /tmp/%s.java", className);
    return this.execInContainer(execCommand);
  }

  public String execCompiledCode(String className) throws DockerException, InterruptedException {
    String execCommand = String.format("java -classpath /tmp %s", className);
    return this.execInContainer(execCommand);
  }

  public void copyToContainer(String path) throws InterruptedException, DockerException, IOException {
    this.docker.copyToContainer(new File(path).toPath(), this.containerId, "/tmp");
  }


  private String execInContainer(String execCommand) throws DockerException, InterruptedException {
    final String[] command = {"sh", "-c", execCommand};
    final ExecCreation execCreation = docker.execCreate(
            containerId, command, DockerClient.ExecCreateParam.attachStdout(),
            DockerClient.ExecCreateParam.attachStderr());

    LogStream logStream = docker.execStart(execCreation.id());
    StringBuilder builder = new StringBuilder();
    while (logStream.hasNext()) {
      final String log = UTF_8.decode(logStream.next().content()).toString();
      builder.append(log);
    }
    return builder.toString();
  }
}
