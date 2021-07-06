package onlinecoder.helper;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;

import java.io.IOException;

public class DbContainerHelper extends ContainerHelper {
  public DbContainerHelper() throws InterruptedException, DockerException, DockerCertificateException, IOException {
    super();
  }

  @Override
  public ContainerConfig createConfig() {
    return ContainerConfig.builder()
            .hostConfig(hostConfig)
            .networkDisabled(true)
            .image("onlinecoder-fordb:latest").exposedPorts(ports)
            .build();
  }

  @Override
  public String execCompile(String className) throws DockerException, InterruptedException {
    String execCommand = String.format("javac -classpath /tmp:postgresql-42.2.21.jar /tmp/%s.java", className);
    return super.execInContainer(execCommand);
  }

  @Override
  public String execCompiledCode(String className) throws DockerException, InterruptedException {
    String execCommand = String.format("java -classpath /tmp:postgresql-42.2.22.jar %s", className);
    return this.execInContainer(execCommand);
  }
}
