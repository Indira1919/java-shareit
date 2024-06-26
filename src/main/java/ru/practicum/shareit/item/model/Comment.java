package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_item", referencedColumnName = "id")
    private Item item;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_author", referencedColumnName = "id")
    private User author;

    private LocalDateTime created;
}
