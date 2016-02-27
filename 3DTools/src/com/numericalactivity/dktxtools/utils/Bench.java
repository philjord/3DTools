package com.numericalactivity.dktxtools.utils;

/**
 * Utilitaire de benchmark
 */
public class Bench {

    protected int _size; // nombre de compteurs
    protected long[] _counters; // compteurs
    protected long[] _numberOfSequences; // nombre de fois que chaque compteur a été démarré et stoppé
    protected long[] _tempNano; // timestamps temporaires

    /**
     * Constructeur
     */
    public Bench() {
        initialize(1);
    }

    /**
     * Constructeur
     * @param size défini le nombre de compteurs
     */
    public Bench(int size) {
        initialize(size);
    }

    /**
     * Initialise le nombre de compteurs
     */
    protected void initialize(int size) {
        _counters           = new long[size];
        _numberOfSequences  = new long[size];
        _tempNano           = new long[size];
    }

    /**
     * Log un message
     * @param string
     */
    protected void log(String string) {
        System.out.println(string);
    }

    /**
     * Démarre un chrono
     * @param tag clé à associer au chrono
     */
    public void start(int index) {
        _tempNano[index] = System.nanoTime();
    }

    /**
     * Stoppe un chrono et ajoute sa valeur au compteur associé
     * @param tag clé associée au chrono
     */
    public void stop(int index) {
        _counters[index] = (System.nanoTime() - _tempNano[index]) + _counters[index];
        _numberOfSequences[index]++;
    }

    /**
     * Remet un compteur à zéro
     * @param tag clé associée au compteur
     */
    public void reset(int index) {
        _counters[index]            = 0;
        _numberOfSequences[index]   = 0;
    }

    /**
     * Log la valeur de tous les compteurs
     */
    public void log() {
        for (byte i = 0; i < _size; i++) {
            log(i);
        }
    }

    /**
     * Log la valeur d'un compteur
     * @param tag clé associée au compteur
     */
    public void log(int index) {
        log(index + " : " + String.valueOf(_counters[index] / 1000000) + "µs (" + String.valueOf(_numberOfSequences[index]) + " sequences)");
    }

    /**
     * Retourne la valeur de tous les compteurs
     * @return valeurs des compteurs
     */
    public long[] get() {
        return _counters;
    }

    /**
     * Retourne la valeur d'un compteur
     * @param tag clé associée au compteur
     * @return valeur du compteur
     */
    public long get(int index) {
        return _counters[index];
    }

    /**
     * Retourne le nombre de fois qu'un compteur associé à un tag a été démarré et stoppé
     * @param tag clé associée au compteur
     * @return
     */
    public long getNumberOfSequences(int index) {
        return _numberOfSequences[index];
    }
}
