package org.kie.cloud.tests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.openshift.api.model.Parameter;
import io.fabric8.openshift.api.model.Template;

public final class OpenshiftExtensionModelUtils {

	private static final String START_TAG = "${";
	private static final String END_TAG = "}";

	private OpenshiftExtensionModelUtils() {

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
