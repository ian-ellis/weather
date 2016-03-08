package com.marvinslullaby.weather.data.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class SearchTermsCacheTest {

  private final String KEY = "searchterms";
  protected final String  GPS_VALUE = "__GPS__";

  private Context context;
  private SharedPreferences prefs;
  private SearchTermsCache cache;

  @Before
  public void setup(){
    context = InstrumentationRegistry.getContext();
    prefs = context.getSharedPreferences("SearchTerms", Context.MODE_PRIVATE);
    cache = new SearchTermsCache(context);
  }

  @Test
  public void cache_returnsAllSearchTermsStoredAsSearchTerms(){
    //given:
    Set<String> newTerms = new LinkedHashSet();
    newTerms.add("Sydney");
    newTerms.add("Melbourne");
    newTerms.add("2099");

    prefs.edit().putStringSet(
      KEY,
      newTerms
    ).commit();
    TestSubscriber<List<SearchTerm>> subscriber = new TestSubscriber<>();

    //when:
    cache.getStoredSearchTerms().subscribe(subscriber);
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);

    //then:
    assertThat(subscriber.getOnNextEvents().size(), is(1));
    List<SearchTerm> result = subscriber.getOnNextEvents().get(0);

    assertThat(result.size(), is(3));

    assertThat(result.get(0),instanceOf(SearchTerm.City.class));
    assertThat(((SearchTerm.City)result.get(0)).getCity(),is("Sydney"));

    assertThat(result.get(1),instanceOf(SearchTerm.City.class));
    assertThat(((SearchTerm.City) result.get(1)).getCity(), is("Melbourne"));

    assertThat(result.get(1),instanceOf(SearchTerm.Zip.class));
    assertThat(((SearchTerm.Zip) result.get(1)).getZip(), is("2099"));

  }
}
