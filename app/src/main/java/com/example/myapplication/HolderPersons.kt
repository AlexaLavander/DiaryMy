package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import pl.droidsonroids.gif.GifImageView

class HolderPersons(val item: View) : RecyclerView.ViewHolder(item) {

    fun setDetails(
        username: String,
        uid: String,
        accountImageView: String,
        accountImageType: String,
        activity: Activity,
        title: String,
        token: String,
        status: String,
        titleKey: String
    ) {


        item.setOnLongClickListener {
            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(activity.intent.extras!!.getString("titleKey").toString())
                .child("partners").get().addOnSuccessListener { it1 ->
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(activity.intent.extras!!.getString("titleKey").toString())
                        .child("partners").get().addOnSuccessListener {
                            if (it.value.toString().contains(uid) || it1.value.toString()
                                    .contains(uid)
                            ) {


                                val dialog_layout =
                                    (activity).layoutInflater.inflate(R.layout.delete, null)
                                dialog_layout.findViewById<TextView>(R.id.yes).setText("Удалить")
                                dialog_layout.findViewById<TextView>(R.id.no).setText("Оставить")
                                dialog_layout.findViewById<TextView>(R.id.message)
                                    .setText("Вы хотите удалить его из списка партнёров?")

                                val alert = AlertDialog.Builder(activity)
                                alert.setView(dialog_layout)
                                val show = alert.show()
                                dialog_layout.findViewById<TextView>(R.id.yes).setOnClickListener {
                                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                                        .get().addOnSuccessListener {
                                            for (user in it.children) {
                                                if (user.child(titleKey).value.toString() != "null") {
                                                }
                                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                    .child(user.key.toString())
                                                    .child(titleKey)
                                                    .child("partners").child(uid).removeValue()
                                                FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                    .child(user.key.toString())
                                                    .child(titleKey)
                                                    .child("partners").child(uid).removeValue()
                                            }
                                        }
                                    item.findViewById<FloatingActionButton>(R.id.addPerson)
                                        .visibility = View.VISIBLE
                                    show.dismiss()


                                }

                                dialog_layout.findViewById<TextView>(R.id.no).setOnClickListener {
                                    show.dismiss()

                                }

                                changeTheme(
                                    null,
                                    null,
                                    listOf(
                                        dialog_layout.findViewById(R.id.no),
                                        dialog_layout.findViewById(R.id.yes),
                                        dialog_layout.findViewById(R.id.message)
                                    ),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    activity,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                                )


                                true

                            }
                        }
                }
            true
        }
        item.findViewById<TextView>(R.id.username).setText(username)
        item.findViewById<TextView>(R.id.status).setText(status)

        if (accountImageType == "gif") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.VISIBLE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.GONE
            if (accountImageView != "null") {
                Glide.with(activity.applicationContext).load(accountImageView).circleCrop()
                    .into(item.findViewById<GifImageView>(R.id.gif))
            }
        }
        changeTheme(
            null,
            null,
            null,
            null,
            item.findViewById<GifImageView>(R.id.gif),
            null,
            null,
            null,
            null,
            activity,
            null,
            null,
            null,
            null,
            null
        )
        if (accountImageType == "image") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.GONE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.VISIBLE

            if (accountImageView != "null") {
                Glide.with(activity.applicationContext).load(accountImageView)
                    .into(item.findViewById<GifImageView>(R.id.image))
            }
        }

        item.findViewById<TextView>(R.id.username).setText(username)
        item.findViewById<FloatingActionButton>(R.id.addPerson).setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(uid)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(it.value)
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(uid)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(title)
                        .setValue("note")
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(uid)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("key")
                        .setValue(titleKey)
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(uid)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("author")
                        .setValue(FirebaseAuth.getInstance().currentUser!!.uid)


                    item.findViewById<FloatingActionButton>(R.id.addPerson).visibility =
                        View.GONE

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                        .addOnSuccessListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                sendNotification(
                                    uid,
                                    title,
                                    it.child("username").value.toString(),
                                    token,
                                    activity
                                )
                            }
                        }


                }

        }

        changeTheme(
            constraintLayout = null,
            flb = listOf(
                item.findViewById<FloatingActionButton>(R.id.addPerson)
            ),
            text = listOf(
                item.findViewById<TextView>(R.id.username),
                item.findViewById<TextView>(R.id.status),
                item.findViewById(R.id.message)
            ),
            image = null,
            gif = null,
            circle = null,
            cardView = null,
            materialToolbar = null,
            btn = null,
            context = activity,
            navigation = null,
            materialWriteRead = null,
            materialRead = null,
            nothing = null,
            nothing1 = null
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(
        uid: String,
        title: String,
        username: String,
        token: String,
        activity: Activity
    ) {
        val notificationSender = FcmNotificationSender(
            token,
            "Партнёры",
            "$username прелагает вам присоединиться к заметке $title",
            activity.applicationContext,
            activity
        )

        notificationSender.SendNotifications()

    }
}


