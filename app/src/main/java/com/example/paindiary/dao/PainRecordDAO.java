package com.example.paindiary.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.paindiary.entity.PainRecord;

import java.util.List;

@Dao
public interface PainRecordDAO {
    @Query("SELECT * FROM painrecord ORDER BY date DESC")
    LiveData<List<PainRecord>> getAll();

    @Query("SELECT * FROM painrecord WHERE uid = :painrecordId LIMIT 1")
    PainRecord findById(int painrecordId);

    @Query("SELECT * FROM painrecord WHERE date = :painrecordDate LIMIT 1")
    PainRecord findByDate(String painrecordDate);

    @Query("SELECT * FROM painrecord WHERE location = :painrecordLocation")
    LiveData<List<PainRecord>> getAllByLocation(String painrecordLocation);

    @Insert
    void insert(PainRecord painrecord);

    @Delete
    void delete(PainRecord painrecord);

    @Update
    void update(PainRecord painrecord);

    @Query("DELETE FROM painrecord")
    void deleteAll();
}
