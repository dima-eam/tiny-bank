package org.eam.tinybank.domain;

import lombok.NonNull;

public record Operation<T>(long timestamp, @NonNull String description,@NonNull T value) {

}