class HolderNotification(val item: View) : RecyclerView.ViewHolder(item) {

    private fun sendNotificationAccept(
        uid: String,
        title: String,
        username: String,
        token: String,
        activity: Activity,
        newTitle: String
    ) {
        val notificationSender = FcmNotificationSender(
            token,
            "Партнёр",
            "$username присоединился к заметке $newTitle",
            activity.applicationContext,
            activity
        )

        notificationSender.SendNotifications()

    }

    fun setDetails(
        username: String,
        uid: String,
        accountImage: String,
        accountImageType: String,
        activity: Activity,
        token: String,
        status: String,
        newTitle: String,
        title: String,
        key: String?
    ) {


        item.findViewById<TextView>(R.id.username).setText(username)
        item.findViewById<TextView>(R.id.storyText).setText(status)

        if (accountImageType == "gif") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.VISIBLE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.GONE

            Glide.with(activity).load(accountImage).circleCrop()
                .into(item.findViewById<GifImageView>(R.id.gif))
        }
        if (accountImageType == "image") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.GONE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.VISIBLE

            Glide.with(activity).load(accountImage)
                .into(item.findViewById<GifImageView>(R.id.image))
        }
        FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(uid)
            .get().addOnSuccessListener { value ->
                if (value.child("friend").value.toString() == "waiting") {
                    item.findViewById<TextView>(R.id.message).setText("хочет с вами подружиться ")
                    item.findViewById<FloatingActionButton>(R.id.requestAccept).setOnClickListener {
                        FirebaseDatabase.getInstance().reference.child("Users").child(uid).get()
                            .addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("Friends")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid).child(uid)
                                    .setValue(it.value)
                            }
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                            .addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("Friends").child(uid)
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(it.value)

                            }
                        FirebaseDatabase.getInstance().reference.child("Notifications")
                            .child(uid)
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                            .addOnSuccessListener {
                                var values = listOf<String>()
                                for (note in it.children) {
                                    values = values + note.key.toString()
                                }
                                if ("note" in values) {
                                    FirebaseDatabase.getInstance().reference.child("Notifications")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(uid).child("friend").removeValue()

                                } else {
                                    FirebaseDatabase.getInstance().reference.child("Notifications")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(uid).removeValue()

                                }
                            }

                    }
                    item.findViewById<FloatingActionButton>(R.id.cancel).setOnClickListener {
                        FirebaseDatabase.getInstance().reference.child("Notifications")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(uid)
                            .removeValue()
                    }


                }
                for (invite in value.children) {
                    if (invite.value.toString() == "note") {
                        item.findViewById<TextView>(R.id.message)
                            .setText("приглашает в ${invite.key.toString()} ")

                        item.findViewById<FloatingActionButton>(R.id.requestAccept)
                            .setOnClickListener {
                                var info: Any?
                                FirebaseDatabase.getInstance().reference.child(
                                    "UserNotesSketches"
                                )
                                    .child(uid)
                                    .child(key.toString())
                                    .get().addOnSuccessListener { it ->
                                        if (it.value.toString() != "null") {
                                            FirebaseDatabase.getInstance().reference.child(
                                                "UserNotesSketches"
                                            )
                                                .child(uid)
                                                .child(key.toString())
                                                .get()
                                                .addOnSuccessListener { it1 ->
                                                    info = it1.value
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotes"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(key.toString())
                                                        .setValue(info)
                                                }
                                            FirebaseDatabase.getInstance().reference.child(
                                                "Users"
                                            )
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .get().addOnSuccessListener { it1 ->
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotesSketches"
                                                    )
                                                        .child(uid)
                                                        .child(key.toString())
                                                        .child("partners")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .setValue(it1.value)

                                                }
                                            FirebaseDatabase.getInstance().reference.child(
                                                "UserNotesSketches"
                                            )
                                                .child(uid)
                                                .child(key.toString())
                                                .get().addOnSuccessListener {
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotes"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(value.child("key").value.toString())
                                                        .setValue(it.value)


                                                }

                                        } else {
                                            FirebaseDatabase.getInstance().reference.child(
                                                "Users"
                                            )
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .get().addOnSuccessListener { it1 ->
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotesSketches"
                                                    )
                                                        .child(uid)
                                                        .child(key.toString())
                                                        .child("partners")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .setValue(it1.value)

                                                }
                                            FirebaseDatabase.getInstance().reference.child(
                                                "UserNotes"
                                            )
                                                .child(uid)
                                                .child(key.toString())
                                                .get()
                                                .addOnSuccessListener {
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotes"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(key.toString())
                                                        .setValue(it.value)
                                                }



                                    }
                                var list = listOf<String>()
                                FirebaseDatabase.getInstance().reference.child("UserNotes")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .get()
                                    .addOnSuccessListener {
                                        for (note in it.children) {
                                            list = list + note.key.toString()
                                        }
                                        val index = (list.indexOf(key))
                                        var indexes = listOf<Int>()
                                        for (notes in list) {
                                            indexes += list.indexOf(notes)
                                        }

                                        if (index == indexes.max()) {
                                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                .child(uid)
                                                .child(key.toString()).child("data").get()
                                                .addOnSuccessListener {
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "Dates"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(
                                                            it.value.toString()
                                                                .replace("/", "")
                                                                .toString()
                                                        )
                                                        .setValue(key)


                                                }

                                        }

                                    }



                                FirebaseDatabase.getInstance().reference.child(
                                    "Notifications"
                                )
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(uid).child("friend").get()
                                    .addOnSuccessListener {
                                        if (it.value.toString() == "null") {
                                            FirebaseDatabase.getInstance().reference.child(
                                                "Notifications"
                                            )
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(uid)
                                                .removeValue()
                                        } else {
                                            FirebaseDatabase.getInstance().reference.child(
                                                "Notifications"
                                            )
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(uid)
                                                .child(value.child("key").value.toString())
                                                .removeValue()
                                        }
                                    }


                            }
                        val material =
                            activity.findViewById<MaterialToolbar>(R.id.materialToolbar)
                        material.menu.getItem(0).setIcon(R.drawable.notif)


                        sendNotificationAccept(
                            uid,
                            invite.key.toString(),
                            username,
                            token,
                            activity,
                            newTitle
                        )


                    }
                    item.findViewById<FloatingActionButton>(R.id.cancel)
                        .setOnClickListener {
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(uid)
                                .removeValue()
                            val material =
                                activity.findViewById<MaterialToolbar>(R.id.materialToolbar)
                            material.menu.getItem(0).setIcon(R.drawable.notif)

                        }

                }
            }
    }


    changeTheme(
    null,
    listOf(item.findViewById(R.id.cancel), item.findViewById(R.id.requestAccept)),
    listOf(
    item.findViewById<TextView>(R.id.username),
    item.findViewById<TextView>(R.id.storyText),
    item.findViewById<TextView>(R.id.message)
    ),
    null,
    item.findViewById(R.id.gif),
    null,
    listOf(item.findViewById(R.id.card_view)),
    null,
    null,
    activity,
    null,
    null,
    null,
    null,
    null
    )

}
}

