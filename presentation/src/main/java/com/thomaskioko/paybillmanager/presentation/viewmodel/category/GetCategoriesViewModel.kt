package com.thomaskioko.paybillmanager.presentation.viewmodel.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thomaskioko.paybillmanager.domain.interactor.category.GetCategories
import com.thomaskioko.paybillmanager.domain.model.Category
import com.thomaskioko.paybillmanager.presentation.mapper.CategoryViewMapper
import com.thomaskioko.paybillmanager.presentation.model.CategoryView
import com.thomaskioko.paybillmanager.presentation.state.Resource
import com.thomaskioko.paybillmanager.presentation.state.ResourceState
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

open class GetCategoriesViewModel @Inject internal constructor(
        private val getBills: GetCategories?,
        private val mapper: CategoryViewMapper
) : ViewModel() {

    open val categoriesLiveData: MutableLiveData<Resource<List<CategoryView>>> = MutableLiveData()


    override fun onCleared() {
        getBills?.dispose()
        super.onCleared()
    }

    open fun getCategories(): LiveData<Resource<List<CategoryView>>> {
        return categoriesLiveData
    }


    fun fetchCategories() {
        categoriesLiveData.postValue(Resource(ResourceState.LOADING, null, null))
        getBills?.execute(CategoriesSubscriber())
    }


    inner class CategoriesSubscriber : DisposableSubscriber<List<Category>>() {
        override fun onNext(t: List<Category>) {
            categoriesLiveData.postValue(Resource(ResourceState.SUCCESS,
                    t.map { mapper.mapToView(it) }, null))
        }

        override fun onComplete() {}

        override fun onError(e: Throwable) {
            categoriesLiveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }


}