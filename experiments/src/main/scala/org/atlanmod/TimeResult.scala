package org.atlanmod

class TimeResult() {

    var tuples: Double = 0L
    var instantiate: Double = 0L
    var extract: Double = 0L
    var broadcast: Double = 0L
    var apply: Double = 0L

    private var tuples_start: Double = 0L
    private var instantiate_start: Double = 0L
    private var extract_start: Double = 0L
    private var broadcast_start: Double = 0L
    private var apply_start: Double = 0L
    private var tuples_end: Double = 0L
    private var instantiate_end: Double = 0L
    private var extract_end: Double = 0L
    private var broadcast_end: Double = 0L
    private var apply_end: Double = 0L

    def this (tuples_time: Long = 0L, instantiate_time: Long = 0L, extract_time: Long = 0L,
              broadcast_time: Long = 0L, apply_time: Long = 0L) = {
        this()
        tuples = tuples_time
        instantiate = instantiate_time
        extract = extract_time
        broadcast = broadcast_time
        apply = apply_time
    }

    def get_total_time(): Double = tuples + instantiate + extract + broadcast + apply
    def get_tuples_time(): Double = tuples
    def get_instantiate_time(): Double = instantiate
    def get_extract_time(): Double = extract
    def get_broadcast_time(): Double = broadcast
    def get_apply_time(): Double = apply

    def start_tuples(): Unit = tuples_start = System.nanoTime()
    def end_tuples(): Unit = {
        tuples_end = System.nanoTime()
        this.tuples = (tuples_end - tuples_start) * 1000 / 1e9d
    }

    def start_instantiate(): Unit = instantiate_start = System.nanoTime()
    def end_instantiate(): Unit = {
        instantiate_end = System.nanoTime()
        this.instantiate = (instantiate_end - instantiate_start) * 1000 / 1e9d
    }

    def start_extract(): Unit = extract_start = System.nanoTime()
    def end_extract(): Unit = {
        extract_end = System.nanoTime()
        this.extract = (extract_end - extract_start) * 1000 / 1e9d
    }

    def start_broadcast(): Unit = broadcast_start = System.nanoTime()
    def end_broadcast(): Unit = {
        broadcast_end = System.nanoTime()
        this.broadcast = (broadcast_end - broadcast_start) * 1000 / 1e9d
    }

    def start_apply(): Unit = apply_start = System.nanoTime()
    def end_apply(): Unit = {
        apply_end = System.nanoTime()
        this.apply = (apply_end - apply_start) * 1000 / 1e9d
    }

}