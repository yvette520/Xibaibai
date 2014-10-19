package com.xibaibai.worker;

import java.lang.reflect.Field;  
import java.lang.reflect.Method;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
import android.content.ContentValues;  
import android.content.Context;  
import android.database.AbstractCursor;
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;  
import android.nfc.Tag;
import android.util.Log;  
/** 
 *  write()
 *  writeArray()
 *  read()
 *  readByid()
 *  
 *      增删改 --> 操作一个sql语句，并且有返回值。 
 *      查询    --> 1. 返回一个游标类型 
 *                2. 返回一个List<Object> 
 *                3. 返回一个List<Map<String, Object>> 
 *  
 * 时间： 2014-07-28 
 */  
public class DBclass {
	public static String db_name = "mydbmydb"; 
	
	private static final int DATABASE_VERSION = 1;
	
    private DBHelper dbHelper;  
    public static DBclass instance = null;  
    private SQLiteDatabase sqliteDatabase;  
  
    public String mOrderby = null;
    /** 
     * 构造函数 
     * @param context   上下文对象 
     */  
    DBclass(Context context, String dbname) {
    	if(dbname.length() < 1) return;
    	db_name = dbname;
        dbHelper = new DBHelper(context, dbname);  
        sqliteDatabase = dbHelper.getReadableDatabase();                                                          
    }  
    DBclass(Context context, String dbname,String initSQL) {
    	if(dbname.length() < 1) return;
    	db_name = dbname;
    	
    	DBHelper.initSQL = initSQL;
        dbHelper = new DBHelper(context, dbname);  
        sqliteDatabase = dbHelper.getReadableDatabase();                                                          
    }     
      
    /*** 
     * 获取本类对象实例 
     * @param context   上下文对象 
     * @return 
     */  
    public static final DBclass getInstance(Context context, String dbname) {  
        if (instance == null)   
            instance = new DBclass(context,dbname);  
        return instance;  
    }  
    
    
    /** 
     * 关闭数据库 
     */  
    public void close() {  
        if(sqliteDatabase.isOpen()) sqliteDatabase.close();  
        if(dbHelper != null) dbHelper.close();  
        if(instance != null) instance = null;  
    }  
  
    
    public void execSQL(String SQL) { 
    	try{
        sqliteDatabase.execSQL(SQL);
    	}catch (Exception e) {         
            e.printStackTrace();          		
    	}
     }  
    
   public void write(String table, ContentValues values){
	    Object Ouid = values.get("_id");
	   // if(Ouid==null) return;
	    
    	//String uid = Ouid.toString();
    	if(Ouid==null){
    		insertData(table, values);
    	}else{
    		updataData(table,values, "_id="+ Ouid, null);
    	}
    	
    }
    
   public ContentValues readById(String table, long id){
	   String filter = "_id = " + id;
	   List<ContentValues> mList = readArray(table, filter);
	   if(mList.size() < 1)
	     return null;
	   else
		 return mList.get(0);
   }
   public ContentValues readOneByFilter(String table, String filter){
	    
	   List<ContentValues> mList = readArray(table, filter);
	   if(mList.size() < 1)
	     return null;
	   else
		 return mList.get(0);
   }
   
   public List<ContentValues> readArray(String table, String filter){
	   
 
	   
	   List<ContentValues> mList = new ArrayList<ContentValues>();  
       if(sqliteDatabase.isOpen()){  
           Cursor cursor;
           //cursor = sqliteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
           //selection 中需要嵌入字符串的地方用 ? 代替，然后在 selectionArgs 中依次提供各个用于替换的值就可以了
             cursor = sqliteDatabase.query(table, null, filter, null, null, null, mOrderby, null);
             
        	if(cursor != null && cursor.getCount() > 0) {  
        	   int Ccount =cursor.getColumnCount();
               String Key, Value;
               int Type;
               while(cursor.moveToNext()){
            	   
            	   ContentValues cv = new ContentValues(); 
                   for(int i = 0; i < Ccount; i++) 
                   {  
                	   Key = cursor.getColumnName(i) ;
                	   Type = cursor.getType(i);
                	    
                	   //Log.e("",  ""+ (cursor.getLong(0) ));
                	   
                	   if(Type== AbstractCursor.FIELD_TYPE_NULL)
                		  cv.putNull(Key);
                	   else if(Type== AbstractCursor.FIELD_TYPE_STRING)
                		  cv.put(Key,  cursor.getString(i));
                	   else if(Type== AbstractCursor.FIELD_TYPE_FLOAT)
                 		  cv.put(Key,  cursor.getFloat(i));
                	   else if(Type== AbstractCursor.FIELD_TYPE_INTEGER)
                  		  cv.put(Key,   cursor.getInt(i));
                	   else if(Type== AbstractCursor.FIELD_TYPE_BLOB)
                  		  cv.put(Key,  cursor.getBlob(i));
                	   
                       //map.put(f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));  
                  }  
                   mList.add(cv);                
               } 
               
           }else{
        	   
           }  
           
           cursor.close();  
       }else{  
           Log.i("info", "数据库已关闭");  
       }  
       
       return mList;  
	   
   }
    
    
    
