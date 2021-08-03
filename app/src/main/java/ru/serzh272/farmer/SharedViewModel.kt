package ru.serzh272.farmer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.serzh272.farmer.data.local.AppDb
import ru.serzh272.farmer.data.local.DbManager
import ru.serzh272.farmer.data.local.dao.FieldDao
import ru.serzh272.farmer.data.local.entities.Field
import ru.serzh272.farmer.models.MapState
import ru.serzh272.farmer.models.SearchMapObject
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val fieldDao: FieldDao):ViewModel() {
    private val stateLiveData by lazy { MutableLiveData(MapState()) }
    private val searchResultsLiveData by lazy { MutableLiveData(listOf<SearchMapObject>()) }
    fun getFields(): LiveData<MutableList<Field>> {
        return fieldDao.getFields()
    }

    fun deleteFieldById(id: Int, result: (Field?) -> Unit) {
        viewModelScope.launch {
            val field = fieldDao.getFieldById(id)
            fieldDao.deleteFieldById(id)
            result(field)
        }
    }
    fun addField(f: Field){
        viewModelScope.launch {
            fieldDao.addField(f)
        }
    }

    fun getState():MutableLiveData<MapState>{
        return stateLiveData
    }

    fun handleChangeEditMode(isEdit: Boolean) {
        val state = stateLiveData.value
        if (state != null) {
            stateLiveData.value = state.copy(isEditMode = isEdit, isSearchMode = if (isEdit) !isEdit else state.isSearchMode)
        }
    }

    fun getSearchResult():MutableLiveData<List<SearchMapObject>>{
        return searchResultsLiveData
    }

    fun updateSearchResult(res: List<SearchMapObject>){
        searchResultsLiveData.value = res
    }

    fun handleSearchMode(isSearch: Boolean) {
        val state = stateLiveData.value
        if (state != null) {
            stateLiveData.value = state.copy(isSearchMode = isSearch)
        }
    }
}