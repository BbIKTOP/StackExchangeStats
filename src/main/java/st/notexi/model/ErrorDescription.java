package st.notexi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ErrorDescription
{
    @SerializedName("error_id")
    @Expose
    private int errorId;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;
    @SerializedName("error_name")
    @Expose
    private String errorName;
}
