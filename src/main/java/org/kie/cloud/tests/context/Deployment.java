package org.kie.cloud.tests.context;

import java.beans.Transient;
import java.io.Closeable;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.kie.cloud.tests.utils.CloseableUtils;

@Getter
@RequiredArgsConstructor
public class Deployment {
	private final String name;
	private final Map<String, String> environmentVariables;
    @Setter
    private String insecureUrl;
    @Setter
    private Object channel;

	@Transient
	public String getEnvironmentVariable(String paramName) {
		return environmentVariables.get(paramName);
	}

    public void close() {
        if (channel != null && channel instanceof Closeable) {
            CloseableUtils.closeQuietly((Closeable) channel);
        }

    }
}
