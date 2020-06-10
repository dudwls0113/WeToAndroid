package com.ninano.weto.src.group_invite.interfaces;

public interface GroupInviteView {

    void groupAcceptSuccess();

    void existUser();

    void notExistUser();

    void signUpSuccess();

    void validateFailure(String message);
}
