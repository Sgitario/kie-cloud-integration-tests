package org.kie.cloud.tests.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.openshift.api.model.Parameter;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.Template;
import org.apache.commons.lang3.StringUtils;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.Severity;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.fail;

public final class OpenshiftExtensionModelUtils {

	private static final String START_TAG = "${";
	private static final String END_TAG = "}";

	private OpenshiftExtensionModelUtils() {

	}

    public static final String resolveRoute(Route route) {
        String protocol = "http";
        String host = route.getSpec().getHost();
        if (route.getSpec().getPort() != null && route.getSpec().getPort().getTargetPort() != null && route.getSpec().getPort().getTargetPort().getStrVal() != null) {
            protocol = route.getSpec().getPort().getTargetPort().getStrVal();
        }

        return String.format("%s://%s", protocol, host);
    }

	@SuppressWarnings("unchecked")
	public static final <T extends HasMetadata> List<T> objectsOf(Template template, Class<T> clazz) {
		return template.getObjects().stream().filter(o -> clazz.isInstance(o)).map(o -> (T) o)
				.collect(Collectors.toList());
	}

    public static String getName(Template template, HasMetadata object, Map<String, String> parameters) {
        return resolveProperty(template, object.getMetadata().getName(), parameters);
	}

	public static final Optional<String> findParam(Template template, String paramName) {
		return template.getParameters().stream().filter(param -> StringUtils.equals(param.getName(), paramName))
				.map(Parameter::getValue).findFirst();
	}

    public static final List<String> getErrorMessagesFromServerInfo(ServiceResponse<KieServerInfo> info) {
        if (info.getResult() == null || info.getResult().getMessages() == null) {
            return Collections.emptyList();
        }

        return info.getResult().getMessages().stream().filter(m -> Severity.ERROR.equals(m.getSeverity())).map(m -> m.getMessages().stream().collect(joining())).collect(toList());
    }

    private static String resolveProperty(Template template, String property, Map<String, String> parameters) {
		String result = property;
		String[] properties = StringUtils.substringsBetween(property, START_TAG, END_TAG);
		if (properties != null) {
			for (String item : properties) {
                String value = Optional.ofNullable(parameters.get(item)).orElseGet(() -> findParam(template, item).orElseGet(() -> {
                    fail("Cannot find property '" + item + "'");
                    return null;
                }));

				result = result.replace(START_TAG + item + END_TAG, value);
			}
		}

		return result;
	}
}
