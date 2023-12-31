package com.example.prototipo_canino

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.prototipo_canino.databinding.FragmentHomeBinding
import com.example.prototipo_canino.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class HomeFragment : Fragment(), FragmentAux {

    private lateinit var mBinding: FragmentHomeBinding

    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mSnapshotsRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater,container,false)
        return mBinding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFirebase()
        setupAdapter()
        setupRecyclerView()
    }

    private fun setupFirebase(){
        mSnapshotsRef = FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
    }



    private fun setupAdapter(){
        val query = mSnapshotsRef

        val options = FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query){
            val snapshot = it.getValue(Snapshot::class.java)
            snapshot!!.id = it.key!!
            snapshot
        }.build()

        mFirebaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>(options){

            private lateinit var mContext: Context

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                mContext= parent.context
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_snapshot, parent,false)
                return SnapshotHolder(view)
            }


            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                val snapshot = getItem(position)

                with(holder){
                    setListener(snapshot)

                    with(binding){
                        tvTitle.text = snapshot.title
                        cbLike.text= snapshot.likeList.keys.size.toString()
                        cbLike.isChecked = snapshot.likeList
                            .containsKey(SnapshotsApplication.currentUser.uid)

                        Glide.with(mContext)
                            .load(snapshot.photoUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(imgPhoto)

                        btnDelete.visibility = if (model.ownerUid == SnapshotsApplication.currentUser.uid){
                            View.VISIBLE
                        }else{
                            View.INVISIBLE
                        }
                        //Comentarios
                        mSnapshotsRef.child(snapshot.id).child("comentarios")
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(dataSnapshot: DataSnapshot){
                                    val listaComentarios = dataSnapshot.children.map{ comentarioSnapshot ->
                                        comentarioSnapshot.getValue() as Map<String, String>
                                    }
                                    val comentarioAdapter = comentarioAdapter(listaComentarios)
                                    comentariosRecyclerView.adapter = comentarioAdapter
                                    comentariosRecyclerView.layoutManager = LinearLayoutManager(mContext)
                                }

                                override fun onCancelled(databaseError: DatabaseError ){
                                    //manejo de errores
                                }
                            })

                    }

                }
            }

            @SuppressLint("NotifyDataSetChanged")//error interno firebase ui 8.0.0
            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progresBar.visibility = View.GONE
                notifyDataSetChanged()// cambio de panorama
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Snackbar.make(mBinding.root, error.message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(){
        mLayoutManager = LinearLayoutManager(context)

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFirebaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAdapter.stopListening()
    }

    private fun deleteSnapshot(snapshot: Snapshot){
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.dialog_delete_title)
                .setPositiveButton(R.string.dialog_delete_confirm){_,_ ->
                    val storageSnapshotsRef = FirebaseStorage.getInstance().reference
                        .child(SnapshotsApplication.PATH_SNAPSHOTS)
                        .child(SnapshotsApplication.currentUser.uid)
                        .child(snapshot.id)
                    storageSnapshotsRef.delete().addOnCompleteListener {result ->
                        if(result.isSuccessful){
                            mSnapshotsRef.child(snapshot.id).removeValue()
                        }else{
                            Snackbar.make(mBinding.root, getString(R.string.home_delte_photo_error),
                                Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton(R.string.dialog_delete_cancel, null)
                .show()
        }
    }

    private fun setLike(snapshot: Snapshot,checked: Boolean){
        val myUserRef = mSnapshotsRef.child(snapshot.id)
            .child(SnapshotsApplication.PROPERTY_LIKE_LIST)
            .child(SnapshotsApplication.currentUser.uid)

        if(checked){
            myUserRef.setValue(checked)
        }else{
            myUserRef.setValue(null)
        }
    }

    //FragmentAux
    override fun refresh(){
        mBinding.recyclerView.smoothScrollToPosition(0)
    }

    inner class SnapshotHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot) {
            with(binding) {
                btnDelete.setOnClickListener { deleteSnapshot(snapshot) }

                cbLike.setOnCheckedChangeListener { _, checked ->
                    setLike(snapshot, checked)
                }

                btnComentar.setOnClickListener {
                    val comentario = etInputComentario.text.toString().trim()
                    if (comentario.isNotEmpty()){
                        publicarComentario(snapshot,comentario)
                        etInputComentario.text.clear()
                    }
                    else{
                        Snackbar.make(binding.root, "Por favor, escribe un comentario.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //comentatios
    inner class comentarioHolder(view:View) : RecyclerView.ViewHolder(view){
        val tvComentarioUsuario: TextView = view.findViewById(R.id.tvNombre_Usuario)
        val tvComentario: TextView = view.findViewById(R.id.tvComentario)
    }

    inner class comentarioAdapter(private val comentarios: List<Map<String,String>>):
    RecyclerView.Adapter<comentarioHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): comentarioHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comments,parent,false)
            return  comentarioHolder(view)
        }

        override fun onBindViewHolder(holder: comentarioHolder, position: Int) {
            val comentario = comentarios[position]
            holder.tvComentarioUsuario.text = comentario["userName"]
            holder.tvComentario.text = comentario["comentario"]
        }

        override fun getItemCount(): Int {
            return comentarios.size
        }
    }

    //metodos para subir comentarios
    private fun publicarComentario(snapshot: Snapshot,comentario:String){
        val comentarioRef = mSnapshotsRef.child(snapshot.id)
            .child("comentarios")
            .push()

        val comentarioData = mapOf(
            "userId" to SnapshotsApplication.currentUser.uid,
            "userName" to SnapshotsApplication.currentUser.displayName,
            "comentario" to comentario
        )
        comentarioRef.setValue(comentarioData)
    }




}