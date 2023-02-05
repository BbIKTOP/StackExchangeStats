package st.notexi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Items
{
    @SerializedName("items")
    @Expose
    private List<User> items;
    @SerializedName("has_more")
    @Expose
    private Boolean hasMore;
    @SerializedName("quota_max")
    @Expose
    private Integer quotaMax;
    @SerializedName("quota_remaining")
    @Expose
    private Integer quotaRemaining;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("page_size")
    @Expose
    private Integer pageSize;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("backoff")
    @Expose
    private Integer backoff;
    @SerializedName("type")
    @Expose
    private String type;
}