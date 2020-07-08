package com.linyw.akkachat.id;


import java.io.Serializable;

public interface EntityId<ID extends Serializable> {
    ID getId();
}
