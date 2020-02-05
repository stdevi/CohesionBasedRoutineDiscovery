package spmf;

public enum SPMFAlgorithmName {
    PrefixSpan("PrefixSpan"),
    BIDE("BIDE+");

    public final String value;

    SPMFAlgorithmName(String value) {
        this.value = value;
    }
}
