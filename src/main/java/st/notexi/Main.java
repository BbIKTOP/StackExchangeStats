package st.notexi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import st.notexi.model.Items;
import st.notexi.service.StackExchangeUsers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main
{
    // https://api.stackexchange.com/2.3/users?page=1&pagesize=10&site=stackoverflow&filter=!)AAF.30qP7MgDt0CTt)h3LW12.24xtlC5m5-QI
    private final static int PAGESIZE = 100;

    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting: " + System.currentTimeMillis());

        Map<String, String> params = new HashMap<>();
        params.put("pagesize", Integer.toString(PAGESIZE));
        params.put("site", "stackoverflow");
        params.put("filter", "!)AAF.30qP7MgDt0CTt)h3LW12.24xtlC5m5-QI");

        String url = "https://api.stackexchange.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StackExchangeUsers stackExchangeUsers = retrofit.create(StackExchangeUsers.class);
        Call<Items> call = stackExchangeUsers.getUsers(params);

        Items items = null;
        int i = 1;
        do
        {
            params.put("page", Integer.toString(i));
            items = call.execute().body();
            call=call.clone();
            i++;
        } while (items != null && items.getHasMore());

        System.out.println("Done: " + System.currentTimeMillis());
    }
}