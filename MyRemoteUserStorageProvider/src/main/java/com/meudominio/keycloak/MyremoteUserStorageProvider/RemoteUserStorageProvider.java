package com.meudominio.keycloak.MyremoteUserStorageProvider;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.Set;

public class RemoteUserStorageProvider implements UserStorageProvider,
        UserLookupProvider, CredentialInputValidator{
    private final KeycloakSession session;
    private final ComponentModel model;
    private final UsersApiService usersService;
    private String username;

    public RemoteUserStorageProvider(KeycloakSession session,
                                     ComponentModel model, UsersApiService usersService) {
        this.session = session;
        this.model = model;
        this.usersService = usersService;
    }


    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();

        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {

        UserModel returnValue = null;

        User user = usersService.getUserDetails(username);

        if(user!=null) {
            returnValue = createUserModel(username, realm);
        }
        System.out.println("getUserByUsername: returnValue: " + returnValue.getUsername());
        return returnValue;
    }

    private UserModel createUserModel(String username, RealmModel realm) {
        this.username = username;
        return new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                System.out.println("createUserModel: username: " + username);
                return username;
            }

            @Override
            public boolean isEmailVerified() {
                return true;
            }

            @Override
            public Set<RoleModel> getRealmRoleMappings() {
                return super.getRealmRoleMappings();
            }
        };
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {

        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return false;
        }
        return !getCredentialStore().getStoredCredentialsStream(realm, user).findAny().isPresent();
    }

    private UserCredentialStore getCredentialStore() {
        return session.userCredentialManager();
    }

//    @Override
//    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
//        VerifyPasswordResponse verifyPasswordResponse = usersService.verifyUserPassword(user.getUsername(),
//                credentialInput.getChallengeResponse());
//
//        if(verifyPasswordResponse == null) return false;
//
//
//        return verifyPasswordResponse.getResult();
//    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;

        System.out.println("isValid method: user: " + user.toString());
        System.out.println("isValid method: getUsername: " + user.getUsername());
        System.out.println("isValid method: password: " + input.getChallengeResponse());
        VerifyPasswordResponse verifyPasswordResponse = usersService.verifyUserPassword(this.username,
                input.getChallengeResponse());

        if(verifyPasswordResponse == null) return false;


        return verifyPasswordResponse.getResult();
    }

}