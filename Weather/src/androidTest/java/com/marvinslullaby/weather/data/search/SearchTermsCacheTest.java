package com.marvinslullaby.weather.data.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SearchTermsCacheTest {

  private final String KEY = "searchterms";
  protected final String  GPS_VALUE = "__GPS__";

  private Context context;
  private SharedPreferences prefs;

  @Before
  public void setup(){
    context = InstrumentationRegistry.getContext();
    prefs = context.getSharedPreferences("SearchTerms", Context.MODE_PRIVATE);
  }

  @Test
  public void getStoredSearchTerms_returnsAllSearchTermsStoredAsSearchTerms(){


  }

}
