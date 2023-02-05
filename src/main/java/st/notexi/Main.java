package st.notexi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import st.notexi.model.Items;
import st.notexi.model.User;
import st.notexi.service.StackExchangeUsers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    // https://api.stackexchange.com/2.3/users?page=1&pagesize=10&site=stackoverflow&filter=!)AAF.30qP7MgDt0CTt)h3LW12.24xtlC5m5-QI
    private final static int PAGESIZE = 100;
    private final static String MIN_REPUTATION = "223";
    private final static Set<String> REQUIRED_TAGS = new HashSet<>();

    static
    {
        REQUIRED_TAGS.add("java");
        REQUIRED_TAGS.add(".net");
        REQUIRED_TAGS.add("docker");
        REQUIRED_TAGS.add("c#");
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting: " + System.currentTimeMillis());

        Map<String, String> params = new HashMap<>();
        params.put("pagesize", Integer.toString(PAGESIZE));
        params.put("order", "desc");
        params.put("sort", "reputation");
        params.put("site", "stackoverflow");
        params.put("min", MIN_REPUTATION);
        params.put("filter", "!d0OIIVTgrb09xZY)*aPuDD0EMy4(rCDQ0FUn");

        String url = "https://api.stackexchange.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StackExchangeUsers stackExchangeUsers = retrofit.create(StackExchangeUsers.class);
        Call<Items> call = stackExchangeUsers.getUsers(params);

        // Todo: add throttling (30 reqs per second)
        Items items;
        int i = 1;
        do
        {
            params.put("page", Integer.toString(i));
            items = call.execute().body();
            if (items == null) break;
            call = call.clone();

            List<User> users = items.getItems().stream()
                    .filter(item -> item.getLocation() != null && (item.getLocation().equalsIgnoreCase("romania") || item.getLocation().equalsIgnoreCase("moldova")))
                    .filter(item -> item.getAnswerCount() > 0)
                    .filter(item ->
                    {
                        try
                        {
                            List<String> tags = item.getCollectives().get(0).getCollective().getTags();
                            if (tags.stream().noneMatch(REQUIRED_TAGS::contains)
                            ) return (false);
                        }
                        catch (NullPointerException npe)
                        {
                            return (false);
                        }
                        return (true);
                    })
                    .collect(Collectors.toList());
            for (User u : users)
            {
                System.out.print(u.getDisplayName() + "|" +
                        u.getLocation() + "|" +
                        u.getAnswerCount() + "|" +
                        u.getQuestionCount() + "|"
                );

                boolean first = true;
                try
                {
                    for (String tag : u.getCollectives().get(0).getCollective().getTags())
                    {
                        if (!first) System.out.print(",");
                        else first = false;
                        System.out.print(tag);
                    }
                }
                catch (NullPointerException ignored)
                {
                }
                if (!first) System.out.print("|");

                System.out.println(u.getDisplayName() + "|" +
                        u.getLink() + "|" +
                        u.getProfileImage() + "|"
                );
            }
            i++;
        } while (i < 10);

        System.out.println("Done: " + System.currentTimeMillis());
    }
}