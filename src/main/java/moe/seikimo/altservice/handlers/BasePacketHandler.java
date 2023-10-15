package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;

public interface BasePacketHandler<T extends GeneratedMessageV3> {
    /**
     * Functional interface with exceptions.
     */
    interface Function<I, O> {
        O apply(I input) throws Exception;
    }
}
