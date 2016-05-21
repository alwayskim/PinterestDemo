package wyz.whaley.pinterest.data;

import java.io.Serializable;

/**
 * Created by alwayking on 16/3/17.
 */
public class LikeShotInfo implements Serializable{
    private long id;
    private String created_at;
    private ShotInfo shot;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public ShotInfo getShot() {
        return shot;
    }

    public void setShot(ShotInfo shot) {
        this.shot = shot;
    }
}
