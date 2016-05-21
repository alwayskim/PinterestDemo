package wyz.whaley.pinterest.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alwayking on 16/3/6.
 */
public class ShotInfo implements Serializable{
    private long id;
    private String title;
    private String description;
    private int width;
    private int height;
    private Map<String, String> images;
    private int views_count;
    private int likes_count;
    private int comments_count;
    private int attachments_count;
    private int rebounds_count;
    private int buckets_count;
    private String created_at;
    private String updated_at;
    private String html_url;
    private String attachments_url;
    private String buckets_url;
    private String comments_url;
    private String likes_url;
    private String projects_url;
    private String rebounds_url;
    private boolean animated;
    private List<String> tags;

    private BaseUser user;
    private BaseUser team;

    public int getWidth() {
        return width;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getHeight() {
        return height;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public int getViews_count() {
        return views_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public int getAttachments_count() {
        return attachments_count;
    }

    public int getRebounds_count() {
        return rebounds_count;
    }

    public int getBuckets_count() {
        return buckets_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getAttachments_url() {
        return attachments_url;
    }

    public String getBuckets_url() {
        return buckets_url;
    }

    public String getComments_url() {
        return comments_url;
    }

    public String getLikes_url() {
        return likes_url;
    }

    public String getProjects_url() {
        return projects_url;
    }

    public String getRebounds_url() {
        return rebounds_url;
    }

    public boolean isAnimated() {
        return animated;
    }

    public List<String> getTags() {
        return tags;
    }

    public BaseUser getUser() {
        return user;
    }

    public BaseUser getTeam() {
        return team;
    }


}


