public final class Api {

    //region Variables
    private static final String API_HOST = Application.string(R.string.api_server);
    private static final Object sLockObject = new Object();

    private static Service sService = null;
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    //endregion

    //region Constructor
    private Api() {}
    //endregion

    private Endpoint getEndpoint() {
        return Endpoints.newFixedEndpoint(API_HOST);
    }

    private RequestInterceptor getHeaders() {
        return new ApiHeaders();
    }

    private ErrorHandler getErrorHandler() {
        return new ApiErrorHandler();
    }

    private Client getClient() {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = ImageUtil.getBitmapFromCache("http");
            if (cacheDir != null) {
                Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
                client.setCache(cache);
            }
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }
        client.setSslSocketFactory(createBadSslSocketFactory());

        return new OkClient(client);
    }

    private RestAdapter getRestAdapter() {
        return new RestAdapter.Builder()
                .setClient(getClient())
                .setEndpoint(getEndpoint())
                .setRequestInterceptor(getHeaders())
                .setErrorHandler(getErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    public static Service getService() {
        if (sService != null) {
            return sService;
        }

        // synchronize, re-check the singleton reference, and create if still necessary
        synchronized (sLockObject) {
            if (sService == null) {
                sService = (new Api())
                        .getRestAdapter()
                        .create(Service.class);
            }
        }
        return sService;
    }

    private SSLSocketFactory createBadSslSocketFactory() {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager() {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            context.init(null, new TrustManager[] { permissive }, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}