package moe.seikimo.altservice.utils.enums;

import lombok.AllArgsConstructor;

/**
 * Type of algorithm for JOSE signatures.
 */
@AllArgsConstructor
public enum AlgorithmType {
    ECDSA256(32),
    ECDSA384(48);

    public final int ecNumberSize;
}
