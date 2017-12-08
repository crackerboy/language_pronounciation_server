package seniordesign.teamc.languageapplication.example;

/**
 * Created by v3nd3774 on 12/2/2017.
 */
import android.view.View;

import static org.hamcrest.CoreMatchers.not;
import org.junit.Test;
import org.junit.runner.RunWith;
import junitparams.JUnitParamsRunner;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class WordFactoryTest {

    private WordFactory wordFactory = new WordFactory();

    @Test
    public void testGetWords(){
        // Test that we can retrieve words from the server
        int expectedNumWords = 80;
        int actualNumWords;
        try {
            Thread.sleep(1000); // Give server 2 seconds to send words
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actualNumWords = this.wordFactory.words.size();
        assertEquals(expectedNumWords,actualNumWords);
    }

    @Test public void getRandomWord(){
        // Test that we can retrieve a random word
        String word;
        try {
            Thread.sleep(1000); // Give server 2 seconds to send words
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.wordFactory.drawWord();
        word = this.wordFactory.word;
        assertThat(word,not("None")); // Assert that we actually retrieved a word
    }
}