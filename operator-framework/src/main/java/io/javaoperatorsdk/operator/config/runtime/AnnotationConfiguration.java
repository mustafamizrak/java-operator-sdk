package io.javaoperatorsdk.operator.config.runtime;

import io.fabric8.kubernetes.client.CustomResource;
import io.javaoperatorsdk.operator.ControllerUtils;
import io.javaoperatorsdk.operator.api.Controller;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.config.ControllerConfiguration;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class AnnotationConfiguration<R extends CustomResource>
    implements ControllerConfiguration<R> {

  private final ResourceController<R> controller;
  private final Optional<Controller> annotation;

  public AnnotationConfiguration(ResourceController<R> controller) {
    this.controller = controller;
    this.annotation = Optional.ofNullable(controller.getClass().getAnnotation(Controller.class));
  }

  @Override
  public String getName() {
    return ControllerUtils.getNameFor(controller);
  }

  @Override
  public String getCRDName() {
    return CustomResource.getCRDName(getCustomResourceClass());
  }

  @Override
  public String getFinalizer() {
    return annotation
        .map(Controller::finalizerName)
        .filter(Predicate.not(String::isBlank))
        .orElse(ControllerUtils.getDefaultFinalizerName(getCRDName()));
  }

  @Override
  public boolean isGenerationAware() {
    return annotation.map(Controller::generationAwareEventProcessing).orElse(true);
  }

  @Override
  public Class<R> getCustomResourceClass() {
    return RuntimeControllerMetadata.getCustomResourceClass(controller);
  }

  @Override
  public Set<String> getNamespaces() {
    return Set.of(annotation.map(Controller::namespaces).orElse(new String[] {}));
  }

  @Override
  public String getAssociatedControllerClassName() {
    return controller.getClass().getCanonicalName();
  }
}