class FriendsHolder(val item: View) : RecyclerView.ViewHolder(item) {

    fun setDetails(
        username: String,
        uid: String,
        accountImageView: String,
        accountImageType: String,
        activity: Activity,
        token: String,
        status: String
    ) {
        item.findViewById<TextView>(R.id.username).setText(username)
        item.findViewById<TextView>(R.id.status).setText(status)

        if (accountImageType == "gif") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.VISIBLE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.GONE
            if (accountImageView != "null") {
                Glide.with(activity).load(accountImageView)
                    .circleCrop()
                    .into(item.findViewById<GifImageView>(R.id.gif))
            }
        }
        if (accountImageType == "image") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.GONE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.VISIBLE

            if (accountImageView != "null") {
                Glide.with(activity).load(accountImageView)
                    .into(item.findViewById<GifImageView>(R.id.image))
            }
        }

        item.findViewById<TextView>(R.id.username).setText(username)
        item.findViewById<FloatingActionButton>(R.id.addPerson).visibility =
            View.GONE

        item.setOnLongClickListener {
            val alertBuilder =
                androidx.appcompat.app.AlertDialog.Builder(activity)
            alertBuilder.setMessage("Вы хотите удалить его из списка друзей?")
            alertBuilder.setPositiveButton("Да") { dialog, id ->

                FirebaseDatabase.getInstance().reference.child("Friends")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).child(uid)
                    .removeValue()
                dialog.cancel()


            }
            alertBuilder.setNegativeButton("Нет") { dialog, id ->
                dialog.cancel()

            }
            alertBuilder.create().show()
            true
        }
        changeTheme(
            constraintLayout = null,
            flb = listOf(
                item.findViewById<FloatingActionButton>(R.id.add_person)
            ),
            text = listOf(
                item.findViewById<TextView>(R.id.username),
                item.findViewById<TextView>(R.id.status)
            ),
            image = null,
            gif = item.findViewById<GifImageView>(R.id.gif),
            circle = null,
            cardView = listOf(item.findViewById(R.id.card_view)),
            materialToolbar = null,
            btn = null,
            context = activity,
            navigation = null,
            materialWriteRead = null,
            materialRead = null,
            nothing = null,
            nothing1 = null
        )

    }
}

