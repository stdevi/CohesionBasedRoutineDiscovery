package spmf;

public enum SPMFAlgorithmName {
    PrefixSpan("PrefixSpan"),
    CloSpan("CloSpan"),
    BIDE("BIDE+");

    public final String value;

    SPMFAlgorithmName(String value) {
        this.value = value;
    }
}
