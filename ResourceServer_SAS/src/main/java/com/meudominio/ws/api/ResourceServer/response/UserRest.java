package com.meudominio.ws.api.ResourceServer.response;

import lombok.Data;

@Data
public class UserRest {

    private String userFirsName;
    private String userLastName;
    private String userId;

    public UserRest(String userFirsName, String userLastName, String userId) {
        this.userFirsName = userFirsName;
        this.userLastName = userLastName;
        this.userId = userId;
    }
}
