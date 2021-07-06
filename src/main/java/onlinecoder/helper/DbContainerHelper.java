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
}
