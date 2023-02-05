package st.notexi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Collectives
{
    @SerializedName("collective")
    @Expose
    private Collective collective;
}
