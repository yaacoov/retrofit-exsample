public interface Service {

    @DELETE("/item")
    void checkEligibility(
            @Query("id") String id,
            CancelableCallback<Response> cb);

    @GET("/ping")
    void ping(CancelableCallback<String> cb);

    @Multipart
    @POST("/photo")
    PhotoUploadResponse uploadPhoto(@Part("photo") TypedFile photo);
}