package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import me.snowdrop.istio.api.model.DoneableIstioResource;
import me.snowdrop.istio.api.model.IstioResource;
import org.junit.Test;

public class SimpleTest {

  private static final String CRD_NAME = "gateways.networking.istio.io";
  private static final String RESOURCE_NAME = "greeting-gateway";

  @Test
  public void run() throws Exception {
    final OpenShiftClient client = new DefaultOpenShiftClient();
    final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    final CustomResourceDefinition customResourceDefinition =
        client.customResourceDefinitions().withName(CRD_NAME).get();
    assertThat(customResourceDefinition).isNotNull();

    final IstioResource istioResource =
        createIstioResource(client, objectMapper, customResourceDefinition);
    assertThat(istioResource).isNotNull();
    assertThat(retrieveIstioResourceFromApiServer(client, customResourceDefinition)).isNotNull();

    final boolean deleteResult =
        client
            .customResources(
                customResourceDefinition,
                IstioResource.class,
                KubernetesResourceList.class,
                DoneableIstioResource.class
            )
            .inNamespace(client.getNamespace())
            .delete(istioResource);
    assertThat(deleteResult).isTrue();
    assertThat(retrieveIstioResourceFromApiServer(client, customResourceDefinition)).isNull();
  }

  private IstioResource createIstioResource(OpenShiftClient client, ObjectMapper objectMapper,
      CustomResourceDefinition customResourceDefinition) throws java.io.IOException {
    return client
        .customResources(
            customResourceDefinition,
            IstioResource.class,
            KubernetesResourceList.class,
            DoneableIstioResource.class
        )
        .inNamespace(client.getNamespace())
        .create(
            objectMapper.readValue(
                this.getClass().getResource("/istio-resource.yml"),
                IstioResource.class
            )
        );
  }

  private IstioResource retrieveIstioResourceFromApiServer(OpenShiftClient client,
      CustomResourceDefinition customResourceDefinition) {

    return client
          .customResources(
              customResourceDefinition,
              IstioResource.class,
              KubernetesResourceList.class,
              DoneableIstioResource.class
          )
          .inNamespace(client.getNamespace())
          .withName(RESOURCE_NAME)
          .get();
  }

}
