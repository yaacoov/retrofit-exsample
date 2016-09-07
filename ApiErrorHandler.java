/* Sample of your custom error, you don't have to do this*/

class ApiErrorHandler implements ErrorHandler {

    //region Variable
    private final Gson mGson;
    //endregion

    //region Constructor
    public ApiErrorHandler() {
        mGson = new GsonBuilder().create();
    }
    //endregion

    @Override public Throwable handleError(RetrofitError cause) {
        if (cause.getResponse() != null
                && cause.getResponse().getBody().mimeType().equalsIgnoreCase("application/json")) {
            InputStreamReader reader;
            try {
                Response response = cause.getResponse();

                reader = new InputStreamReader(response.getBody().in());

                ApiError error = mGson.fromJson(reader, ApiError.class);
                error.setReason(response.getReason());
                error.setUrl(response.getUrl());
                error.setStatus(response.getStatus());

                return error;

            } catch (IOException e) {
                Timber.e(e, e.getMessage());

                return new ApiError(cause);
            }
        } else {
            return new ApiError(cause);
        }
    }
}