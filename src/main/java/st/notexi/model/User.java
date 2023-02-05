package st.notexi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class User
{
    @SerializedName("collectives")
    @Expose
    private List<Collectives> collectives;
    @SerializedName("answer_count")
    @Expose
    private Integer answerCount;
    @SerializedName("question_count")
    @Expose
    private Integer questionCount;
    @SerializedName("account_id")
    @Expose
    private Integer accountId;
    @SerializedName("reputation")
    @Expose
    private Integer reputation;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("display_name")
    @Expose
    private String displayName;

}