package UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gettimeslot.R;

import java.text.MessageFormat;
import java.util.List;

import Model.Slot;

public class StudentSlotRecyclerAdapter extends RecyclerView.Adapter<StudentSlotRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Slot> slotList;

    public StudentSlotRecyclerAdapter(Context context, List<Slot> slotList) {
        this.context = context;
        this.slotList = slotList;
    }

    @NonNull
    @Override
    public StudentSlotRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_slot_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentSlotRecyclerAdapter.ViewHolder holder, int position) {
        Slot slot = slotList.get(position);

        holder.time.setText(MessageFormat.format("Time:  {0} to {1}", slot.getStartTime(), slot.getEndTime()));
        holder.subject.setText(MessageFormat.format("Subject:  {0}", slot.getSubject()));
        holder.faculty.setText(MessageFormat.format("Faculty:  {0}", slot.getUserName()));
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView time, subject, faculty;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            time = view.findViewById(R.id.s_row_timeTextView);
            subject = view.findViewById(R.id.s_row_subjectTextView);
            faculty = view.findViewById(R.id.s_row_facultyTextView);
        }
    }
}
