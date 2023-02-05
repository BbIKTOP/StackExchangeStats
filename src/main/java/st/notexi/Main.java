package st.notexi;

import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import st.notexi.model.ErrorDescription;
import st.notexi.model.Items;
import st.notexi.model.User;
import st.notexi.service.StackExchangeUsers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    private final static int THROTHLING_LIMIT_PER_SEC = 10;
    private final static int PAGE_SIZE = 100;
    private final static int PAGES_LIMIT = 500;
    private final static String MIN_REPUTATION = "223";
    private final static String ACCESS_KEY = "v)BNnoGX599gljDYzv1Odw((";
    private final static String FIELDS_FILTER = "!d0OIIVTgrb09xZY)*aPuDD0EMy4(rCDQ0FUn";
    private final static Set<String> REQUIRED_TAGS = new HashSet<>();
    private final static Set<String> REQUIRED_COUNTRIES = new HashSet<>();

    static
    {
        REQUIRED_TAGS.add("java");
        REQUIRED_TAGS.add(".net");
        REQUIRED_TAGS.add("docker");
        REQUIRED_TAGS.add("c#");
//        REQUIRED_TAGS.add("apigee");

        REQUIRED_COUNTRIES.add("romania");
        REQUIRED_COUNTRIES.add("moldova");
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting: " + System.currentTimeMillis());

        Map<String, String> params = new HashMap<>();
        params.put("pagesize", Integer.toString(PAGE_SIZE));
        params.put("order", "desc");
        params.put("sort", "reputation");
        params.put("site", "stackoverflow");
        params.put("min", MIN_REPUTATION);
        params.put("filter", FIELDS_FILTER);
        params.put("key", ACCESS_KEY);

        String url = "https://api.stackexchange.com/";

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        StackExchangeUsers stackExchangeUsers = retrofit.create(StackExchangeUsers.class);
        Call<Items> call = stackExchangeUsers.getUsers(params);

        long previousReqTime = 0;
        Items items;
        int i = 1;
        do
        {
            // Check throttling
            long executionTime = System.currentTimeMillis() - previousReqTime;
            if (executionTime < 1000 / THROTHLING_LIMIT_PER_SEC)
            {
                try
                {
                    Thread.sleep(1000 / THROTHLING_LIMIT_PER_SEC - executionTime);
                }
                catch (InterruptedException ignored)
                {
                }
            }

            params.put("page", Integer.toString(i));
            Response<Items> response = call.execute(); // Synchronous request

            if (!response.isSuccessful())
            {
                ErrorDescription errorDescription;
                if (response.errorBody() != null)
                {
                    errorDescription = new Gson().fromJson(response.errorBody().string(), ErrorDescription.class);
                    System.out.printf("Server error %d (%s): %s\n",
                            errorDescription.getErrorId(),
                            errorDescription.getErrorName(),
                            errorDescription.getErrorMessage());
                }
                else
                {
                    System.out.println("Server error.");
                }
                break;
            }
            items = response.body();
            if (items == null || items.getItems() == null)
            {
                System.out.println("No users found!");
                break;
            }
            call = call.clone();

            // Do we need to filter by user_type equalsTo "registered"?
            List<User> users = items.getItems().stream()
                    .filter(item -> item.getLocation() != null
                            &&
                            REQUIRED_COUNTRIES.stream()
                                    .anyMatch(country -> item.getLocation()
                                            .toLowerCase()
                                            .contains(country)))
                    .filter(item -> item.getAnswerCount() > 0)
                    .filter(item ->
                    {
                        try
                        {
                            if (item.getCollectives().get(0).getCollective().getTags().stream()
                                    .noneMatch(REQUIRED_TAGS::contains))
                                return (false);
                        }
                        catch (NullPointerException npe)
                        {
                            return (false);
                        }
                        return (true);
                    }).collect(Collectors.toList());

//            if (users.size() == 0) System.out.print("-\n");
            for (User u : users)
            {
                System.out.print(u.getDisplayName() + "|" +
                        u.getLocation() + "|" +
                        u.getAnswerCount() + "|" +
                        u.getQuestionCount() + "|");

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
                catch (NullPointerException ignored) // No tags, print nothing
                {
                }
                if (!first) System.out.print("|");

                System.out.println(u.getDisplayName() + "|" +
                        u.getLink() + "|" +
                        u.getProfileImage() + "|");
            }
            i++;
            previousReqTime = System.currentTimeMillis();
        } while (i < PAGES_LIMIT);

        System.out.println("Done: " + System.currentTimeMillis());
    }
}