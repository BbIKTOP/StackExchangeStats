package st.notexi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Collective
{
    @SerializedName("tags")
    @Expose
    private List<String> tags;
}
