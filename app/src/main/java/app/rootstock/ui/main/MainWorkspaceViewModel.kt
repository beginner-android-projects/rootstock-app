package app.rootstock.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.user.UserRepository
import app.rootstock.data.workspace.Workspace
import app.rootstock.ui.workspace.WorkspaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainWorkspaceViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository
) :
    ViewModel() {

    val user = userRepository.getUser()

    private val _workspace = MutableLiveData<Workspace>()
    val workspace: LiveData<Workspace> get() = _workspace

    init {
        viewModelScope.launch {
            val workspaceId = "3835881e-11ca-4ed8-bcde-845ef7ec549e"
            workspaceRepository.getMainWorkspace(workspaceId).collect {
                when (it) {
                    is ResponseResult.Success -> {
                    }
                    is ResponseResult.Error -> {
                    }
                }
            }
        }
    }

}