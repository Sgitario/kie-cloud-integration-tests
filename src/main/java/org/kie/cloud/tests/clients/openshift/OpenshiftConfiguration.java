package org.kie.cloud.tests.clients.openshift;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cz.xtf.core.config.OpenShiftConfig;

@Component
public class OpenshiftConfiguration {

	@Value("${client.openshift.url}")
	private String url;
	@Value("${client.openshift.master.username}")
	private String masterUsername;
	@Value("${client.openshift.master.password}")
	private String masterPassword;
	@Value("${client.openshift.admin.username}")
	private String adminUsername;
	@Value("${client.openshift.admin.password}")
	private String adminPassword;
	@Value("${client.openshift.version}")
	private String version;
	@Value("${client.openshift.namespace}")
	private String namespace;

	@PostConstruct
	public void init() {
		System.setProperty(OpenShiftConfig.OPENSHIFT_URL, url);
		System.setProperty(OpenShiftConfig.OPENSHIFT_MASTER_USERNAME, masterUsername);
		System.setProperty(OpenShiftConfig.OPENSHIFT_MASTER_PASSWORD, masterPassword);
		System.setProperty(OpenShiftConfig.OPENSHIFT_ADMIN_USERNAME, adminUsername);
		System.setProperty(OpenShiftConfig.OPENSHIFT_ADMIN_PASSWORD, adminPassword);
		System.setProperty(OpenShiftConfig.OPENSHIFT_VERSION, version);
		System.setProperty(OpenShiftConfig.OPENSHIFT_NAMESPACE, namespace);
	}
}
