package com.kevinmost.internal;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public final class AutoValueTypeAdapterFactory implements TypeAdapterFactory {

  private static final String AUTO_VALUE_CLASS_PREFIX = "AutoValue_";

  @SuppressWarnings("unchecked")
  @Override
  public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
    final Type type = typeToken.getType();
    if (!(type instanceof Class)) {
      return null;
    }
    final String typeName = ((Class<?>) type).getName();
    if (!typeName.contains(AUTO_VALUE_CLASS_PREFIX)) {
      return null;
    }
    try {
      final Class<?> nonAutoValueType = Class.forName(typeName.replace(AUTO_VALUE_CLASS_PREFIX, ""));
      return ((TypeAdapter<T>) gson.getAdapter(nonAutoValueType));
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Tried to get the non-AutoValue version of " + typeName, e);
    }
  }

}
