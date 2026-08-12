package com.example.okey101.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GameDao_Impl implements GameDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Game> __insertionAdapterOfGame;

  private final EntityDeletionOrUpdateAdapter<Game> __deletionAdapterOfGame;

  private final EntityDeletionOrUpdateAdapter<Game> __updateAdapterOfGame;

  public GameDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGame = new EntityInsertionAdapter<Game>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `games` (`gameId`,`date`,`hostName`,`isFinished`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Game entity) {
        statement.bindLong(1, entity.getGameId());
        statement.bindLong(2, entity.getDate());
        statement.bindString(3, entity.getHostName());
        final int _tmp = entity.isFinished() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__deletionAdapterOfGame = new EntityDeletionOrUpdateAdapter<Game>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `games` WHERE `gameId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Game entity) {
        statement.bindLong(1, entity.getGameId());
      }
    };
    this.__updateAdapterOfGame = new EntityDeletionOrUpdateAdapter<Game>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `games` SET `gameId` = ?,`date` = ?,`hostName` = ?,`isFinished` = ? WHERE `gameId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Game entity) {
        statement.bindLong(1, entity.getGameId());
        statement.bindLong(2, entity.getDate());
        statement.bindString(3, entity.getHostName());
        final int _tmp = entity.isFinished() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getGameId());
      }
    };
  }

  @Override
  public Object insertGame(final Game game, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGame.insertAndReturnId(game);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteGame(final Game game, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfGame.handle(game);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateGame(final Game game, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGame.handle(game);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Game>> getAllGames() {
    final String _sql = "SELECT * FROM games ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"games"}, new Callable<List<Game>>() {
      @Override
      @NonNull
      public List<Game> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHostName = CursorUtil.getColumnIndexOrThrow(_cursor, "hostName");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final List<Game> _result = new ArrayList<Game>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Game _item;
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpHostName;
            _tmpHostName = _cursor.getString(_cursorIndexOfHostName);
            final boolean _tmpIsFinished;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp != 0;
            _item = new Game(_tmpGameId,_tmpDate,_tmpHostName,_tmpIsFinished);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllGamesSync(final Continuation<? super List<Game>> $completion) {
    final String _sql = "SELECT * FROM games";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Game>>() {
      @Override
      @NonNull
      public List<Game> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHostName = CursorUtil.getColumnIndexOrThrow(_cursor, "hostName");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final List<Game> _result = new ArrayList<Game>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Game _item;
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpHostName;
            _tmpHostName = _cursor.getString(_cursorIndexOfHostName);
            final boolean _tmpIsFinished;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp != 0;
            _item = new Game(_tmpGameId,_tmpDate,_tmpHostName,_tmpIsFinished);
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
  public Object getGame(final long gameId, final Continuation<? super Game> $completion) {
    final String _sql = "SELECT * FROM games WHERE gameId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Game>() {
      @Override
      @NonNull
      public Game call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHostName = CursorUtil.getColumnIndexOrThrow(_cursor, "hostName");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final Game _result;
          if (_cursor.moveToFirst()) {
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpHostName;
            _tmpHostName = _cursor.getString(_cursorIndexOfHostName);
            final boolean _tmpIsFinished;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp != 0;
            _result = new Game(_tmpGameId,_tmpDate,_tmpHostName,_tmpIsFinished);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
