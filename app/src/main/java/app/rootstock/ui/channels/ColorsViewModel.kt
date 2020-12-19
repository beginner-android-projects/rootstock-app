package app.rootstock.ui.channels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.data.channel.ColorsDelegate
import app.rootstock.data.channel.ImageUrls
import app.rootstock.data.network.ResponseResult
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@FragmentScoped
class ColorsViewModel @ViewModelInject constructor(
    private val colorsDelegate: ColorsDelegate,
) :
    ViewModel() {

    private val _images = MutableLiveData<ImageUrls>()
    val images: LiveData<ImageUrls> get() = _images

    init {
        viewModelScope.launch {
            when (val response = colorsDelegate.getColors().first()) {
                is ResponseResult.Success -> {
                    _images.value = response.data
                }
                is ResponseResult.Error -> {
                }
            }
        }
    }


}