   /*********************************** basic ***************************************************/ 
    
    /** 
     * 插入数据 
     * @param sql       执行更新操作的sql语句 
     * @param bindArgs      sql语句中的参数,参数的顺序对应占位符顺序 
     * @return  result      返回新添记录的行号，与主键id无关  
     */  
    public Long insertDataBySql(String sql, String[] bindArgs) throws Exception{  
        long result = 0;  
        if(sqliteDatabase.isOpen()){  
            SQLiteStatement statement = sqliteDatabase.compileStatement(sql);  
            if(bindArgs != null){  
                int size = bindArgs.length;  
                for(int i = 0; i < size; i++){  
                    //将参数和占位符绑定，对应  
                    statement.bindString(i+1, bindArgs[i]);  
                }  
                result = statement.executeInsert();  
                statement.close();  
            }  
        }else{  
            Log.i("info", "数据库已关闭");  
        }  
        return result;  
    }  
      
    /** 
     * 插入数据 
     * @param table         表名 
     * @param values        要插入的数据 
     * @return  result      返回新添记录的行号，与主键id无关  
     */  
    public Long insertData(String table, ContentValues values){  
        long result = 0;  
        if(sqliteDatabase.isOpen()){  
            result = sqliteDatabase.insert(table, null, values);  
        }  
        return result;  
    }  
      
    /** 
     * 更新数据 
     * @param sql       执行更新操作的sql语句 
     * @param bindArgs  sql语句中的参数,参数的顺序对应占位符顺序 
     */  
    public void updateDataBySql(String sql, String[] bindArgs) throws Exception{  
        if(sqliteDatabase.isOpen()){  
            SQLiteStatement statement = sqliteDatabase.compileStatement(sql);  
            if(bindArgs != null){  
                int size = bindArgs.length;  
                for(int i = 0; i < size; i++){  
                    statement.bindString(i+1, bindArgs[i]);  
                }  
                statement.execute();  
                statement.close();  
            }  
        }else{  
            Log.i("info", "数据库已关闭");  
        }  
    }  
      
    /** 
     * 更新数据 
     * @param table         表名 
     * @param values        表示更新的数据 
     * @param whereClause   表示SQL语句中条件部分的语句 
     * @param whereArgs     表示占位符的值 
     * @return 
     */  
    public int updataData(String table, ContentValues values, String whereClause, String[] whereArgs){  
        int result = 0;  
        if(sqliteDatabase.isOpen()){  
            result = sqliteDatabase.update(table, values, whereClause, whereArgs);  
        }  
        return result;  
    }  
  
    /** 
     * 删除数据 
     * @param sql       执行更新操作的sql语句 
     * @param bindArgs  sql语句中的参数,参数的顺序对应占位符顺序 
     */  
    public void deleteDataBySql(String sql, String[] bindArgs) throws Exception{  
        if(sqliteDatabase.isOpen()){  
            SQLiteStatement statement = sqliteDatabase.compileStatement(sql);  
            if(bindArgs != null){  
                int size = bindArgs.length;  
                for(int i = 0; i < size; i++){  
                    statement.bindString(i+1, bindArgs[i]);  
                }  
                Method[] mm = statement.getClass().getDeclaredMethods();  
                for (Method method : mm) {  
                    Log.i("info", method.getName());          
                    /** 
                     *  反射查看是否能获取executeUpdateDelete方法 
                     *  查看源码可知 executeUpdateDelete是public的方法，但是好像被隐藏了所以不能被调用， 
                     *      利用反射貌似只能在root以后的机器上才能调用，小米是可以，其他机器却不行，所以还是不能用。 
                     */  
                }  
                statement.execute();      
                statement.close();  
            }  
        }else{  
            Log.i("info", "数据库已关闭");  
        }  
    }  
  
