package seniordesign.teamc.languageapplication.example;

/**
 * Created by v3nd3774 on 12/2/2017.
 */
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class ServerInterfaceTest {

    private ServerInterface serverInterface = new ServerInterface();
    // Parameters are: (0, 1)
    // 0=word, 1=file
    public Object[] params(){
        File this1 = new File("this1.wav"); //Stored in root of repository
        return new Object[]{
                new Object[]{"this", this1}
        };
    }

    @Test
    @Parameters(method="params")
    public void testQueryServer(String word, File file) {
        // Test that we can contact the server with POST request file +
        // that we can recieve the JSON object.
        double threshhold = 0.05;
        JSONObject expectedResponse = null;
        try {
            expectedResponse = new JSONObject()
                    .put("score", 0.9395329349339164)
                    .put("word", "this");
        } catch (JSONException e) {
            // oops (won't happen this is static
        }
        serverInterface.queryServer(word, file);
        while(serverInterface.score == null){
            System.out.println("Waiting for server to finish working with text...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JSONObject actualResponse = serverInterface.responseJSON;
        String actual = "";
        String expected = "";
        try {
            actual = (String) actualResponse.get("word");
            expected = (String) expectedResponse.get("word");
        } catch (JSONException e) {
            // OOps, didn't work :(
        }
        // Check that the words match
        assertEquals(actual, expected);

        double actualsc = -1;
        double expectedsc = -2;
        try {
            actualsc = (double) actualResponse.get("score");
            expectedsc = (double) expectedResponse.get("score");
        } catch (JSONException e) {
            // OOps, didn't work :(
        }

        assertEquals(actualsc, expectedsc, threshhold);
    }

    @Test
    public void testContactServer(){
        // Test that we can contact the server via HTTP GET request
        int expectedCode = 200;
        assertEquals(serverInterface.contactServer(), expectedCode);
    }
}