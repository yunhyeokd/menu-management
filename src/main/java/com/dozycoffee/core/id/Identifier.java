package com.dozycoffee.core.id;

import java.io.Serializable;

public interface Identifier<T> extends Serializable {
    T getValue();
}
