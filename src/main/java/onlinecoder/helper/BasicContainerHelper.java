package onlinecoder.helper;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;

import java.io.IOException;

public class BasicContainerHelper extends ContainerHelper {
  public BasicContainerHelper() throws InterruptedException, DockerException, DockerCertificateException, IOException {
    super();
  }

  @Override
  public ContainerConfig createConfig() {
    return ContainerConfig.builder()
            .hostConfig(hostConfig)
            .image("adoptopenjdk/openjdk8:alpine").exposedPorts(ports)
            .cmd("sh", "-c", "while :; do sleep 1; done")
            .build();
  }
}
