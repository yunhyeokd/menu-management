package com.dozycoffee.domain.common;

import java.io.Serializable;

public interface Identifier<T> extends Serializable {
    T getValue();
}
