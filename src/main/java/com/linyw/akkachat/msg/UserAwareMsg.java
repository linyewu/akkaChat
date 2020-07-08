package com.linyw.akkachat.msg;

import com.linyw.akkachat.id.UserId;

public interface UserAwareMsg {
    UserId getUserId();
}
