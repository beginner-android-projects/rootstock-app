package app.rootstock.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.rootstock.data.user.User
import app.rootstock.data.user.UserRepository
import app.rootstock.data.workspace.Workspace
import app.rootstock.ui.workspace.WorkspaceRepository
import kotlinx.coroutines.launch

class MainWorkspaceViewModel @ViewModelInject constructor(
    private val repository: UserRepository,
    private val workspaceRepository: WorkspaceRepository
) :
    ViewModel() {

    val user = repository.getUser()

    val workspaces = workspaceRepository.getMainWorkspace()


//    private fun updateMainWorkspaces() {
//        workspaceRepository.getWorkspace(user.)
//    }
}