package com.meudominio.keycloak.MyremoteUserStorageProvider;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class RemoteUserStorageProviderFactory implements UserStorageProviderFactory<RemoteUserStorageProvider> {

    public static final String PROVIDER_NAME = "my-remote-user-storage-provider";
    private static final String PROVIDER_HOST = "http://localhost:8099";

    @Override
    public RemoteUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new RemoteUserStorageProvider(keycloakSession,componentModel,
                buildHttpClient(PROVIDER_HOST));
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    private UsersApiService buildHttpClient(String uri){
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(uri);
        return target.proxyBuilder(UsersApiService.class)
                .classloader(UsersApiService.class.getClassLoader()).build();

    }
}
