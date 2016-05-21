package wyz.whaley.pinterest.utils;

import java.util.Map;

/**
 * Created by alwayking on 16/3/9.
 */
public class DribbbleParamsUtils {
    public static final String IMAGE_HIDP_MAP = "hidpi";
    public static final String IMAGE_NORMAL_MAP = "normal";
    public static final String IMAGE_TEASER_MAP = "teaser";

    public static String getBestImage(Map<String, String> imageMap) {
        String url = null;
        if (imageMap.get(IMAGE_HIDP_MAP) != null) {
            url = imageMap.get(IMAGE_HIDP_MAP);
        } else if (imageMap.get(IMAGE_NORMAL_MAP) != null) {
            url = imageMap.get(IMAGE_NORMAL_MAP);
        } else if (imageMap.get(IMAGE_TEASER_MAP) != null) {
            url = imageMap.get(IMAGE_TEASER_MAP);
        }
        return url;
    }

    public static String getSmallImage(Map<String, String> imageMap) {
        String url = null;
        if (imageMap.get(IMAGE_TEASER_MAP) != null) {
            url = imageMap.get(IMAGE_TEASER_MAP);
        } else if (imageMap.get(IMAGE_NORMAL_MAP) != null) {
            url = imageMap.get(IMAGE_NORMAL_MAP);
        } else if (imageMap.get(IMAGE_HIDP_MAP) != null) {
            url = imageMap.get(IMAGE_HIDP_MAP);
        }
        return url;
    }
}
