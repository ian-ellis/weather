package com.marvinslullaby.weather.data.search

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rx.Observable
import rx.schedulers.Schedulers


//open class SearchTermSqlHelper(){
open class SearchTermSqlHelper(context: Context)
: SQLiteOpenHelper(context, "SearchTermDB", null, 1) {

  val TABLE_NAME = "searchTerms"
  val KEY_ID = "id"
  val KEY_VALUE = "value"
  val COLUMNS = arrayOf(KEY_ID, KEY_VALUE)
//
  override fun onCreate(db: SQLiteDatabase) {
    val CREATE_TABLE = """CREATE TABLE $TABLE_NAME
      ( id INTEGER PRIMARY KEY AUTOINCREMENT,
        $KEY_VALUE TEXT)"""
    db.execSQL(CREATE_TABLE);
  }
//
  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    val sql = "DROP TABLE IF EXISTS $TABLE_NAME"
      db.execSQL(sql)
    this.onCreate(db)
  }

  open fun getAll():Observable<List<SearchTerm>>{

    return doAsync({
      val result = mutableListOf<SearchTerm>()
      val query = "SELECT  * FROM $TABLE_NAME ORDER BY id"
      val db = this.writableDatabase;
      val cursor = db.rawQuery(query, null);

      if (cursor.moveToFirst()) {
        do {
          result.add(readSearchTerm(cursor))
        } while (cursor.moveToNext())
      }
      result.toList()
    }).onErrorReturn {
      emptyList<SearchTerm>()
    }
  }

  open fun add(searchTerm:SearchTerm):Observable<SearchTerm>{
    return find(searchTerm).flatMap{
      //found existing, remove and add so it is the latest when ordered by id
      delete(searchTerm).flatMap {
        add(searchTerm)
      }
    }.onErrorResumeNext{
      //could not find so actually add new entry
      doAsync {
        val db = this.writableDatabase
        val values = getContentValues(searchTerm)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        searchTerm
      }

    }
  }



  open fun delete(searchTerm: SearchTerm): Observable<SearchTerm> {
    return doAsync {
      val db = this.writableDatabase
      db.delete(TABLE_NAME, "$KEY_VALUE  = ?", arrayOf(SearchTerm.map(searchTerm)))
      db.close();
      searchTerm
    }
  }

  protected fun find(searchTerm:SearchTerm):Observable<SearchTerm>{
    return doAsync {
      val db = this.readableDatabase
      val cursor = db.query(TABLE_NAME,
        COLUMNS,//columns
        "$KEY_VALUE = ?",//selections
        arrayOf(SearchTerm.map(searchTerm)),//selection args
        null, // group by
        null, // having
        null, // order by
        null)
      cursor.moveToFirst()
      readSearchTerm(cursor)
    }
  }


  protected fun readSearchTerm(cursor: Cursor):SearchTerm{
    val value = cursor.getString(1)
    return SearchTerm.map(value)
  }

  protected fun <T> doAsync(action: () -> T): Observable<T> {
    return Observable.create<T>() { subscriber ->
      try {
        val result = action.invoke()
        subscriber.onNext(result)
        subscriber.onCompleted()
      } catch(e: Exception) {
        subscriber.onError(e)
      }

    }.subscribeOn(Schedulers.io())
  }



  protected fun getContentValues(searchTerm:SearchTerm): ContentValues {
    val values = ContentValues()
    values.put(KEY_VALUE, SearchTerm.map(searchTerm))
    return values
  }

}