package app.rootstock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.data.workspace.Workspace
import app.rootstock.databinding.ItemWorkspaceBinding

interface WorkspaceEventHandler {
    fun workspaceClicked(workspaceId: String)
}

class WorkspaceListAdapter constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val workspaces: LiveData<List<Workspace>>,
    private val workspaceEventHandler: WorkspaceEventHandler

) :
    androidx.recyclerview.widget.ListAdapter<Workspace, WorkspaceListAdapter.WorkspaceViewHolder>(
        object :
            DiffUtil.ItemCallback<Workspace>() {

            override fun areItemsTheSame(oldItem: Workspace, newItem: Workspace): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Workspace, newItem: Workspace): Boolean {
                return oldItem == newItem
            }

        }) {

    inner class WorkspaceViewHolder constructor(
        private val binding: ItemWorkspaceBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val workspaces: LiveData<List<Workspace>>,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Workspace, position: Int) {
            binding.workspaces = workspaces
            binding.positionIndex = position
            binding.workspaceEventHandler = workspaceEventHandler
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        val binding = ItemWorkspaceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WorkspaceViewHolder(binding, lifecycleOwner, workspaces)
    }

    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        holder.bind(getItem(position), position)

    }
}
