class ApiHeaders implements RequestInterceptor {

    @Override public void intercept(RequestFacade request) {
        // adding your custom headers
        request.addHeader("User-Agent", "Android");
        request.addHeader("S-Version", version);
        request.addHeader("S-Device-ID", Device.getID());
        request.addHeader("S-OS", "android;" + Build.VERSION.RELEASE);
        request.addHeader("S-Device", TextUtils.join(";", deviceDetail));
        request.addHeader("Authorization", "token" + " " + Session.getMyToken());
    }
}