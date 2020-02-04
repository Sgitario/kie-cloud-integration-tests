package org.kie.cloud.tests.utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.openshift.api.model.Parameter;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.Template;
import org.apache.commons.lang3.StringUtils;

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

	public static String getName(Template template, HasMetadata object) {
		return resolveProperty(template, object.getMetadata().getName());
	}

	public static final Optional<String> findParam(Template template, String paramName) {
		return template.getParameters().stream().filter(param -> StringUtils.equals(param.getName(), paramName))
				.map(Parameter::getValue).findFirst();
	}

	private static String resolveProperty(Template template, String property) {
		String result = property;
		String[] properties = StringUtils.substringsBetween(property, START_TAG, END_TAG);
		if (properties != null) {
			for (String item : properties) {
				String value = findParam(template, item).orElseGet(() -> {
					fail("Cannot find property '" + item + "'");
					return null;
				});

				result = result.replace(START_TAG + item + END_TAG, value);
			}
		}

		return result;
	}
}
