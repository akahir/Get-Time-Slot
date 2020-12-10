package UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gettimeslot.FacultySlotListActivity;
import com.example.gettimeslot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.Slot;
import Util.FacultyApi;

public class FacultySlotRecyclerAdapter extends RecyclerView.Adapter<FacultySlotRecyclerAdapter.ViewHolder> {

    private static final String TAG = "FacultySlotRecyclerAdapter";
    private Context context;
    private List<Slot> slotList;

    public FacultySlotRecyclerAdapter(Context context, List<Slot> slotList) {
        this.context = context;
        this.slotList = slotList;
    }

    @NonNull
    @Override
    public FacultySlotRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_slot_row, parent, false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultySlotRecyclerAdapter.ViewHolder holder, int position) {
        Slot slot = slotList.get(position);

        holder.time.setText(MessageFormat.format("Time:  {0} to {1}", slot.getStartTime(), slot.getEndTime()));
        holder.subject.setText(MessageFormat.format("Subject:  {0}", slot.getSubject()));
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView time, subject;
        public ImageButton deleteButton;

        public FirebaseFirestore db = FirebaseFirestore.getInstance();
        public CollectionReference collectionReference = db.collection("Slot");

        public List<String> docList = new ArrayList<>();

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            time = view.findViewById(R.id.row_timeTextView);
            subject = view.findViewById(R.id.row_subjectTextView);
            deleteButton = view.findViewById(R.id.row_deleteButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the slot

                    final int pos = getAdapterPosition();
                    final Slot st = slotList.get(pos);
                    final String sTime = st.getStartTime();
                    final String eTime = st.getEndTime();
                    final String subject = st.getSubject();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.sure_popup,null);

                    builder.setView(view);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    Button noButton = view.findViewById(R.id.popup_no_button);
                    Button yesButton = view.findViewById(R.id.popup_yes_button);

                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            collectionReference.get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {
                                                for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                                                {
                                                    if(Objects.equals(FacultyApi.getFacultyInstance().getUserId(), document.getString("UserId")) &&
                                                            Objects.equals(sTime, document.getString("StartTime")) &&
                                                            Objects.equals(eTime, document.getString("EndTime")) &&
                                                            Objects.equals(subject, document.getString("Subject"))) {
                                                        docList.add(document.getId());
                                                    }
                                                }

                                                if(!docList.isEmpty()) {
                                                    collectionReference.document(docList.get(0))
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @SuppressLint("LongLogTag")
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @SuppressLint("LongLogTag")
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                                                }
                                                            });

                                                    Toast.makeText(context,"Deleted Successfully", Toast.LENGTH_LONG).show();

                                                    slotList.remove(pos);
                                                    notifyItemRemoved(pos);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    });


                            dialog.dismiss();
                        }
                    });
                }
            });
        }

    }
}
