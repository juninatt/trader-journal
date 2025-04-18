package se.pbt.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<TradeSnapshot> snapshots = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private JournalEntry journalEntry;

    public void addSnapshot(TradeSnapshot snapshot) {
        if (snapshot != null) {
            if (snapshot.getTrade() != null && snapshot.getTrade() != this) {
                throw new IllegalStateException("Snapshot already belongs to another trade");
            }
            snapshot.setTrade(this);
            snapshots.add(snapshot);
        }
    }

    public void removeSnapshot(TradeSnapshot snapshot) {
        if (snapshots.remove(snapshot)) {
            snapshot.setTrade(null);
        }
    }
}
