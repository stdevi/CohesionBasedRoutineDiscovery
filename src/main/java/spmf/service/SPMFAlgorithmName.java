package spmf.service;

public enum SPMFAlgorithmName {
    PrefixSpan("PrefixSpan"),
    CloFast("CloFast"),
    BIDE("BIDE+");

    public final String value;

    SPMFAlgorithmName(String value) {
        this.value = value;
    }
}
