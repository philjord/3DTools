package com.numericalactivity.dktxtools.pool;

public interface PoolInterface {
    /**
     * Remet l'objet à son état d'origine, comme s'il venait d'être crée.
     * Marque cet objet comme n'étant plus disponible pour réutilisation.
     */
    public void reset();

    /**
     * Marque l'objet comme étant réutilisable et l'ajoute au pool.
     * Un objet ne peut appeler cette méthode plus d'une fois sans appeler la méthode "reset()"
     */
    public void recycle();
}
