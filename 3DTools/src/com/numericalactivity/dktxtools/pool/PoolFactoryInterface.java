package com.numericalactivity.dktxtools.pool;

public interface PoolFactoryInterface<T> {
    /**
     * Retourne un nouvel objet T
     * @return
     */
    T factory();
}
