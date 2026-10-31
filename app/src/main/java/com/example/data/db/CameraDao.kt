package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CameraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraDao {
    @Query("SELECT * FROM cameras ORDER BY isFavorite DESC, orderIndex ASC, name ASC")
    fun getAllCameras(): Flow<List<CameraEntity>>

    @Query("SELECT * FROM cameras WHERE isFavorite = 1 ORDER BY orderIndex ASC")
    fun getFavoriteCameras(): Flow<List<CameraEntity>>

    @Query("SELECT * FROM cameras WHERE locationName = :location ORDER BY orderIndex ASC, name ASC")
    fun getCamerasByLocation(location: String): Flow<List<CameraEntity>>

    @Query("SELECT * FROM cameras WHERE id = :id LIMIT 1")
    suspend fun getCameraById(id: String): CameraEntity?

    @Query("SELECT COUNT(*) FROM cameras WHERE isDemo = 0")
    fun getNonDemoCameraCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cameras")
    suspend fun getTotalCameraCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCamera(camera: CameraEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameras(cameras: List<CameraEntity>)

    @Update
    suspend fun updateCamera(camera: CameraEntity)

    @Delete
    suspend fun deleteCamera(camera: CameraEntity)

    @Query("DELETE FROM cameras WHERE id = :id")
    suspend fun deleteCameraById(id: String)

    @Query("DELETE FROM cameras WHERE isDemo = 1")
    suspend fun deleteDemoCameras()

    @Query("UPDATE cameras SET locationName = :newLocation WHERE locationName = :oldLocation")
    suspend fun reassignCamerasLocation(oldLocation: String, newLocation: String)
}
