package com.kevinmost.lifx.request;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kevinmost.internal.Func1;
import com.kevinmost.internal.JSONArrayBuilder;
import com.kevinmost.internal.JSONObjectBuilder;
import com.kevinmost.internal.JsonUtil;
import com.kevinmost.lifx.LifxRequest;
import com.kevinmost.lifx.request.model.Operation;
import com.kevinmost.lifx.request.model.OperationResult;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.kevinmost.internal.Util.assertRange;

public final class SetLightsRequest extends LifxRequest.Adapter<List<OperationResult>> {

  @NotNull private final List<Operation> operations = new ArrayList<>();

  SetLightsRequest() {}

  @NotNull public SetLightsRequest plus(Operation... operations) {
    return plus(Arrays.asList(operations));
  }

  @NotNull public SetLightsRequest plus(Collection<Operation> operations) {
    this.operations.addAll(operations);
    return this;
  }

  @NotNull @Override public Request buildRequest(@NotNull HttpUrl baseURL, @NotNull final Gson gson) {
    assertRange("number of operations", operations.size(), 1, 50);
    final JsonObject body = new JSONObjectBuilder()
        .add("states", new JSONArrayBuilder()
            .addAll(operations, new Func1<Operation, JsonElement>() {
              @NotNull @Override public JsonElement call(@NotNull Operation in) {
                return JsonUtil.toJSON(gson, in, Operation.class);
              }
            })
        )
        .build();
    return new Request.Builder()
        .url(baseURL.resolve("v1/lights/states"))
        .put(JsonUtil.toRequestBody(gson, body))
        .build();
  }

  @NotNull @Override
  public List<OperationResult> unmarshal(@NotNull JsonElement json, @NotNull Gson gson) {
    final List<OperationResult> out = new ArrayList<>();
    final JsonArray results = json.getAsJsonArray();
    for (int i = 0; i < results.size(); i++) {
      out.add(JsonUtil.fromJSON(gson, results.get(i), OperationResult.class));
    }
    return out;
  }

}
