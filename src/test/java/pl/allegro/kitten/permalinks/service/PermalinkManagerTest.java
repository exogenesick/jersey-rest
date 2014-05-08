package pl.allegro.kitten.permalinks.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import pl.allegro.kitten.permalinks.model.Permalink;
import pl.allegro.kitten.permalinks.repository.PermalinkRepository;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PermalinkManagerTest {

    private String key = "http://source-url";
    private String nullableKey = "http://key-with-not-existence-value";
    private String value = "http://destination-url";

    @Mock
    private StringRedisTemplate redisTemplateMock;

    @Mock
    private ValueOperations valueOperationsMock;

    @Mock
    private PermalinkRepository permalinkRepositoryMock;

    private PermalinkManager permalinkManager;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        permalinkManager = new PermalinkManager(permalinkRepositoryMock);

        when(permalinkRepositoryMock.findDestinationUrlBySourceUrl(key)).thenReturn(value);
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
        permalinkManager.set(new Permalink(key, value));
        verify(permalinkRepositoryMock, times(1)).save(key, value);
    }

    @Test
    public void should_delete_permalink() throws Exception {
        permalinkManager.delete(new Permalink(key, value));
        verify(permalinkRepositoryMock, times(1)).deleteBySourceUrl(key);
    }

}
