package io.swagger.client.api;

import com.google.gson.reflect.TypeToken;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.Configuration;
import io.swagger.client.Pair;
import io.swagger.client.ProgressRequestBody;
import io.swagger.client.ProgressResponseBody;
import io.swagger.client.model.Matches;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchesApi {
  private ApiClient apiClient;

  public MatchesApi() {
    this(Configuration.getDefaultApiClient());
  }

  public MatchesApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Build call for matches
   * @param userID user to return matches for (required)
   * @param progressListener Progress listener
   * @param progressRequestListener Progress request listener
   * @return Call to execute
   * @throws ApiException If fail to serialize the request body object
   */
  public com.squareup.okhttp.Call matchesCall(String userID, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
    Object localVarPostBody = null;

    // create path and map variables
    String localVarPath = "/matches/{userID}/"
        .replaceAll("\\{" + "userID" + "\\}", apiClient.escapeString(userID.toString()));

    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

    Map<String, String> localVarHeaderParams = new HashMap<String, String>();

    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    final String[] localVarAccepts = {
        "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

    final String[] localVarContentTypes = {

    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    localVarHeaderParams.put("Content-Type", localVarContentType);

    if(progressListener != null) {
      apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
        @Override
        public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
          com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
          return originalResponse.newBuilder()
              .body(new ProgressResponseBody(originalResponse.body(), progressListener))
              .build();
        }
      });
    }

    String[] localVarAuthNames = new String[] {  };
    return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
  }

  @SuppressWarnings("rawtypes")
  private com.squareup.okhttp.Call matchesValidateBeforeCall(String userID, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
    // verify the required parameter 'userID' is set
    if (userID == null) {
      throw new ApiException("Missing the required parameter 'userID' when calling matches(Async)");
    }

    com.squareup.okhttp.Call call = matchesCall(userID, progressListener, progressRequestListener);
    return call;





  }

  /**
   *
   * return a maximum of 100 matches for a user
   * @param userID user to return matches for (required)
   * @return Matches
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   */
  public Matches matches(String userID) throws ApiException {
    ApiResponse<Matches> resp = matchesWithHttpInfo(userID);
    return resp.getData();
  }

  /**
   *
   * return a maximum of 100 matches for a user
   * @param userID user to return matches for (required)
   * @return ApiResponse&lt;Matches&gt;
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   */
  public ApiResponse<Matches> matchesWithHttpInfo(String userID) throws ApiException {
    com.squareup.okhttp.Call call = matchesValidateBeforeCall(userID, null, null);
    Type localVarReturnType = new TypeToken<Matches>(){}.getType();
    return apiClient.execute(call, localVarReturnType);
  }

  /**
   *  (asynchronously)
   * return a maximum of 100 matches for a user
   * @param userID user to return matches for (required)
   * @param callback The callback to be executed when the API call finishes
   * @return The request call
   * @throws ApiException If fail to process the API call, e.g. serializing the request body object
   */
  public com.squareup.okhttp.Call matchesAsync(String userID, final ApiCallback<Matches> callback) throws ApiException {

    ProgressResponseBody.ProgressListener progressListener = null;
    ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

    if (callback != null) {
      progressListener = new ProgressResponseBody.ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
          callback.onDownloadProgress(bytesRead, contentLength, done);
        }
      };

      progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
        @Override
        public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
          callback.onUploadProgress(bytesWritten, contentLength, done);
        }
      };
    }

    com.squareup.okhttp.Call call = matchesValidateBeforeCall(userID, progressListener, progressRequestListener);
    Type localVarReturnType = new TypeToken<Matches>(){}.getType();
    apiClient.executeAsync(call, localVarReturnType, callback);
    return call;
  }
}