package st.notexi.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import st.notexi.model.Items;

import java.util.Map;



public interface StackExchangeUsers
{
    @GET("/2.3/users/")
    Call <Items> getUsers(@QueryMap Map<String, String> params);
}