    /** 
     * 删除数据 
     * @param table         表名 
     * @param whereClause   表示SQL语句中条件部分的语句 
     * @param whereArgs     表示占位符的值 
     * @return               
     */  
    public int deleteData(String table, String whereClause, String[] whereArgs){  
        int result = 0;  
        if(sqliteDatabase.isOpen()){  
            result = sqliteDatabase.delete(table, whereClause, whereArgs);  
        }  
        return result;  
    }  
      
    /** 
     * 查询数据 
     * @param searchSQL         执行查询操作的sql语句 
     * @param selectionArgs     查询条件 
     * @return                  返回查询的游标，可对数据自行操作，需要自己关闭游标 
     */  
    public Cursor queryData2Cursor(String sql, String[] selectionArgs) throws Exception{  
        if(sqliteDatabase.isOpen()){  
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);  
            if (cursor != null) {  
                cursor.moveToFirst();   
            }  
            return cursor;  
        }  
        return null;  
    }  
      
    /** 
     * 查询数据 
     * @param sql               执行查询操作的sql语句     
     * @param selectionArgs     查询条件 
     * @param object                Object的对象 
     * @return List<Object>       返回查询结果   
     */  
    public List<Object> queryData2Object(String sql, String[] selectionArgs, Object object) throws Exception{  
        List<Object> mList = new ArrayList<Object>();  
        if(sqliteDatabase.isOpen()){  
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);  
            Field[] f;  
            if(cursor != null && cursor.getCount() > 0) {  
                while(cursor.moveToNext()){  
                    f = object.getClass().getDeclaredFields();  
                    for(int i = 0; i < f.length; i++) {  
                        //为JavaBean 设值  
                        invokeSet(object, f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));  
                    }  
                    mList.add(object);  
                }  
            }  
            cursor.close();  
        }else{  
            Log.i("info", "数据库已关闭");  
        }  
        return mList;  
    }  
      
    /** 
     * 查询数据 
     * @param sql                           执行查询操作的sql语句     
     * @param selectionArgs                 查询条件 
     * @param object                            Object的对象 
     * @return  List<Map<String, Object>>   返回查询结果   
     * @throws Exception 
     */  
    public List<Map<String, Object>> queryData2Map(String sql, String[] selectionArgs, Object object)throws Exception{  
        List<Map<String, Object>> mList = new ArrayList<Map<String,Object>>();  
        if(sqliteDatabase.isOpen()){  
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);  
            Field[] f;  
            Map<String, Object> map;  
            if(cursor != null && cursor.getCount() > 0) {  
                while(cursor.moveToNext()){  
                    map = new HashMap<String, Object>();  
                    f = object.getClass().getDeclaredFields();  
                    for(int i = 0; i < f.length; i++) {  
                        map.put(f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));  
                    }  
                    mList.add(map);  
                }  
            }  
            cursor.close();  
        }else{  
            Log.i("info", "数据库已关闭");  
        }  
        return mList;  
    }   
      
    /**     
     * java反射bean的set方法     
     * @param objectClass     
     * @param fieldName     
     * @return     
     */         
    @SuppressWarnings("unchecked")         
    public static Method getSetMethod(Class objectClass, String fieldName) {         
        try {         
            Class[] parameterTypes = new Class[1];         
            Field field = objectClass.getDeclaredField(fieldName);         
            parameterTypes[0] = field.getType();         
            StringBuffer sb = new StringBuffer();         
            sb.append("set");         
            sb.append(fieldName.substring(0, 1).toUpperCase());         
            sb.append(fieldName.substring(1));         
            Method method = objectClass.getMethod(sb.toString(), parameterTypes);         
            return method;         
        } catch (Exception e) {         
            e.printStackTrace();         
        }         
        return null;         
    }         
        
    /**     
     * 执行set方法     
     * @param object    执行对象     
     * @param fieldName 属性     
     * @param value     值     
     */         
    public static void invokeSet(Object object, String fieldName, Object value) {         
        Method method = getSetMethod(object.getClass(), fieldName);         
        try {         
            method.invoke(object, new Object[] { value });         
        } catch (Exception e) {         
            e.printStackTrace();         
        }         
    }
    
    private static class DBHelper extends SQLiteOpenHelper
	{   public static String initSQL = ""; 
    	 
    	DBHelper(Context context, String dbname)
		{
		  super(context, dbname, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			//db.execSQL("DROP TABLE IF EXISTS messages");
		   if(initSQL.length() < 1) return;
		   db.execSQL(initSQL);	
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,	int newVersion)
		{
			Log.w("DBclass", "Upgrading database from version " + oldVersion
			+ " to "
			+ newVersion + ", which will destroy all old data");
			
			//db.execSQL("DROP TABLE IF EXISTS messages");
			onCreate(db);
		}
	}
}  