package com.mathbank.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile QuestionDao _questionDao;

  private volatile TestDao _testDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` TEXT NOT NULL, `text` TEXT NOT NULL, `imageData` BLOB, `fullPageImageData` BLOB, `topic` TEXT NOT NULL, `subtopic` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `options` TEXT NOT NULL, `correctAnswer` TEXT, `sourcePdf` TEXT NOT NULL, `pageNumber` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `hasFigure` INTEGER NOT NULL, `questionType` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tests` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `questionIds` TEXT NOT NULL, `topics` TEXT NOT NULL, `difficulties` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `totalTime` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `score` INTEGER NOT NULL, `completedAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_answers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `testId` TEXT NOT NULL, `questionId` TEXT NOT NULL, `selectedAnswer` TEXT, `isCorrect` INTEGER NOT NULL, `timeSpent` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ff9c4cf67841bdd8306d7211a5d05d89')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `tests`");
        db.execSQL("DROP TABLE IF EXISTS `test_answers`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(14);
        _columnsQuestions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("imageData", new TableInfo.Column("imageData", "BLOB", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("fullPageImageData", new TableInfo.Column("fullPageImageData", "BLOB", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("topic", new TableInfo.Column("topic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("subtopic", new TableInfo.Column("subtopic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("difficulty", new TableInfo.Column("difficulty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("options", new TableInfo.Column("options", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("correctAnswer", new TableInfo.Column("correctAnswer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("sourcePdf", new TableInfo.Column("sourcePdf", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("pageNumber", new TableInfo.Column("pageNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("hasFigure", new TableInfo.Column("hasFigure", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("questionType", new TableInfo.Column("questionType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.mathbank.data.model.Question).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsTests = new HashMap<String, TableInfo.Column>(10);
        _columnsTests.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("questionIds", new TableInfo.Column("questionIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("topics", new TableInfo.Column("topics", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("difficulties", new TableInfo.Column("difficulties", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("totalTime", new TableInfo.Column("totalTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTests.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTests = new TableInfo("tests", _columnsTests, _foreignKeysTests, _indicesTests);
        final TableInfo _existingTests = TableInfo.read(db, "tests");
        if (!_infoTests.equals(_existingTests)) {
          return new RoomOpenHelper.ValidationResult(false, "tests(com.mathbank.data.model.Test).\n"
                  + " Expected:\n" + _infoTests + "\n"
                  + " Found:\n" + _existingTests);
        }
        final HashMap<String, TableInfo.Column> _columnsTestAnswers = new HashMap<String, TableInfo.Column>(6);
        _columnsTestAnswers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestAnswers.put("testId", new TableInfo.Column("testId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestAnswers.put("questionId", new TableInfo.Column("questionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestAnswers.put("selectedAnswer", new TableInfo.Column("selectedAnswer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestAnswers.put("isCorrect", new TableInfo.Column("isCorrect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestAnswers.put("timeSpent", new TableInfo.Column("timeSpent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTestAnswers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTestAnswers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTestAnswers = new TableInfo("test_answers", _columnsTestAnswers, _foreignKeysTestAnswers, _indicesTestAnswers);
        final TableInfo _existingTestAnswers = TableInfo.read(db, "test_answers");
        if (!_infoTestAnswers.equals(_existingTestAnswers)) {
          return new RoomOpenHelper.ValidationResult(false, "test_answers(com.mathbank.data.model.TestAnswer).\n"
                  + " Expected:\n" + _infoTestAnswers + "\n"
                  + " Found:\n" + _existingTestAnswers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "ff9c4cf67841bdd8306d7211a5d05d89", "9d5db23de05212535cdadfeb9074d743");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "questions","tests","test_answers");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `tests`");
      _db.execSQL("DELETE FROM `test_answers`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(QuestionDao.class, QuestionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TestDao.class, TestDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public QuestionDao questionDao() {
    if (_questionDao != null) {
      return _questionDao;
    } else {
      synchronized(this) {
        if(_questionDao == null) {
          _questionDao = new QuestionDao_Impl(this);
        }
        return _questionDao;
      }
    }
  }

  @Override
  public TestDao testDao() {
    if (_testDao != null) {
      return _testDao;
    } else {
      synchronized(this) {
        if(_testDao == null) {
          _testDao = new TestDao_Impl(this);
        }
        return _testDao;
      }
    }
  }
}
