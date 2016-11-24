package com.kevinmost.lifx;

import com.kevinmost.lifx.model.LifxError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public abstract class LifxResult<T> {

  @NotNull private final LifxRequest<T> request;

  protected LifxResult(@NotNull LifxRequest<T> request) {
    this.request = request;
  }

  @NotNull public final T unwrap() {
    return asSuccess().get();
  }

  @Nullable public final T getOrNull() {
    return isSuccess() ? asSuccess().get() : null;
  }

  @NotNull public final T getOr(@NotNull T or) {
    return isSuccess() ? asSuccess().get() : or;
  }

  public final boolean isSuccess() {
    return this instanceof Success;
  }

  @NotNull public final Success<T> asSuccess() {
    return ((Success<T>) this);
  }

  public final boolean isFailure() {
    return this instanceof Failure;
  }

  @NotNull public final Failure<T> asFailure() {
    return ((Failure<T>) this);
  }

  public final boolean isNetworkError() {
    return this instanceof NetworkError;
  }

  @NotNull public final NetworkError<T> asNetworkError() {
    return ((NetworkError<T>) this);
  }

  public static final class Success<T> extends LifxResult<T> {
    public final int httpCode;
    @NotNull private final T result;

    public Success(@NotNull LifxRequest<T> request, int httpCode, @NotNull T result) {
      super(request);
      this.httpCode = httpCode;
      this.result = result;
    }

    @NotNull public T get() {
      return result;
    }
  }

  public static final class Failure<T> extends LifxResult<T> {
    public final int httpCode;
    @NotNull private final List<LifxError> errors;

    public Failure(@NotNull LifxRequest<T> request, int httpCode, @NotNull List<LifxError> errors) {
      super(request);
      this.httpCode = httpCode;
      this.errors = errors;
    }

    @NotNull public List<LifxError> get() {
      return errors;
    }
  }

  public static final class NetworkError<T> extends LifxResult<T> {
    @NotNull private final IOException e;

    public NetworkError(@NotNull LifxRequest<T> request, @NotNull IOException e) {
      super(request);
      this.e = e;
    }

    @NotNull public IOException get() {
      return e;
    }
  }

  @NotNull public LifxRequest<T> originalRequest() {
    return request;
  }
}
