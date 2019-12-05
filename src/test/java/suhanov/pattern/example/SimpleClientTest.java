package suhanov.pattern.example;

import io.reactivex.Single;
import org.junit.Test;
import suhanov.pattern.example.model.SimpleResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleClientTest {
    @Test
    public void scheduleSuccess() throws Exception {
        SimpleClient simpleClient = SimpleClient.builder().baseUrl("https://jsonplaceholder.typicode.com/todos/1").build();
        Single<SimpleResponse> resultSingle1 = simpleClient.schedule();
        resultSingle1.subscribe(
                processingResult -> {
                    assertThat(processingResult.getId(), is(1));
                    assertThat(processingResult.getUserId(), is(1));
                    assertThat(processingResult.getTitle(), is("delectus aut autem"));
                    assertThat(processingResult.getCompleted(), is(false));
                },
                throwable -> {
                    System.out.println(throwable.getMessage());
                }
        );
    }
}
