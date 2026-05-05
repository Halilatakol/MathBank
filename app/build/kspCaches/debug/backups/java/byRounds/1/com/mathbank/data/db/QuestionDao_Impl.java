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
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mathbank.data.model.Difficulty;
import com.mathbank.data.model.Question;
import com.mathbank.data.model.QuestionType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Question> __insertionAdapterOfQuestion;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Question> __deletionAdapterOfQuestion;

  private final EntityDeletionOrUpdateAdapter<Question> __updateAdapterOfQuestion;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestion = new EntityInsertionAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`text`,`imageData`,`fullPageImageData`,`topic`,`subtopic`,`difficulty`,`options`,`correctAnswer`,`sourcePdf`,`pageNumber`,`createdAt`,`hasFigure`,`questionType`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getText());
        if (entity.getImageData() == null) {
          statement.bindNull(3);
        } else {
          statement.bindBlob(3, entity.getImageData());
        }
        if (entity.getFullPageImageData() == null) {
          statement.bindNull(4);
        } else {
          statement.bindBlob(4, entity.getFullPageImageData());
        }
        statement.bindString(5, entity.getTopic());
        statement.bindString(6, entity.getSubtopic());
        final String _tmp = __converters.difficultyToString(entity.getDifficulty());
        statement.bindString(7, _tmp);
        final String _tmp_1 = __converters.listToJson(entity.getOptions());
        statement.bindString(8, _tmp_1);
        if (entity.getCorrectAnswer() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCorrectAnswer());
        }
        statement.bindString(10, entity.getSourcePdf());
        statement.bindLong(11, entity.getPageNumber());
        statement.bindLong(12, entity.getCreatedAt());
        final int _tmp_2 = entity.getHasFigure() ? 1 : 0;
        statement.bindLong(13, _tmp_2);
        final String _tmp_3 = __converters.questionTypeToString(entity.getQuestionType());
        statement.bindString(14, _tmp_3);
      }
    };
    this.__deletionAdapterOfQuestion = new EntityDeletionOrUpdateAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `questions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfQuestion = new EntityDeletionOrUpdateAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `questions` SET `id` = ?,`text` = ?,`imageData` = ?,`fullPageImageData` = ?,`topic` = ?,`subtopic` = ?,`difficulty` = ?,`options` = ?,`correctAnswer` = ?,`sourcePdf` = ?,`pageNumber` = ?,`createdAt` = ?,`hasFigure` = ?,`questionType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getText());
        if (entity.getImageData() == null) {
          statement.bindNull(3);
        } else {
          statement.bindBlob(3, entity.getImageData());
        }
        if (entity.getFullPageImageData() == null) {
          statement.bindNull(4);
        } else {
          statement.bindBlob(4, entity.getFullPageImageData());
        }
        statement.bindString(5, entity.getTopic());
        statement.bindString(6, entity.getSubtopic());
        final String _tmp = __converters.difficultyToString(entity.getDifficulty());
        statement.bindString(7, _tmp);
        final String _tmp_1 = __converters.listToJson(entity.getOptions());
        statement.bindString(8, _tmp_1);
        if (entity.getCorrectAnswer() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCorrectAnswer());
        }
        statement.bindString(10, entity.getSourcePdf());
        statement.bindLong(11, entity.getPageNumber());
        statement.bindLong(12, entity.getCreatedAt());
        final int _tmp_2 = entity.getHasFigure() ? 1 : 0;
        statement.bindLong(13, _tmp_2);
        final String _tmp_3 = __converters.questionTypeToString(entity.getQuestionType());
        statement.bindString(14, _tmp_3);
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM questions WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<Question> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestion.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final Question question, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestion.insert(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Question question, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQuestion.handle(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Question question, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfQuestion.handle(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<Question>> $completion) {
    final String _sql = "SELECT * FROM questions ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "imageData");
          final int _cursorIndexOfFullPageImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "fullPageImageData");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfSubtopic = CursorUtil.getColumnIndexOrThrow(_cursor, "subtopic");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfSourcePdf = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePdf");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfHasFigure = CursorUtil.getColumnIndexOrThrow(_cursor, "hasFigure");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final byte[] _tmpImageData;
            if (_cursor.isNull(_cursorIndexOfImageData)) {
              _tmpImageData = null;
            } else {
              _tmpImageData = _cursor.getBlob(_cursorIndexOfImageData);
            }
            final byte[] _tmpFullPageImageData;
            if (_cursor.isNull(_cursorIndexOfFullPageImageData)) {
              _tmpFullPageImageData = null;
            } else {
              _tmpFullPageImageData = _cursor.getBlob(_cursorIndexOfFullPageImageData);
            }
            final String _tmpTopic;
            _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            final String _tmpSubtopic;
            _tmpSubtopic = _cursor.getString(_cursorIndexOfSubtopic);
            final Difficulty _tmpDifficulty;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.stringToDifficulty(_tmp);
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToList(_tmp_1);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpSourcePdf;
            _tmpSourcePdf = _cursor.getString(_cursorIndexOfSourcePdf);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpHasFigure;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfHasFigure);
            _tmpHasFigure = _tmp_2 != 0;
            final QuestionType _tmpQuestionType;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp_3);
            _item = new Question(_tmpId,_tmpText,_tmpImageData,_tmpFullPageImageData,_tmpTopic,_tmpSubtopic,_tmpDifficulty,_tmpOptions,_tmpCorrectAnswer,_tmpSourcePdf,_tmpPageNumber,_tmpCreatedAt,_tmpHasFigure,_tmpQuestionType);
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
  public Object getById(final String id, final Continuation<? super Question> $completion) {
    final String _sql = "SELECT * FROM questions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Question>() {
      @Override
      @Nullable
      public Question call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "imageData");
          final int _cursorIndexOfFullPageImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "fullPageImageData");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfSubtopic = CursorUtil.getColumnIndexOrThrow(_cursor, "subtopic");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfSourcePdf = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePdf");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfHasFigure = CursorUtil.getColumnIndexOrThrow(_cursor, "hasFigure");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final Question _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final byte[] _tmpImageData;
            if (_cursor.isNull(_cursorIndexOfImageData)) {
              _tmpImageData = null;
            } else {
              _tmpImageData = _cursor.getBlob(_cursorIndexOfImageData);
            }
            final byte[] _tmpFullPageImageData;
            if (_cursor.isNull(_cursorIndexOfFullPageImageData)) {
              _tmpFullPageImageData = null;
            } else {
              _tmpFullPageImageData = _cursor.getBlob(_cursorIndexOfFullPageImageData);
            }
            final String _tmpTopic;
            _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            final String _tmpSubtopic;
            _tmpSubtopic = _cursor.getString(_cursorIndexOfSubtopic);
            final Difficulty _tmpDifficulty;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.stringToDifficulty(_tmp);
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToList(_tmp_1);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpSourcePdf;
            _tmpSourcePdf = _cursor.getString(_cursorIndexOfSourcePdf);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpHasFigure;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfHasFigure);
            _tmpHasFigure = _tmp_2 != 0;
            final QuestionType _tmpQuestionType;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp_3);
            _result = new Question(_tmpId,_tmpText,_tmpImageData,_tmpFullPageImageData,_tmpTopic,_tmpSubtopic,_tmpDifficulty,_tmpOptions,_tmpCorrectAnswer,_tmpSourcePdf,_tmpPageNumber,_tmpCreatedAt,_tmpHasFigure,_tmpQuestionType);
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
  public Object getFiltered(final List<String> topics, final int topicsEmpty,
      final List<String> difficulties, final int difficultiesEmpty, final String query,
      final int figurOnly, final Continuation<? super List<Question>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM questions ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = 1 OR topic IN (");
    final int _inputSize = topics.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("          AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = 1 OR difficulty IN (");
    final int _inputSize_1 = difficulties.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("          AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = '' OR text LIKE '%' || ");
    _stringBuilder.append("?");
    _stringBuilder.append(" || '%' OR topic LIKE '%' || ");
    _stringBuilder.append("?");
    _stringBuilder.append(" || '%' OR subtopic LIKE '%' || ");
    _stringBuilder.append("?");
    _stringBuilder.append(" || '%')");
    _stringBuilder.append("\n");
    _stringBuilder.append("          AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = 0 OR hasFigure = 1)");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY createdAt DESC");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 7 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, topicsEmpty);
    _argIndex = 2;
    for (String _item : topics) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    _argIndex = 2 + _inputSize;
    _statement.bindLong(_argIndex, difficultiesEmpty);
    _argIndex = 3 + _inputSize;
    for (String _item_1 : difficulties) {
      _statement.bindString(_argIndex, _item_1);
      _argIndex++;
    }
    _argIndex = 3 + _inputSize + _inputSize_1;
    _statement.bindString(_argIndex, query);
    _argIndex = 4 + _inputSize + _inputSize_1;
    _statement.bindString(_argIndex, query);
    _argIndex = 5 + _inputSize + _inputSize_1;
    _statement.bindString(_argIndex, query);
    _argIndex = 6 + _inputSize + _inputSize_1;
    _statement.bindString(_argIndex, query);
    _argIndex = 7 + _inputSize + _inputSize_1;
    _statement.bindLong(_argIndex, figurOnly);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "imageData");
          final int _cursorIndexOfFullPageImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "fullPageImageData");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfSubtopic = CursorUtil.getColumnIndexOrThrow(_cursor, "subtopic");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfSourcePdf = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePdf");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfHasFigure = CursorUtil.getColumnIndexOrThrow(_cursor, "hasFigure");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item_2;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final byte[] _tmpImageData;
            if (_cursor.isNull(_cursorIndexOfImageData)) {
              _tmpImageData = null;
            } else {
              _tmpImageData = _cursor.getBlob(_cursorIndexOfImageData);
            }
            final byte[] _tmpFullPageImageData;
            if (_cursor.isNull(_cursorIndexOfFullPageImageData)) {
              _tmpFullPageImageData = null;
            } else {
              _tmpFullPageImageData = _cursor.getBlob(_cursorIndexOfFullPageImageData);
            }
            final String _tmpTopic;
            _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            final String _tmpSubtopic;
            _tmpSubtopic = _cursor.getString(_cursorIndexOfSubtopic);
            final Difficulty _tmpDifficulty;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.stringToDifficulty(_tmp);
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToList(_tmp_1);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpSourcePdf;
            _tmpSourcePdf = _cursor.getString(_cursorIndexOfSourcePdf);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpHasFigure;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfHasFigure);
            _tmpHasFigure = _tmp_2 != 0;
            final QuestionType _tmpQuestionType;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp_3);
            _item_2 = new Question(_tmpId,_tmpText,_tmpImageData,_tmpFullPageImageData,_tmpTopic,_tmpSubtopic,_tmpDifficulty,_tmpOptions,_tmpCorrectAnswer,_tmpSourcePdf,_tmpPageNumber,_tmpCreatedAt,_tmpHasFigure,_tmpQuestionType);
            _result.add(_item_2);
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
  public Object getAllTopics(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT topic FROM questions ORDER BY topic";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object getSubtopics(final String topic,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT subtopic FROM questions WHERE topic = ? ORDER BY subtopic";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, topic);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object getTotalCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getCountByDifficulty(final String difficulty,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM questions WHERE difficulty = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, difficulty);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getTopicStats(final Continuation<? super List<TopicStatsRaw>> $completion) {
    final String _sql = "\n"
            + "        SELECT topic, subtopic, \n"
            + "               COUNT(*) as totalCount,\n"
            + "               SUM(CASE WHEN difficulty = 'EASY' THEN 1 ELSE 0 END) as easyCount,\n"
            + "               SUM(CASE WHEN difficulty = 'MEDIUM' THEN 1 ELSE 0 END) as mediumCount,\n"
            + "               SUM(CASE WHEN difficulty = 'HARD' THEN 1 ELSE 0 END) as hardCount\n"
            + "        FROM questions \n"
            + "        GROUP BY topic, subtopic\n"
            + "        ORDER BY topic, subtopic\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TopicStatsRaw>>() {
      @Override
      @NonNull
      public List<TopicStatsRaw> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTopic = 0;
          final int _cursorIndexOfSubtopic = 1;
          final int _cursorIndexOfTotalCount = 2;
          final int _cursorIndexOfEasyCount = 3;
          final int _cursorIndexOfMediumCount = 4;
          final int _cursorIndexOfHardCount = 5;
          final List<TopicStatsRaw> _result = new ArrayList<TopicStatsRaw>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TopicStatsRaw _item;
            final String _tmpTopic;
            _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            final String _tmpSubtopic;
            _tmpSubtopic = _cursor.getString(_cursorIndexOfSubtopic);
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpEasyCount;
            _tmpEasyCount = _cursor.getInt(_cursorIndexOfEasyCount);
            final int _tmpMediumCount;
            _tmpMediumCount = _cursor.getInt(_cursorIndexOfMediumCount);
            final int _tmpHardCount;
            _tmpHardCount = _cursor.getInt(_cursorIndexOfHardCount);
            _item = new TopicStatsRaw(_tmpTopic,_tmpSubtopic,_tmpTotalCount,_tmpEasyCount,_tmpMediumCount,_tmpHardCount);
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
  public Object getRandomQuestions(final List<String> topics, final int topicsEmpty,
      final List<String> difficulties, final int difficultiesEmpty, final int limit,
      final Continuation<? super List<Question>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM questions");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = 1 OR topic IN (");
    final int _inputSize = topics.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("          AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = 1 OR difficulty IN (");
    final int _inputSize_1 = difficulties.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY RANDOM()");
    _stringBuilder.append("\n");
    _stringBuilder.append("        LIMIT ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 3 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, topicsEmpty);
    _argIndex = 2;
    for (String _item : topics) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    _argIndex = 2 + _inputSize;
    _statement.bindLong(_argIndex, difficultiesEmpty);
    _argIndex = 3 + _inputSize;
    for (String _item_1 : difficulties) {
      _statement.bindString(_argIndex, _item_1);
      _argIndex++;
    }
    _argIndex = 3 + _inputSize + _inputSize_1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "imageData");
          final int _cursorIndexOfFullPageImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "fullPageImageData");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfSubtopic = CursorUtil.getColumnIndexOrThrow(_cursor, "subtopic");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfSourcePdf = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePdf");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfHasFigure = CursorUtil.getColumnIndexOrThrow(_cursor, "hasFigure");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item_2;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final byte[] _tmpImageData;
            if (_cursor.isNull(_cursorIndexOfImageData)) {
              _tmpImageData = null;
            } else {
              _tmpImageData = _cursor.getBlob(_cursorIndexOfImageData);
            }
            final byte[] _tmpFullPageImageData;
            if (_cursor.isNull(_cursorIndexOfFullPageImageData)) {
              _tmpFullPageImageData = null;
            } else {
              _tmpFullPageImageData = _cursor.getBlob(_cursorIndexOfFullPageImageData);
            }
            final String _tmpTopic;
            _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            final String _tmpSubtopic;
            _tmpSubtopic = _cursor.getString(_cursorIndexOfSubtopic);
            final Difficulty _tmpDifficulty;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.stringToDifficulty(_tmp);
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToList(_tmp_1);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpSourcePdf;
            _tmpSourcePdf = _cursor.getString(_cursorIndexOfSourcePdf);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpHasFigure;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfHasFigure);
            _tmpHasFigure = _tmp_2 != 0;
            final QuestionType _tmpQuestionType;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp_3);
            _item_2 = new Question(_tmpId,_tmpText,_tmpImageData,_tmpFullPageImageData,_tmpTopic,_tmpSubtopic,_tmpDifficulty,_tmpOptions,_tmpCorrectAnswer,_tmpSourcePdf,_tmpPageNumber,_tmpCreatedAt,_tmpHasFigure,_tmpQuestionType);
            _result.add(_item_2);
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
  public Object getByIds(final List<String> ids,
      final Continuation<? super List<Question>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM questions WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : ids) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "imageData");
          final int _cursorIndexOfFullPageImageData = CursorUtil.getColumnIndexOrThrow(_cursor, "fullPageImageData");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfSubtopic = CursorUtil.getColumnIndexOrThrow(_cursor, "subtopic");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
          final int _cursorIndexOfSourcePdf = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePdf");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfHasFigure = CursorUtil.getColumnIndexOrThrow(_cursor, "hasFigure");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final byte[] _tmpImageData;
            if (_cursor.isNull(_cursorIndexOfImageData)) {
              _tmpImageData = null;
            } else {
              _tmpImageData = _cursor.getBlob(_cursorIndexOfImageData);
            }
            final byte[] _tmpFullPageImageData;
            if (_cursor.isNull(_cursorIndexOfFullPageImageData)) {
              _tmpFullPageImageData = null;
            } else {
              _tmpFullPageImageData = _cursor.getBlob(_cursorIndexOfFullPageImageData);
            }
            final String _tmpTopic;
            _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
            final String _tmpSubtopic;
            _tmpSubtopic = _cursor.getString(_cursorIndexOfSubtopic);
            final Difficulty _tmpDifficulty;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.stringToDifficulty(_tmp);
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToList(_tmp_1);
            final String _tmpCorrectAnswer;
            if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
              _tmpCorrectAnswer = null;
            } else {
              _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
            }
            final String _tmpSourcePdf;
            _tmpSourcePdf = _cursor.getString(_cursorIndexOfSourcePdf);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpHasFigure;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfHasFigure);
            _tmpHasFigure = _tmp_2 != 0;
            final QuestionType _tmpQuestionType;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp_3);
            _item_1 = new Question(_tmpId,_tmpText,_tmpImageData,_tmpFullPageImageData,_tmpTopic,_tmpSubtopic,_tmpDifficulty,_tmpOptions,_tmpCorrectAnswer,_tmpSourcePdf,_tmpPageNumber,_tmpCreatedAt,_tmpHasFigure,_tmpQuestionType);
            _result.add(_item_1);
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