class HolderAddFriend(val item: View) : RecyclerView.ViewHolder(item) {

    fun setDetails(
        username: String,
        uid: String,
        accountImageView: String,
        accountImageType: String,
        activity: Activity,
        token: String,
        status: String
    ) {
        item.findViewById<TextView>(R.id.username).setText(username)
        item.findViewById<TextView>(R.id.status).setText(status)

        if (accountImageType == "gif") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.VISIBLE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.GONE
            if (accountImageView != "null") {
                Glide.with(activity.applicationContext).load(accountImageView)
                    .circleCrop()
                    .into(item.findViewById<GifImageView>(R.id.gif))
            }

        }
        if (accountImageType == "image") {
            item.findViewById<GifImageView>(R.id.gif).visibility = View.GONE
            item.findViewById<CircleImageView>(R.id.image).visibility = View.VISIBLE

            if (accountImageView != "null") {
                Glide.with(activity.applicationContext).load(accountImageView)
                    .into(item.findViewById<GifImageView>(R.id.image))
            }
        }
        if (accountImageType == "null") {
            changeTheme(
                constraintLayout = null,
                flb = null,
                text = null,
                image = null,
                gif = item.findViewById<GifImageView>(R.id.gif),
                circle = null,
                cardView = null,
                materialToolbar = null,
                btn = null,
                context = activity,
                navigation = null,
                materialWriteRead = null,
                materialRead = null,
                nothing = null,
                nothing1 = null
            )
        }
        if (uid == FirebaseAuth.getInstance().currentUser!!.uid) {
            item.visibility = View.GONE
            item.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(uid).child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("friend").get()
            .addOnSuccessListener {
                if (it.value.toString() != "null") {

                    item.findViewById<FloatingActionButton>(R.id.addPerson).visibility =
                        View.GONE
                } else {

                    item.findViewById<FloatingActionButton>(R.id.addPerson).visibility =
                        View.VISIBLE

                }
            }
        FirebaseDatabase.getInstance().reference.child("Friends")
            .child(uid).child(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.value.toString() != "null") {

                    item.findViewById<FloatingActionButton>(R.id.addPerson).visibility =
                        View.GONE
                }
            }


        item.findViewById<TextView>(R.id.username).setText(username)

        item.findViewById<FloatingActionButton>(R.id.addPerson).setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {

                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(uid)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(it.value)

                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(uid)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("friend")
                        .setValue("waiting")


                    item.findViewById<FloatingActionButton>(R.id.addPerson).visibility =
                        View.GONE

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                        .addOnSuccessListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                sendNotification(
                                    uid,
                                    it.child("username").value.toString(),
                                    token,
                                    activity
                                )
                            }
                        }


                }

        }

        changeTheme(
            constraintLayout = null,
            flb = listOf(
                item.findViewById<FloatingActionButton>(R.id.addPerson)
            ),
            text = listOf(
                item.findViewById<TextView>(R.id.username),
                item.findViewById<TextView>(R.id.status)
            ),
            image = null,
            gif = null,
            circle = null,
            cardView = listOf(item.findViewById(R.id.card_view)),
            materialToolbar = null,
            btn = null,
            context = activity,
            navigation = null,
            materialWriteRead = null,
            materialRead = null,
            nothing = null,
            nothing1 = null
        )


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(
        uid: String,
        username: String,
        token: String,
        activity: Activity
    ) {
        val notificationSender = FcmNotificationSender(
            token,
            "Друзья",
            "$username хочет с вами подружиться",
            activity.applicationContext,
            activity
        )

        notificationSender.SendNotifications()

    }
}

