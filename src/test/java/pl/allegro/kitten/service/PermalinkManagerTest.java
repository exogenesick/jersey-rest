package pl.allegro.kitten.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import pl.allegro.kitten.model.Permalink;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PermalinkManagerTest {

    private String key;
    private String nullableKey;
    private String value;
    private StringRedisTemplate redisTemplateMock;
    private ValueOperations valueOperationsMock;
    private PermalinkManager permalinkManager;

    @Before
    public void setUp() throws Exception {
        key = "http://source-url";
        value = "http://destination-url";
        nullableKey = "http://key-with-not-existence-value";

        redisTemplateMock = mock(StringRedisTemplate.class);
        valueOperationsMock = mock(ValueOperations.class);

        when(valueOperationsMock.get(nullableKey)).thenReturn(null);
        when(valueOperationsMock.get(key)).thenReturn(value);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(valueOperationsMock).set(key, value);

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(redisTemplateMock).delete(key);

        permalinkManager = new PermalinkManager(redisTemplateMock);
    }

    @Test
    public void should_return_permalink_object_type() throws Exception {
        assertThat(permalinkManager.get(key), instanceOf(Permalink.class));
    }

    @Test
    public void should_return_expected_value_for_given_key() throws Exception {
        assertEquals(value, permalinkManager.get(key).getDestinationUrl());
    }

    @Test
    public void should_return_null_value_for_given_key() throws Exception {
        assertNull(permalinkManager.get(nullableKey));
    }

    @Test
    public void should_return_permalink_object_after_set() throws Exception {
        Permalink permalink = new Permalink();
        permalink.setSourceUrl(key);
        permalink.setDestinationUrl(value);

        permalinkManager.set(permalink);

        verify(valueOperationsMock, times(1)).set(key, value);
    }

    @Test
    public void should_delete_permalink() throws Exception {
        Permalink permalink = new Permalink();
        permalink.setSourceUrl(key);
        permalink.setDestinationUrl(value);

        permalinkManager.delete(permalink);

        verify(redisTemplateMock, times(1)).delete(key);
    }
}
