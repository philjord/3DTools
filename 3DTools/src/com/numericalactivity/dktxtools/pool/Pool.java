package com.numericalactivity.dktxtools.pool;

public class Pool<T extends PoolInterface> {

    protected final int _numberOfSlots; // nombre de slots
    protected PoolInterface[] _pool; // objets disponibles
    protected PoolFactoryInterface<T> _factory; // objet permettant la création de nouveau objets T
    protected int _index = -1; // index du pool

    /**
     * Constructeur.
     * Initialise le nombre de slots.
     * @param numberOfSlots
     */
    public Pool(int numberOfSlots, PoolFactoryInterface<T> factory) {
        _numberOfSlots  = numberOfSlots;
        _pool           = new PoolInterface[numberOfSlots];
        _factory        = factory;
    }

    /**
     * Ajoute un objet au pool
     * @param object
     */
    public synchronized void add(T object) {
        // si le pool est déjà plein on ne peut pas ajouter de nouvel objet
        if (_index + 1 >= _numberOfSlots) {
            return;
        }

        // on incrémente l'index et on ajoute l'objet au pool
        _index++;
        _pool[_index] = object;
    }

    /**
     * Retourne un objet réutilisable
     * @return un objet réinitialisé ou nouvellement crée
     */
    @SuppressWarnings("unchecked")
    public synchronized T get() {
        // on retourne un objet du pool s'il y en a un
        if (_index >= 0) {
            _pool[_index].reset();
            T object        = (T) _pool[_index];
            _pool[_index]   = null;
            _index--;
            return object;
        }

        // il n'y a aucun objet disponible dans le pool donc on crée un nouvel objet
        return _factory.factory();
    }
}
