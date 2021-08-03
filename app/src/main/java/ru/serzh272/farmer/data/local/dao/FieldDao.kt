package ru.serzh272.farmer.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.serzh272.farmer.data.local.entities.Field

@Dao
interface FieldDao : BaseDao<Field> {
    @Query(
        """
        SELECT * FROM fields
        WHERE id = :id
    """
    )
    suspend fun getFieldById(id: Int): Field

    @Query(
        """
        SELECT * FROM fields
    """
    )
    fun getFields(): LiveData<MutableList<Field>>

    @Insert
    suspend fun addField(field:Field)

    @Query("""
        DELETE FROM fields WHERE id=:id
    """)
    suspend fun deleteFieldById(id: Int)

    @Transaction
    suspend fun insertToFields(usersList: List<Field>) {
        insert((usersList))
            .mapIndexed{index, l -> if (l ==-1L) usersList[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}