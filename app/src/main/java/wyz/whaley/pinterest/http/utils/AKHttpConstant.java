package wyz.whaley.pinterest.http.utils;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class AKHttpConstant {
    public static String USER_ACCESS_TOKEN = null;
    public static final String APPLICATION_ACCESS_TOKEN = "98b9afaaf62fc652291eb7102d8daa0ca2933853f2998647748d79df60d0c06d";
    public static final String ACCESS_TOKEN_PARAM = "access_token";

    public static final String CLIENT_ID = "e930e2dd3f9376b30064e30aacfe6d5a9438053367ab8c2fe0de96f1712f9a0f";
    public static final String CLIENT_ID_PARAM = "client_id";

    public static final String CLIENT_SECRET = "acbea9ebbd786c47d84cffc69a3f3330882c5492caac634974acf699da5dd4ea";
    public static final String CLIENT_SECRET_PARAM = "client_secret";

    public static final String CLIENT_CODE_PARAM = "code";

    public static final String REDIRECT_URI = "redirect_uri";
    public static final String REDIRECT_URI_PARAM = "http://www.qq.com";

    public static final String SCOPE = "scope";
    public static final String SCOPE_TYPE = "public+write+comment+upload";

    public static final String PER_PAGE_PARAM = "per_page";
    public static final String PER_PAGE = "30";
    public static final String PAGE_PARAM = "page";


    public static final String AUTH_URI = "https://dribbble.com/oauth/authorize";

    public static final String TOKEN_URI = "https://dribbble.com/oauth/token";

    public static final String BASE_URI = "https://api.dribbble.com/v1";
    public static final String SHOTS = "/shots";
    public static final String USER_INFO = "/user";
    public static final String FOLLOWING_SHOTS = "/user/following/shots";
    public static final String LIKE_SHOTS = "/user/likes";


    public static String decordFromBaseUri(String subUri) {
        return BASE_URI + subUri;
    }

    public static Map<String, String> getPageParam(int page) {
        Map<String, String> map = new HashMap<>();
        map.put(PER_PAGE_PARAM, PER_PAGE);
        map.put(PAGE_PARAM, String.valueOf(page));
        return map;
    }
}
