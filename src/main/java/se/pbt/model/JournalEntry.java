package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Comment is required")
    private String comment;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<HoldingSnapshot> snapshots = new ArrayList<>();

    private String notes;

    public void addSnapshot(HoldingSnapshot snapshot) {
        if (snapshot != null) {
            snapshots.add(snapshot);
            snapshot.setJournalEntry(this);
        }
    }
}
