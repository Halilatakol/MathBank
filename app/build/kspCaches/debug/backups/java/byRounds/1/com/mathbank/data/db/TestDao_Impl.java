package com.mathbank.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mathbank.data.model.Difficulty;
import com.mathbank.data.model.Test;
import com.mathbank.data.model.TestAnswer;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TestDao_Impl implements TestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Test> __insertionAdapterOfTest;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<TestAnswer> __insertionAdapterOfTestAnswer;

  private final EntityDeletionOrUpdateAdapter<Test> __deletionAdapterOfTest;

  private final EntityDeletionOrUpdateAdapter<Test> __updateAdapterOfTest;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAnswersForTest;

  public TestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTest = new EntityInsertionAdapter<Test>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tests` (`id`,`name`,`questionIds`,`topics`,`difficulties`,`createdAt`,`totalTime`,`isCompleted`,`score`,`completedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Test entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __converters.listToJson(entity.getQuestionIds());
        statement.bindString(3, _tmp);
        final String _tmp_1 = __converters.listToJson(entity.getTopics());
        statement.bindString(4, _tmp_1);
        final String _tmp_2 = __converters.difficultyListToJson(entity.getDifficulties());
        statement.bindString(5, _tmp_2);
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getTotalTime());
        final int _tmp_3 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        statement.bindLong(9, entity.getScore());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCompletedAt());
        }
      }
    };
    this.__insertionAdapterOfTestAnswer = new EntityInsertionAdapter<TestAnswer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `test_answers` (`id`,`testId`,`questionId`,`selectedAnswer`,`isCorrect`,`timeSpent`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestAnswer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTestId());
        statement.bindString(3, entity.getQuestionId());
        if (entity.getSelectedAnswer() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSelectedAnswer());
        }
        final int _tmp = entity.isCorrect() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getTimeSpent());
      }
    };
    this.__deletionAdapterOfTest = new EntityDeletionOrUpdateAdapter<Test>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tests` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Test entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfTest = new EntityDeletionOrUpdateAdapter<Test>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tests` SET `id` = ?,`name` = ?,`questionIds` = ?,`topics` = ?,`difficulties` = ?,`createdAt` = ?,`totalTime` = ?,`isCompleted` = ?,`score` = ?,`completedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Test entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __converters.listToJson(entity.getQuestionIds());
        statement.bindString(3, _tmp);
        final String _tmp_1 = __converters.listToJson(entity.getTopics());
        statement.bindString(4, _tmp_1);
        final String _tmp_2 = __converters.difficultyListToJson(entity.getDifficulties());
        statement.bindString(5, _tmp_2);
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getTotalTime());
        final int _tmp_3 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        statement.bindLong(9, entity.getScore());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCompletedAt());
        }
        statement.bindString(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAnswersForTest = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_answers WHERE testId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTest(final Test test, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTest.insert(test);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAnswer(final TestAnswer answer,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestAnswer.insert(answer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAnswers(final List<TestAnswer> answers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestAnswer.insert(answers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTest(final Test test, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTest.handle(test);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTest(final Test test, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTest.handle(test);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAnswersForTest(final String testId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAnswersForTest.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, testId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAnswersForTest.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllTests(final Continuation<? super List<Test>> $completion) {
    final String _sql = "SELECT * FROM tests ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Test>>() {
      @Override
      @NonNull
      public List<Test> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "questionIds");
          final int _cursorIndexOfTopics = CursorUtil.getColumnIndexOrThrow(_cursor, "topics");
          final int _cursorIndexOfDifficulties = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulties");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfTotalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<Test> _result = new ArrayList<Test>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Test _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<String> _tmpQuestionIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionIds);
            _tmpQuestionIds = __converters.jsonToList(_tmp);
            final List<String> _tmpTopics;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTopics);
            _tmpTopics = __converters.jsonToList(_tmp_1);
            final List<Difficulty> _tmpDifficulties;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDifficulties);
            _tmpDifficulties = __converters.jsonToDifficultyList(_tmp_2);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpTotalTime;
            _tmpTotalTime = _cursor.getInt(_cursorIndexOfTotalTime);
            final boolean _tmpIsCompleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_3 != 0;
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new Test(_tmpId,_tmpName,_tmpQuestionIds,_tmpTopics,_tmpDifficulties,_tmpCreatedAt,_tmpTotalTime,_tmpIsCompleted,_tmpScore,_tmpCompletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTestById(final String id, final Continuation<? super Test> $completion) {
    final String _sql = "SELECT * FROM tests WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Test>() {
      @Override
      @Nullable
      public Test call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfQuestionIds = CursorUtil.getColumnIndexOrThrow(_cursor, "questionIds");
          final int _cursorIndexOfTopics = CursorUtil.getColumnIndexOrThrow(_cursor, "topics");
          final int _cursorIndexOfDifficulties = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulties");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfTotalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final Test _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<String> _tmpQuestionIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionIds);
            _tmpQuestionIds = __converters.jsonToList(_tmp);
            final List<String> _tmpTopics;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTopics);
            _tmpTopics = __converters.jsonToList(_tmp_1);
            final List<Difficulty> _tmpDifficulties;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDifficulties);
            _tmpDifficulties = __converters.jsonToDifficultyList(_tmp_2);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpTotalTime;
            _tmpTotalTime = _cursor.getInt(_cursorIndexOfTotalTime);
            final boolean _tmpIsCompleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_3 != 0;
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _result = new Test(_tmpId,_tmpName,_tmpQuestionIds,_tmpTopics,_tmpDifficulties,_tmpCreatedAt,_tmpTotalTime,_tmpIsCompleted,_tmpScore,_tmpCompletedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAnswersForTest(final String testId,
      final Continuation<? super List<TestAnswer>> $completion) {
    final String _sql = "SELECT * FROM test_answers WHERE testId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, testId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestAnswer>>() {
      @Override
      @NonNull
      public List<TestAnswer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTestId = CursorUtil.getColumnIndexOrThrow(_cursor, "testId");
          final int _cursorIndexOfQuestionId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionId");
          final int _cursorIndexOfSelectedAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedAnswer");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfTimeSpent = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSpent");
          final List<TestAnswer> _result = new ArrayList<TestAnswer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestAnswer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTestId;
            _tmpTestId = _cursor.getString(_cursorIndexOfTestId);
            final String _tmpQuestionId;
            _tmpQuestionId = _cursor.getString(_cursorIndexOfQuestionId);
            final String _tmpSelectedAnswer;
            if (_cursor.isNull(_cursorIndexOfSelectedAnswer)) {
              _tmpSelectedAnswer = null;
            } else {
              _tmpSelectedAnswer = _cursor.getString(_cursorIndexOfSelectedAnswer);
            }
            final boolean _tmpIsCorrect;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCorrect);
            _tmpIsCorrect = _tmp != 0;
            final int _tmpTimeSpent;
            _tmpTimeSpent = _cursor.getInt(_cursorIndexOfTimeSpent);
            _item = new TestAnswer(_tmpId,_tmpTestId,_tmpQuestionId,_tmpSelectedAnswer,_tmpIsCorrect,_tmpTimeSpent);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
